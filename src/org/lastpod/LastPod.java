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
 *
 */
public class LastPod {
    public static UI UI;
    public static List recentplayed; //parsed using DbReader class
    private static Scrobbler scrobbler;
    private static Logger logger;
    private static String NoPrefsError =
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
            logger.log(Level.INFO, LastPod.NoPrefsError);

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
                if (trace[i].getClassName().startsWith("ws.afterglo.audioPod")) {
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

        if (username.equals("default") && password.equals("default")) {
            logger.log(Level.INFO, LastPod.NoPrefsError);

            return;
        }

        try {
            LastPod.scrobbler = new Scrobbler(username, password);

            List activeRecentPlayed = onlyActiveTrackItems(recentplayed);
            LastPod.scrobbler.handshake(activeRecentPlayed);
            LastPod.scrobbler.submittracks(activeRecentPlayed);
            LastPod.recentplayed = onlyInactiveTrackItems(recentplayed); //clear recent track list
            LastPod.UI.newTrackListAvailable();
        } catch (Exception e) {
            StackTraceElement[] trace = e.getStackTrace();

            for (int i = 0; i < trace.length; i++) {
                if (trace[i].getClassName().startsWith("ws.afterglo.audioPod")) {
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
