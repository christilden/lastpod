/*
 * LastPod is an application used to publish one's iPod play counts to Last.fm.
 * Copyright (C) 2007  Chris Tilden
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.lastpod;

import org.lastpod.chunk.Chunk;
import org.lastpod.chunk.ChunkUtil;

import org.lastpod.util.IoUtils;
import org.lastpod.util.MiscUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.FailedLoginException;

/**
 * @author muti
 * @version $Id$
 */
public class Scrobbler {
    /**
     * AudioScrobbler suggests a maximum number of tracks per chunk.
     */
    private static final int MAX_TRACKS_PER_CHUNK = 10;

    /**
     * The minimum length (in seconds) of a track that meets Last.fm guidelines.
     */
    private static final int MIN_TRACK_SECONDS = 30;
    private String username;
    private String encryptedPassword;
    private String backupUrl;
    private String challenge;
    private String submitHost;
    private Integer submitPort;
    private String submitUrl;

    /**
     * Stores the chunks of tracks to be submitted.
     */
    private List trackChunks;

    /**
     * Displays the submission progress as this class updates it.
     */
    private ChunkProgress chunkProgress;

    /**
     * The number of seconds to pause between submissions. Only if the server
     * asks to do so.
     */
    private int interval = 0;
    private Logger logger;

