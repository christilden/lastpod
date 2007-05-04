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

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

/**
 * @author muti
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

    private static void Load() {
        LastPod.recentplayed = new ArrayList();
        LastPod.UI = new UI();
        LastPod.UI.buildUI();

        logger = Logger.getLogger(LastPod.class.getPackage().getName());
        logger.setLevel(Level.ALL);
        logger.addHandler(new LogHandler());

        LastPod.ParsePlayCounts();

        LastPod.UI.makeVisable();
    }

    public static void ParsePlayCounts() {
        Preferences fPrefs = Preferences.userRoot().node("ws/afterglo/audioPod");
        String iTunesPath = fPrefs.get("iTunes Path", "default");

        if (iTunesPath.equals("default")) {
            logger.log(Level.INFO, LastPod.NO_PREFS_ERROR);

            return;
        }

        DbReader reader = new DbReader(iTunesPath);

        try {
            reader.parse();
            LastPod.recentplayed = reader.getRecentplays();
            LastPod.UI.newTrackListAvailable();
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

    public static void SubmitTracks() {
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
            logger.log(Level.INFO, LastPod.NO_PREFS_ERROR);

            return;
        }

        String backupUrl = fPrefs.get("backupUrl", "");

        try {
            LastPod.scrobbler = new Scrobbler(username, encryptedPassword, backupUrl);

            List activeRecentPlayed = onlyActiveTrackItems(recentplayed);
            List inactiveRecentPlayed = onlyInactiveTrackItems(recentplayed);
            LastPod.scrobbler.handshake(activeRecentPlayed);
            LastPod.scrobbler.submittracks(activeRecentPlayed);
            LastPod.scrobbler.addHistories(activeRecentPlayed, inactiveRecentPlayed);

            /* Clear recent track list. */
            LastPod.recentplayed = inactiveRecentPlayed;
            LastPod.UI.newTrackListAvailable();
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
                    LastPod.Load();
                }
            });
    }
}
