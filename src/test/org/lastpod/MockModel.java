package org.lastpod;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

public class MockModel implements Model {
    private List recentlyPlayed = null;

    /**
     * Default constructor.
     */
    public MockModel() {
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
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2007);
        cal.set(Calendar.MONTH, Calendar.MAY);
        cal.set(Calendar.DAY_OF_MONTH, 25);
        cal.set(Calendar.HOUR, 10);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        for (int i = 0; i <= 30; i++) {
            TrackItem trackItem = new TrackItem();
            trackItem.setTrackid(1);
            trackItem.setLength(60);
            trackItem.setArtist("My Chemical Romance");
            trackItem.setAlbum("The Black Parade");
            trackItem.setTrack("Welcome To The Black Parade");
            trackItem.setPlaycount(1);
            trackItem.setLastplayed(cal.getTimeInMillis() / 1000);

            if (History.getInstance(".").isInHistory(trackItem.getLastplayed())) {
                trackItem.setActive(Boolean.FALSE);
            }

            recentlyPlayed.add(trackItem);

            cal.add(Calendar.HOUR, 1);
        }

        userInterface.newTrackListAvailable(recentlyPlayed);
    }

    /**
     * Submits the tracks to Last.fm
     * @param userInterface  The application's user interface.
     * @return  A status message upon completion.
     */
    public Object submitTracks(UI userInterface) {
        List activeRecentPlayed = onlyActiveTrackItems(recentlyPlayed);
        List inactiveRecentPlayed = onlyInactiveTrackItems(recentlyPlayed);

        userInterface.setNumberOfChunks(activeRecentPlayed.size());

        for (int i = 0; i < activeRecentPlayed.size(); i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                /* Just ignore. */
            }

            TrackItem trackItem = (TrackItem) activeRecentPlayed.get(i);
            trackItem.setActive(Boolean.FALSE);
            userInterface.updateCurrentChunk(i + 1);

            Logger logger = Logger.getLogger(LastPod.class.getPackage().getName());
            logger.info("The logger should be really noisy.");
        }

        History.getInstance(".");

        Scrobbler scrobbler = new Scrobbler(null, null, null);
        scrobbler.addHistories(activeRecentPlayed, inactiveRecentPlayed);
        userInterface.setCompletionStatus(true);

        /* Refresh track list. */
        userInterface.newTrackListAvailable(recentlyPlayed);

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
}
