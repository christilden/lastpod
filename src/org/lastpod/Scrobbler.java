/*
 * LastPod is an application used to publish one's iPod play counts to Last.fm.
 * Copyright (C) 2007  muti, Chris Tilden
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
 * package org.lastpod;
 */
package org.lastpod;

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
    private String username;
    private String encryptedPassword;
    private String backupUrl;
    private String challenge;
    private String submitHost;
    private Integer submitPort;
    private String submitUrl;
    private Logger logger;

    public Scrobbler(String username, String encryptedPassword, String backupUrl) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.backupUrl = backupUrl;
        logger = Logger.getLogger(getClass().getPackage().getName());
    }

    public void handshake(List recentPlayed)
            throws UnsupportedEncodingException, MalformedURLException, IOException,
                FailedLoginException {
        if (recentPlayed.size() == 0) {
            throw new RuntimeException("No tracks to submit");
        }

        logger.log(Level.INFO, "Beginning Handshake");

        String args = "?hs=true&p=1.1&c=apd&v=0.1&u=" + URLEncoder.encode(username, "UTF-8");
        URL url = new URL("http://post.audioscrobbler.com/" + args);
        logger.log(Level.FINE, "Handshaking to URL: " + url.toString());

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
            throw new RuntimeException("Invalid response received from AudioScrobbler");
        }

        String[] lines = content.split("\n");

        if ((lines[0].length() >= 6) && lines[0].substring(0, 6).equals("FAILED")) {
            throw new RuntimeException(lines[0].substring(7));
        }

        if ((lines[0].length() >= 7) && lines[0].substring(0, 7).equals("BADUSER")) {
            throw new FailedLoginException("Invalid Username");
        }

        if ((lines[0].length() >= 6) && lines[0].substring(0, 6).equals("UPDATE")) {
            throw new RuntimeException("Update your client:" + lines[0].substring(7));
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

        logger.log(Level.INFO, "Handshake completed");
    }

    public void submitTracks(final List recentPlayed)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException,
                IOException, FailedLoginException {
        logger.log(Level.INFO, "Submitting tracks...");

        if (recentPlayed.size() == 0) {
            throw new RuntimeException("No tracks to submit");
        }

        MessageDigest md = MessageDigest.getInstance("MD5");
        String md5pass = encryptedPassword + challenge;
        String md5chal = MiscUtilities.hexEncode(md.digest(md5pass.getBytes()));
        String urlEncodedUsername = URLEncoder.encode(username, "UTF-8");
        String urlEncodedChallange = URLEncoder.encode(md5chal, "UTF-8");

        String queryString = "u=" + urlEncodedUsername + "&" + "s=" + urlEncodedChallange;

        int tracknum = 0;

        for (int i = 0; i < recentPlayed.size(); i++) {
            TrackItem track = (TrackItem) recentPlayed.get(i);

            if (track.getLength() < 30) {
                continue;
            }

            //TODO: Is all this UTF-8 encoding needed?
            String artistutf8 = new String(track.getArtist().getBytes("UTF-8"), "UTF-8");
            String trackutf8 = new String(track.getTrack().getBytes("UTF-8"), "UTF-8");
            String albumutf8 = new String(track.getAlbum().getBytes("UTF-8"), "UTF-8");
            String trackString = Long.toString(track.getLength());
            Date date = new Date(track.getLastplayed() * 1000);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("GMT:00"));

            String datestring = format.format(date);

            queryString += ("&a[" + tracknum + "]=" + URLEncoder.encode(artistutf8, "UTF-8"));
            queryString += ("&t[" + tracknum + "]=" + URLEncoder.encode(trackutf8, "UTF-8"));
            queryString += ("&b[" + tracknum + "]=" + URLEncoder.encode(albumutf8, "UTF-8"));
            queryString += ("&m[" + tracknum + "]=");
            queryString += ("&l[" + tracknum + "]=" + URLEncoder.encode(trackString, "UTF-8"));
            queryString += ("&i[" + tracknum + "]=" + URLEncoder.encode(datestring, "UTF-8"));

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

        if ((lines[0].length() >= 6) && lines[0].substring(0, 6).equals("FAILED")) {
            throw new RuntimeException(lines[0].substring(7));
        }

        if ((lines[0].length() >= 7) && lines[0].substring(0, 7).equals("BADAUTH")) {
            throw new FailedLoginException("Invalid username/password");
        }

        if ((lines[0].length() >= 2) && !lines[0].substring(0, 2).equals("OK")) {
            throw new RuntimeException("Unknown error submitting tracks");
        }

        logger.log(Level.INFO, "Tracks submitted");
        logger.log(Level.INFO,
            "You must now sync your iPod with your music management software "
            + "or delete 'Play Counts' from the iTunes folder!");
    }

    /**
     * Creates the histories and writes them to a file.
     * @param activeRecentPlayed  The list of active recently played tracks.
     * @param inactiveRecentPlayed  The list of inactive recently played tracks.
     */
    public void addHistories(List activeRecentPlayed, List inactiveRecentPlayed) {
        for (int i = 0; i < activeRecentPlayed.size(); i++) {
            TrackItem track = (TrackItem) activeRecentPlayed.get(i);
            History.getInstance().addhistory(track.getLastplayed());
        }

        for (int i = 0; i < inactiveRecentPlayed.size(); i++) {
            TrackItem track = (TrackItem) inactiveRecentPlayed.get(i);

            if (History.getInstance().isInHistory(track.getLastplayed())) {
                History.getInstance().addhistory(track.getLastplayed());
            }
        }

        History.getInstance().write();
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
