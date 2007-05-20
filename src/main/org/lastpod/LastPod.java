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

import org.lastpod.util.MiscUtilities;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

/**
 * The LastPod controller.
 * @author muti
 * @author Chris Tilden
 * @version $Id$
 */
public class LastPod {
    public static UI UI;
    public static List recentplayed; //parsed using DbReader class
    private static Scrobbler scrobbler;
    private static Logger logger;
    private final static String NO_PREFS_ERROR =
        "You have not setup your preferences.\n"
        + "Please click Preferences below to configure the location of "
        + "your iTunesDB (it's on your iPod's drive) and your AudioScrobbler "
        + "username and password.";

    /**
     * Loads the recent played information from the iPod and constructs the
     * GUI to display it.
     */
    private static void load() {
        recentplayed = new ArrayList();
        UI = new UI();
        UI.buildUI();

        logger = Logger.getLogger(LastPod.class.getPackage().getName());
        logger.setLevel(Level.ALL);
        logger.addHandler(new LogHandler());

        parsePlayCounts();

        UI.makeVisable();
    }

    /**
     * Parses the play counts and track information from the iPod.
     */
    public static void parsePlayCounts() {
        Preferences fPrefs = Preferences.userRoot().node("ws/afterglo/audioPod");
        String iTunesPath = fPrefs.get("iTunes Path", "default");
        String parseVariousArtistsStr = fPrefs.get("parseVariousArtists", "1");
        boolean parseVariousArtists = parseVariousArtistsStr.equals("1") ? true : false;

        if (iTunesPath.equals("default")) {
            logger.log(Level.INFO, NO_PREFS_ERROR);

            return;
        }

        DbReader reader = new DbReader(iTunesPath, parseVariousArtists);

        try {
            reader.parse();
            recentplayed = reader.getRecentplays();
            UI.newTrackListAvailable();
        } catch (IOException e) {
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
     * @return  A status message upon completion.
     */
    public static Object submitTracks() {
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
            JOptionPane.showMessageDialog(UI.getFrame(), message);

            logger = Logger.getLogger(LastPod.class.getPackage().getName());
            logger.log(Level.WARNING, message);
        }

        String encryptedDefault = MiscUtilities.md5DigestPassword("default");

        if (username.equals("default") && encryptedPassword.equals(encryptedDefault)) {
            logger.log(Level.INFO, NO_PREFS_ERROR);

            return NO_PREFS_ERROR;
        }

        String backupUrl = fPrefs.get("backupUrl", "");

        try {
            scrobbler = new Scrobbler(username, encryptedPassword, backupUrl);

            List activeRecentPlayed = onlyActiveTrackItems(recentplayed);
            List inactiveRecentPlayed = onlyInactiveTrackItems(recentplayed);

            scrobbler.setChunkProgress(UI);
            scrobbler.setTracksToSubmit(activeRecentPlayed);
            scrobbler.handshake();
            scrobbler.submitTracks();
            scrobbler.addHistories(activeRecentPlayed, inactiveRecentPlayed);

            /* Refresh track list. */
            UI.newTrackListAvailable();
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

    private static List onlyActiveTrackItems(List recentPlayed) {
        return filterTrackItems(recentPlayed, true);
    }

    private static List onlyInactiveTrackItems(List recentPlayed) {
        return filterTrackItems(recentPlayed, false);
    }

    private static List filterTrackItems(List recentPlayed, boolean filterActive) {
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

    public static void selectAll() {
        setupSelections(true);
    }

    public static void unselectAll() {
        setupSelections(false);
    }

    private static void setupSelections(boolean select) {
        for (int i = 0; i < recentplayed.size(); i++) {
            TrackItem trackItem = (TrackItem) recentplayed.get(i);
            trackItem.setActive(new Boolean(select));
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    load();
                }
            });
    }
}
