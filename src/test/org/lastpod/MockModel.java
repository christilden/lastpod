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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

public class MockModel implements Model {
    /**
     * A constant that stores the default "Various Artists" string.
     */
    private static final String[] VARIOUS_ARTISTS_STRING =
        { "Various Artists", "__Compilations", "__Soundtracks" };
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

    public Object submitTracks(UI userInterface, boolean online) {
        return LastPod.NO_PREFS_ERROR;
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

        /* This item should not parse out Various Artists. */
        TrackItem trackItem = new TrackItem();
        trackItem.setTrackid(1);
        trackItem.setLength(60);
        trackItem.setArtist("Various Artists");
        trackItem.setAlbum("A 1940's Christmas");
        trackItem.setTrack("Bing Crosby - I'll Be Home for Christmas");
        trackItem.setPlaycount(1);
        trackItem.setLastplayed(cal.getTimeInMillis() / 1000);

        if (History.getInstance(".").isInHistory(trackItem.getLastplayed())) {
            trackItem.setActive(Boolean.FALSE);
        }

        recentlyPlayed.add(trackItem);

        cal.add(Calendar.HOUR, 1);

        /* This item should parse out Various Artists. */
        trackItem = new TrackItem();
        trackItem.setTrackid(1);
        trackItem.setLength(60);
        trackItem.setArtist("Various Artists");
        trackItem.setAlbum("A 1940's Christmas");
        trackItem.setTrack("Bing Crosby - I'll Be Home for Christmas");
        trackItem.setPlaycount(1);
        trackItem.setLastplayed(cal.getTimeInMillis() / 1000);
        trackItem.setParseVariousArtists(true);
        trackItem.setVariousArtistsStrings(VARIOUS_ARTISTS_STRING);

        if (History.getInstance(".").isInHistory(trackItem.getLastplayed())) {
            trackItem.setActive(Boolean.FALSE);
        }

        recentlyPlayed.add(trackItem);

        cal.add(Calendar.HOUR, 1);

        /* This item should parse out __Compilations. */
        trackItem = new TrackItem();
        trackItem.setTrackid(1);
        trackItem.setLength(60);
        trackItem.setArtist("__Compilations");
        trackItem.setAlbum("A 1940's Christmas");
        trackItem.setTrack("Bing Crosby - I'll Be Home for Christmas");
        trackItem.setPlaycount(1);
        trackItem.setLastplayed(cal.getTimeInMillis() / 1000);
        trackItem.setParseVariousArtists(true);
        trackItem.setVariousArtistsStrings(VARIOUS_ARTISTS_STRING);

        if (History.getInstance(".").isInHistory(trackItem.getLastplayed())) {
            trackItem.setActive(Boolean.FALSE);
        }

        recentlyPlayed.add(trackItem);

        cal.add(Calendar.HOUR, 1);

        /* This item should parse out __Soundtracks. */
        trackItem = new TrackItem();
        trackItem.setTrackid(1);
        trackItem.setLength(60);
        trackItem.setArtist("__Soundtracks");
        trackItem.setAlbum("A 1940's Christmas");
        trackItem.setTrack("Bing Crosby - I'll Be Home for Christmas");
        trackItem.setPlaycount(1);
        trackItem.setLastplayed(cal.getTimeInMillis() / 1000);
        trackItem.setParseVariousArtists(true);
        trackItem.setVariousArtistsStrings(VARIOUS_ARTISTS_STRING);

        if (History.getInstance(".").isInHistory(trackItem.getLastplayed())) {
            trackItem.setActive(Boolean.FALSE);
        }

        recentlyPlayed.add(trackItem);

        cal.add(Calendar.HOUR, 1);

        for (int i = 0; i <= 27; i++) {
            trackItem = new TrackItem();
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
        Logger logger = Logger.getLogger(LastPod.class.getPackage().getName());

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

            String statusMessage = "The logger should be really noisy.";
            userInterface.setSubmitStatusMessage(statusMessage);
            logger.info(statusMessage);
        }

        History.getInstance(".");

        Scrobbler scrobbler = new Scrobbler(null, null, null, null);
        scrobbler.addInactiveToHistories(inactiveRecentPlayed);
        scrobbler.addHistories(activeRecentPlayed);
        userInterface.setCompletionStatus(true);

        /* Refresh track list. */
        recentlyPlayed = new ArrayList();
        parsePlayCounts(userInterface);
        userInterface.newTrackListAvailable(recentlyPlayed);

        String statusMessage = "Done.";
        userInterface.setSubmitStatusMessage(statusMessage);
        logger.info(statusMessage);

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