    public Scrobbler(String username, String encryptedPassword, String backupUrl) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.backupUrl = backupUrl;
        logger = Logger.getLogger(getClass().getPackage().getName());
    }

    /**
     * Sets the object that displays submission progress.  This object updates
     * the progress as it is processing.
     * @param chunkProgress  The object that displays submission progress.
     */
    public void setChunkProgress(ChunkProgress chunkProgress) {
        this.chunkProgress = chunkProgress;
    }

    /**
     * Sets the tracks that are submitted.
     * @param recentPlayed  A list of tracks to submit.
     */
    public void setTracksToSubmit(final List recentPlayed) {
        if (recentPlayed.size() == 0) {
            throw new RuntimeException("No tracks to submit");
        }

        /* Converts the recentPlayed List into a List of Chunk objects.  Each
         * chunk stores at most 10 tracks.  Each chunk will be submitted to
         * Last.fm individually, per their guidelines.
         */
        trackChunks = ChunkUtil.createChunks(recentPlayed, MAX_TRACKS_PER_CHUNK);

        /* Add 1 because the handshake will also be included in the progress. */
        chunkProgress.setNumberOfChunks(trackChunks.size() + 1);
    }

    public void handshake()
            throws UnsupportedEncodingException, MalformedURLException, IOException,
                FailedLoginException {
        if (trackChunks.size() == 0) {
            throw new RuntimeException("No tracks to submit");
        }

        String statusMessage = "Beginning Handshake";
        chunkProgress.setSubmitStatusMessage(statusMessage);
        logger.log(Level.INFO, statusMessage);

        String args = "?hs=true&p=1.1&c=apd&v=0.1&u=" + URLEncoder.encode(username, "UTF-8");
        URL url = new URL("http://post.audioscrobbler.com/" + args);

        statusMessage = "Handshaking to URL: " + url.toString();
        chunkProgress.setSubmitStatusMessage(statusMessage);
        logger.log(Level.FINE, statusMessage);

        HttpURLConnection c = (HttpURLConnection) url.openConnection();

        c.setDoInput(true);
        c.setRequestMethod("GET");
        c.setUseCaches(false);
        c.setRequestProperty("Connection", "close");
        c.connect();

        if (c.getResponseCode() != 200) {
            throw new RuntimeException("Invalid HTTP return code");
        }

        BufferedReader breader = new BufferedReader(new InputStreamReader(c.getInputStream()));

        String content = null;
        String buffer = null;

        while ((buffer = breader.readLine()) != null) {
            if (content != null) {
                content += (buffer + "\n");
            } else {
                content = buffer + "\n";
            }
        }

        logger.log(Level.FINE, "Received from server:\n" + content);

        if ((content == null) || (content.length() == 0)) {
            statusMessage = "Invalid response received from AudioScrobbler";
            chunkProgress.setSubmitStatusMessage(statusMessage);
            throw new RuntimeException(statusMessage);
        }

        String[] lines = content.split("\n");

        if ((lines[0].length() >= 6) && lines[0].substring(0, 6).equals("FAILED")) {
            statusMessage = lines[0].substring(7);
            chunkProgress.setSubmitStatusMessage(statusMessage);
            throw new RuntimeException(statusMessage);
        }

        if ((lines[0].length() >= 7) && lines[0].substring(0, 7).equals("BADUSER")) {
            statusMessage = "Invalid Username";
            chunkProgress.setSubmitStatusMessage(statusMessage);
            throw new FailedLoginException(statusMessage);
        }

        if ((lines[0].length() >= 6) && lines[0].substring(0, 6).equals("UPDATE")) {
            statusMessage = "Update your client:" + lines[0].substring(7);
            chunkProgress.setSubmitStatusMessage(statusMessage);
            throw new RuntimeException(statusMessage);
        }

        /* Sets the interval, if it is present in the response. */
        if ((lines.length >= 4) && (lines[3].length() >= 10)) {
            String wait = lines[3].substring(9);
            interval = Integer.parseInt(wait);
        }

        Pattern p = Pattern.compile("http://(.*):(\\d+)(.*)");
        Matcher m = p.matcher(lines[2]);

        if (m.matches()) {
            submitHost = m.group(1);
            submitPort = new Integer(m.group(2));
            submitUrl = m.group(3);
            logger.log(Level.FINE, "Set submithost to: " + submitHost);
            logger.log(Level.FINE, "Set submitport to: " + submitPort);
            logger.log(Level.FINE, "Set submiturl to: " + submitUrl);
        } else {
            throw new RuntimeException("Invalid POST URL returned, unable to continue");
        }

        challenge = lines[1];

        /* Displays some progress update once the handshake is completed. */
        chunkProgress.updateCurrentChunk(1);

        statusMessage = "Handshake completed";
        chunkProgress.setSubmitStatusMessage(statusMessage);
        logger.log(Level.INFO, statusMessage);
    }

    public void submitTracks()
            throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException,
                IOException, FailedLoginException {
        String statusMessage = "Submitting tracks...";
        chunkProgress.setSubmitStatusMessage(statusMessage);
        logger.log(Level.INFO, statusMessage);

        if (trackChunks.size() == 0) {
            statusMessage = "No tracks to submit";
            chunkProgress.setSubmitStatusMessage(statusMessage);
            throw new RuntimeException(statusMessage);
        }

        MessageDigest md = MessageDigest.getInstance("MD5");
        String md5pass = encryptedPassword + challenge;
        String md5chal = MiscUtilities.hexEncode(md.digest(md5pass.getBytes()));
        String urlEncodedUsername = URLEncoder.encode(username, "UTF-8");
        String urlEncodedChallange = URLEncoder.encode(md5chal, "UTF-8");

        Chunk chunk = null;

        for (int i = 0; i < trackChunks.size(); i++) {
            pauseIfRequired();

            chunk = (Chunk) trackChunks.get(i);

            String queryString = "u=" + urlEncodedUsername + "&" + "s=" + urlEncodedChallange;

            int tracknum = 0;

            for (int j = 0; j < chunk.getChunkSize(); j++) {
                TrackItem track = (TrackItem) chunk.getContent().get(j);

                /* Per Last.fm guidelines; do not submit tracks that are less
                 * than 30 characters in length.
                 */
                if (track.getLength() < MIN_TRACK_SECONDS) {
                    continue;
                }

                queryString += buildTrackQueryString(track, tracknum);

                tracknum++;
            }

            String content = null;

            /* If a backup URL is specified then two submits will take place.  A
             * backup URL can be used to send your information to another server.
             */
            if ((backupUrl != null) && !backupUrl.equals("")) {
                content = fetchContent(backupUrl, queryString);
                logger.log(Level.FINE, "Received from server:\n" + content);
            }

            String urlString = "http://" + submitHost + ":" + submitPort + submitUrl;
            content = fetchContent(urlString, queryString);

            String[] lines = content.split("\n");

            /* Sets the interval, if it is present in the response. */
            if ((lines.length >= 2) && (lines[1].length() >= 10)) {
                String wait = lines[1].substring(9);
                interval = Integer.parseInt(wait);
            }

            if ((lines[0].length() >= 6) && lines[0].substring(0, 6).equals("FAILED")) {
                throw new RuntimeException(lines[0].substring(7));
            }

            if ((lines[0].length() >= 7) && lines[0].substring(0, 7).equals("BADAUTH")) {
                throw new FailedLoginException("Invalid username/password");
            }

            if ((lines[0].length() >= 2) && !lines[0].substring(0, 2).equals("OK")) {
                throw new RuntimeException("Unknown error submitting tracks");
            }

            /* The chunk is successfully written to last.fm. Makes sure the
             * tracks are marked as inactive.  Writes the history file.
             * This is done after each chunk because if the next chunk fails
             * the history file should reflect where the failure occurred.
             */
            for (int j = 0; j < chunk.getChunkSize(); j++) {
                TrackItem track = (TrackItem) chunk.getContent().get(j);
                track.setActive(Boolean.FALSE);
            }

            addHistories(chunk.getContent());

            /* Add 2 to progress.  1 because chunk progress starts at 1, whereas
             * this for-loop is zero indexed.  1 because the handshake is also
             * part of the progress.
             */
            chunkProgress.updateCurrentChunk(i + 2);
        }

        chunkProgress.setSubmitStatusMessage("Done. You may now sync your iPod.");
        logger.log(Level.INFO, "Tracks submitted");
        logger.log(Level.INFO,
            "You may now sync your iPod with your music management software "
            + "or delete 'Play Counts' from the iTunes folder!");

        chunkProgress.setCompletionStatus(true);
    }

    /**
     * Last.fm will informs this client if it needs to pause.  This occurs when
     * Last.fm is extremely busy.
     *
     * This function will pause the required amount of time if it is needed.
     *
     */
    private void pauseIfRequired() {
        if (interval != 0) {
            try {
                Thread.sleep(interval * 1000);
            } catch (InterruptedException e) {
                /* If interrupted it will simply submit early.  Therefore
                 * it will not fail if this occurs.
                 */
            }
        }
    }

    /**
     * Builds the query string for the given <code>TrackItem</code> and track
     * number.
     * @param track  The track to build a query string of.
     * @param trackNum  The number of the track in the submission.
     * @return  The complete query string for the given track.
     * @throws UnsupportedEncodingException  Thrown if errors occur.
     */
    private String buildTrackQueryString(TrackItem track, int trackNum)
            throws UnsupportedEncodingException {
        StringBuffer trackQueryString = new StringBuffer();

        //TODO: Is all this UTF-8 encoding needed?
        String artistutf8 = new String(track.getArtist().getBytes("UTF-8"), "UTF-8");
        String trackutf8 = new String(track.getTrack().getBytes("UTF-8"), "UTF-8");
        String albumutf8 = new String(track.getAlbum().getBytes("UTF-8"), "UTF-8");
        String trackString = Long.toString(track.getLength());
        Date date = new Date(track.getLastplayed() * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT:00"));

        String dateString = format.format(date);

        trackQueryString.append("&a[" + trackNum + "]=" + URLEncoder.encode(artistutf8, "UTF-8"));
        trackQueryString.append("&t[" + trackNum + "]=" + URLEncoder.encode(trackutf8, "UTF-8"));
        trackQueryString.append("&b[" + trackNum + "]=" + URLEncoder.encode(albumutf8, "UTF-8"));
        trackQueryString.append("&m[" + trackNum + "]=");
        trackQueryString.append("&l[" + trackNum + "]=" + URLEncoder.encode(trackString, "UTF-8"));
        trackQueryString.append("&i[" + trackNum + "]=" + URLEncoder.encode(dateString, "UTF-8"));

        return trackQueryString.toString();
    }

    /**
     * Creates the histories and writes them to a file.
     * @param activeRecentPlayed  The list of active recently played tracks.
     */
    public void addHistories(List activeRecentPlayed) {
        for (int i = 0; i < activeRecentPlayed.size(); i++) {
            TrackItem track = (TrackItem) activeRecentPlayed.get(i);
            History.getInstance(null).addhistory(track.getLastplayed());
        }

        History.getInstance(null).write();
    }

    /**
     * Adds inactive recent played tracks to the histories file.  This is done
     * so they will be preserved in the histories.
     * @param inactiveRecentPlayed  The list of inactive recently played tracks.
     */
    public void addInactiveToHistories(List inactiveRecentPlayed) {
        for (int i = 0; i < inactiveRecentPlayed.size(); i++) {
            TrackItem track = (TrackItem) inactiveRecentPlayed.get(i);

            if (History.getInstance(null).isInHistory(track.getLastplayed())) {
                History.getInstance(null).addhistory(track.getLastplayed());
            }
        }
    }

    /**
     * Fetches the HTTP content given a URL String and a query String.
     * @param urlString  The URL to fetch from.
     * @param queryString  The query String to submit.
     * @return  The content returned from the request.
     * @throws MalformedURLException  Thrown if exceptions occur.
     * @throws IOException  Thrown if exceptions occur.
     * @throws ProtocolException  Thrown if exceptions occur.
     */
    private String fetchContent(String urlString, String queryString)
            throws MalformedURLException, IOException, ProtocolException {
        String content = null;
        URL url = new URL(urlString);
        logger.log(Level.FINE, "Submitting tracks to URL: " + url.toString());

        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("POST");
        c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        c.setRequestProperty("Content-Length", new Integer(queryString.length()).toString());
        c.setRequestProperty("Connection", "close");
        c.setDoInput(true);
        c.setDoOutput(true);
        c.setUseCaches(false);
        c.connect();

        logger.log(Level.FINE, "POST query string:\n" + queryString);

        OutputStream out = null;
        OutputStreamWriter writer = null;
        InputStream in = null;
        Reader reader = null;
        BufferedReader bufferedReader = null;

        try {
            out = c.getOutputStream();
            writer = new OutputStreamWriter(out);
            writer.write(queryString);
            writer.flush();

            IoUtils.cleanup(null, writer);
            IoUtils.cleanup(null, out);

            if (c.getResponseCode() != 200) {
                throw new RuntimeException("Invalid HTTP return code");
            }

            in = c.getInputStream();
            reader = new InputStreamReader(in);
            bufferedReader = new BufferedReader(reader);

            String buffer = null;

            while ((buffer = bufferedReader.readLine()) != null) {
                if (content != null) {
                    content += (buffer + "\n");
                } else {
                    content = buffer + "\n";
                }
            }
        } finally {
            IoUtils.cleanup(null, writer);
            IoUtils.cleanup(null, out);
            IoUtils.cleanup(bufferedReader, null);
            IoUtils.cleanup(reader, null);
            IoUtils.cleanup(in, null);
        }

        logger.log(Level.FINE, "Received from server:\n" + content);

        if ((content == null) || (content.length() == 0)) {
            throw new RuntimeException("Invalid response received from AudioScrobbler");
        }

        return content;
    }
}
