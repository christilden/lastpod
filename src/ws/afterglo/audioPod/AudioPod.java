package ws.afterglo.audioPod;

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
public class AudioPod {
    public static UI		UI;
    public static List	recentplayed; //parsed using DbReader class
    public static Scrobbler	scrobbler;
    
    public static Logger    logger;
    public static String    NoPrefsError = "You have not setup your preferences.\n" +
                                           "Please click Preferences below to configure the location of " +
                                           "your iTunesDB (it's on your iPod's drive) and your AudioScrobbler " +
                                           "username and password.";
    
    public static void Load() {
        AudioPod.recentplayed = new ArrayList();
        AudioPod.UI = new UI();
        AudioPod.UI.buildUI();
        
        logger = Logger.getLogger(AudioPod.class.getPackage().getName());
        logger.setLevel(Level.ALL);
        logger.addHandler(new LogHandler());
        
        AudioPod.ParsePlayCounts();
    }
    
    public static void ParsePlayCounts() {
        Preferences fPrefs = Preferences.userRoot().node("ws/afterglo/audioPod");
        String iTunesPath = fPrefs.get("iTunes Path", "default");
        
        if(iTunesPath.equals("default")) {
            logger.log(Level.INFO, AudioPod.NoPrefsError);
            return;
        }
        
        DbReader reader = new DbReader(iTunesPath);
        try {
            reader.parse();
            AudioPod.recentplayed = reader.getRecentplays();
            AudioPod.UI.newTrackListAvailable();
        } catch (IOException e) {
            StackTraceElement[] trace = e.getStackTrace();
            for(int i=0; i < trace.length; i++) {
                if(trace[i].getClassName().startsWith("ws.afterglo.audioPod")) {
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
        
        if(username.equals("default") && password.equals("default")) {
            logger.log(Level.INFO, AudioPod.NoPrefsError);
            return;
        }
        
        try {
            AudioPod.scrobbler = new Scrobbler(username, password);
            List activeRecentPlayed = onlyActiveTrackItems(recentplayed);
            AudioPod.scrobbler.handshake(activeRecentPlayed);
            AudioPod.scrobbler.submittracks(activeRecentPlayed);
            AudioPod.recentplayed = onlyInactiveTrackItems(recentplayed); //clear recent track list
            AudioPod.UI.newTrackListAvailable();
        } catch (Exception e) {
            StackTraceElement[] trace = e.getStackTrace();
            for(int i=0; i < trace.length; i++) {
                if(trace[i].getClassName().startsWith("ws.afterglo.audioPod")) {
                    logger.log(Level.SEVERE, trace[i].toString());
                }
            }
            logger.log(Level.SEVERE, e.toString());
            JOptionPane.showMessageDialog(null, e.getMessage(),"Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static List onlyActiveTrackItems(List recentPlayed) {
        return filterTrackItems(recentPlayed, true);
    }

    public static List onlyInactiveTrackItems(List recentPlayed) {
        return filterTrackItems(recentPlayed, false);
    }

    public static List filterTrackItems(List recentPlayed, boolean filterActive) {
        List filteredRecentPlayed = new ArrayList(); 
        
        for(int i = 0; i < recentPlayed.size(); i++ ) {
            TrackItem trackItem = (TrackItem) recentPlayed.get(i);
            
            boolean trackActive = trackItem.isActive().booleanValue();

            if (trackActive && filterActive) {
                filteredRecentPlayed.add(trackItem);
            } else if(!trackActive && !filterActive) {
                filteredRecentPlayed.add(trackItem);
            }
        }
        
        return filteredRecentPlayed;
    }

    public static void main(String args[]) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AudioPod.Load();
            }
        });
    }
}