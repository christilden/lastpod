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

import org.lastpod.parser.ItunesDbParser;
import org.lastpod.parser.ItunesStatsParser;
import org.lastpod.parser.PlayCountsParser;
import org.lastpod.parser.TrackItemParser;

import org.lastpod.util.MiscUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

public class ModelImpl implements Model {
    private List recentlyPlayed = null;

    /**
     * Default constructor.
     */
    public ModelImpl() {
        /* Default constructor. */
    }

    public List getRecentlyPlayed() {
        return recentlyPlayed;
    }

    public void setRecentlyPlayed(List recentlyPlayed) {
        this.recentlyPlayed = recentlyPlayed;
    }

    /**
     * A utility function to clear the recently played track list.
     * @param recentlyPlayed
     */
    public void clearRecentlyPlayed() {
        recentlyPlayed = new ArrayList();
    }

    public void selectAll() {
        setupSelections(true);
    }

    public void unselectAll() {
        setupSelections(false);
    }

    private void setupSelections(boolean select) {
        for (int i = 0; i < recentlyPlayed.size(); i++) {
            TrackItem trackItem = (TrackItem) recentlyPlayed.get(i);
            trackItem.setActive(new Boolean(select));
        }
    }

    /**
     * Parses the play counts and track information from the iPod.
     */
    public void parsePlayCounts(UI userInterface) {
        Logger logger = Logger.getLogger(LastPod.class.getPackage().getName());
        Preferences fPrefs = Preferences.userRoot().node("ws/afterglo/audioPod");
        String iTunesPath = fPrefs.get("iTunes Path", "default");
        String parseVariousArtistsStr = fPrefs.get("parseVariousArtists", "1");
        String parseMultiPlayTracksStr = fPrefs.get("parseMultiPlayTracks", "1");
        boolean parseVariousArtists = parseVariousArtistsStr.equals("1") ? true : false;
        String variousArtistsString = fPrefs.get("variousArtistsString", "Various Artists");
        String[] splitVariousArtistStrings = splitVariousArtistsString(variousArtistsString);
        boolean parseMultiPlayTracks = parseMultiPlayTracksStr.equals("1") ? true : false;

        if (iTunesPath.equals("default")) {
            logger.log(Level.INFO, LastPod.NO_PREFS_ERROR);

            return;
        }

        /* Initialize the history file. */
        History.getInstance(iTunesPath);

        boolean isShuffle = ItunesStatsParser.isIpodShuffle(iTunesPath);

        ItunesDbParser itunesDbParser =
            new ItunesDbParser(iTunesPath, parseVariousArtists, splitVariousArtistStrings, isShuffle);

        TrackItemParser playCountsParser = null;

        /* If the iPod is a Shuffle, use the iPod shuffle parser. Otherwise
         * use the non-shuffle parser. */
        if (isShuffle) {
            playCountsParser = new ItunesStatsParser(iTunesPath, parseMultiPlayTracks);
        } else {
            playCountsParser = new PlayCountsParser(iTunesPath, parseMultiPlayTracks);
        }

        DbReader reader = new DbReader(itunesDbParser, playCountsParser);

        try {
            reader.parse();
            recentlyPlayed = reader.getRecentplays();
            userInterface.newTrackListAvailable(recentlyPlayed);
        } catch (Exception e) {
            StackTraceElement[] trace = e.getStackTrace();

            for (int i = 0; i < trace.length; i++) {
                if (trace[i].getClassName().startsWith("org.lastpod")) {
                    logger.log(Level.SEVERE, trace[i].toString());
                }
            }

            logger.log(Level.SEVERE, e.toString());
        }
    }

    /**
     * Submits the tracks to Last.fm
     * @param userInterface  The application's user interface.
     * @param online  submission type:
     *                  true - online, false - offline (to Last.fm client cache)
     * @return  A status message upon completion.
     */
    public Object submitTracks(UI userInterface, boolean online) {
        Logger logger = Logger.getLogger(LastPod.class.getPackage().getName());
        Preferences fPrefs = Preferences.userRoot().node("ws/afterglo/audioPod");
        String username = fPrefs.get("Username", "default");
        String password = fPrefs.get("Password", "default");
        String encryptedPassword = fPrefs.get("encryptedPassword", "default");

        if (!password.equals("default")) {
            encryptedPassword = MiscUtilities.md5DigestPassword(password);
            fPrefs.put("encryptedPassword", encryptedPassword);
            fPrefs.remove("Password");

            String message =
                "Your password was stored unencrypted on your system."
                + " This version of LastPod has encrypted this password for future usage.";
            JOptionPane.showMessageDialog(userInterface.getFrame(), message);

            logger.log(Level.WARNING, message);
        }

        String encryptedDefault = MiscUtilities.md5DigestPassword("default");

        if (username.equals("default") && encryptedPassword.equals(encryptedDefault)) {
            logger.log(Level.INFO, LastPod.NO_PREFS_ERROR);

            return LastPod.NO_PREFS_ERROR;
        }

        String backupUrl = fPrefs.get("backupUrl", "");

        String submitCachePath = fPrefs.get("submitCachePath", "");

        try {
            Scrobbler scrobbler =
                new Scrobbler(username, encryptedPassword, backupUrl, submitCachePath);

            List activeRecentPlayed = onlyActiveTrackItems(recentlyPlayed);
            List inactiveRecentPlayed = onlyInactiveTrackItems(recentlyPlayed);

            scrobbler.setChunkProgress(userInterface);
            scrobbler.setTracksToSubmit(activeRecentPlayed);
            scrobbler.addInactiveToHistories(inactiveRecentPlayed);

            if (online) {
                scrobbler.handshake();
                scrobbler.submitTracks();
            } else {
                scrobbler.submitTracksToCache();
            }

            /* Refresh track list. */
            recentlyPlayed = new ArrayList();
            parsePlayCounts(userInterface);
            userInterface.newTrackListAvailable(recentlyPlayed);
        } catch (Exception e) {
            StackTraceElement[] trace = e.getStackTrace();

            for (int i = 0; i < trace.length; i++) {
                if (trace[i].getClassName().startsWith("org.lastpod")) {
                    logger.log(Level.SEVERE, trace[i].toString());
                }
            }

            logger.log(Level.SEVERE, e.toString());
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
        }

        return "Success";
    }

    private List onlyActiveTrackItems(List recentPlayed) {
        return filterTrackItems(recentPlayed, true);
    }

    private List onlyInactiveTrackItems(List recentPlayed) {
        return filterTrackItems(recentPlayed, false);
    }

    private List filterTrackItems(List recentPlayed, boolean filterActive) {
        List filteredRecentPlayed = new ArrayList();

        for (int i = 0; i < recentPlayed.size(); i++) {
            TrackItem trackItem = (TrackItem) recentPlayed.get(i);

            boolean trackActive = trackItem.isActive().booleanValue();

            if (trackActive && filterActive) {
                filteredRecentPlayed.add(trackItem);
            } else if (!trackActive && !filterActive) {
                filteredRecentPlayed.add(trackItem);
            }
        }

        return filteredRecentPlayed;
    }

    /**
     * Splits a ; seperated String of various artist strings.
     * @param variousArtistsString  The String to split
     * @return  A split and trimmed String array.
     */
    public static String[] splitVariousArtistsString(String variousArtistsString) {
        String[] splitVariousArtistsString = variousArtistsString.split(";");

        for (int i = 0; i < splitVariousArtistsString.length; i++) {
            splitVariousArtistsString[i] = splitVariousArtistsString[i].trim();
        }

        return splitVariousArtistsString;
    }
}
