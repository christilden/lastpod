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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
    private String challenge;
    private String submithost;
    private Integer submitport;
    private String submiturl;
    private Logger logger;

    public Scrobbler(String username, String encryptedPassword) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.logger = Logger.getLogger(this.getClass().getPackage().getName());
    }

    public void handshake(List recentplayed)
            throws UnsupportedEncodingException, MalformedURLException, IOException,
                FailedLoginException {
        if (recentplayed.size() == 0) {
            throw new RuntimeException("No tracks to submit");
        }

        this.logger.log(Level.INFO, "Beginning Handshake");

        String args = "?hs=true&p=1.1&c=apd&v=0.1&u=" + URLEncoder.encode(this.username, "UTF-8");
        URL url = new URL("http://post.audioscrobbler.com/" + args);
        this.logger.log(Level.FINE, "Handshaking to URL: " + url.toString());

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

        this.logger.log(Level.FINE, "Received from server:\n" + content);

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
            this.submithost = m.group(1);
            this.submitport = new Integer(m.group(2));
            this.submiturl = m.group(3);
            this.logger.log(Level.FINE, "Set submithost to: " + this.submithost);
            this.logger.log(Level.FINE, "Set submitport to: " + this.submitport);
            this.logger.log(Level.FINE, "Set submiturl to: " + this.submiturl);
        } else {
            throw new RuntimeException("Invalid POST URL returned, unable to continue");
        }

        this.challenge = lines[1];

        this.logger.log(Level.INFO, "Handshake completed");
    }

    public void submittracks(List recentplayed)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException,
                IOException, FailedLoginException {
        this.logger.log(Level.INFO, "Submitting tracks...");

        if (recentplayed.size() == 0) {
            throw new RuntimeException("No tracks to submit");
        }

        MessageDigest md = MessageDigest.getInstance("MD5");
        String md5pass = encryptedPassword + this.challenge;
        String md5chal = MiscUtilities.hexEncode(md.digest(md5pass.getBytes()));

        String querystring =
            "u=" + URLEncoder.encode(this.username) + "&" + "s=" + URLEncoder.encode(md5chal) + "&";

        int tracknum = 0;

        for (int i = 0; i < recentplayed.size(); i++) {
            TrackItem track = (TrackItem) recentplayed.get(i);

            if (track.getLength() < 30) {
                continue;
            }

            String artistutf8 = new String(track.getArtist().getBytes("UTF-8"), "UTF-8");
            String trackutf8 = new String(track.getTrack().getBytes("UTF-8"), "UTF-8");
            String albumutf8 = new String(track.getAlbum().getBytes("UTF-8"), "UTF-8");
            Date date = new Date(track.getLastplayed() * 1000);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("GMT:00"));

            String datestring = format.format(date);

            querystring += ("a[" + tracknum + "]=" + URLEncoder.encode(artistutf8, "UTF-8") + "&");
            querystring += ("t[" + tracknum + "]=" + URLEncoder.encode(trackutf8, "UTF-8") + "&");
            querystring += ("b[" + tracknum + "]=" + URLEncoder.encode(albumutf8, "UTF-8") + "&");
            querystring += ("m[" + tracknum + "]=" + "&");
            querystring += ("l[" + tracknum + "]="
            + URLEncoder.encode(new Long(track.getLength()).toString(), "UTF-8") + "&");
            querystring += ("i[" + tracknum + "]=" + URLEncoder.encode(datestring, "UTF-8") + "&");

            tracknum++;
        }

        querystring = querystring.substring(0, querystring.length() - 1); //trim last &

        URL url = new URL("http://" + this.submithost + ":" + this.submitport + this.submiturl);
        this.logger.log(Level.FINE, "Submitting tracks to URL: " + url.toString());

        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("POST");
        c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        c.setRequestProperty("Content-Length", new Integer(querystring.length()).toString());
        c.setRequestProperty("Connection", "close");
        c.setDoInput(true);
        c.setDoOutput(true);
        c.setUseCaches(false);
        c.connect();

        this.logger.log(Level.FINE, "POST query string:\n" + querystring);

        OutputStreamWriter wr = new OutputStreamWriter(c.getOutputStream());
        wr.write(querystring);
        wr.flush();
        wr.close();

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

        this.logger.log(Level.FINE, "Received from server:\n" + content);

        if ((content == null) || (content.length() == 0)) {
            throw new RuntimeException("Invalid response received from AudioScrobbler");
        }

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

        this.logger.log(Level.INFO, "Tracks submitted");
        this.logger.log(Level.INFO,
            "You must now sync your iPod with your music management software "
            + "or delete 'Play Counts' from the iTunes folder!");
        
        
        for (int i = 0; i < recentplayed.size(); i++) {
            TrackItem track = (TrackItem) recentplayed.get(i);            
            History.getInstance().addhistory(track.getLastplayed());            
        }
        
        History.getInstance().write();
    }
}
