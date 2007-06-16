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
package org.lastpod.parser;

import org.lastpod.TrackItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the iTunes DB file from the iPod an creates a <code>List</code> of
 * <code>TrackItems</code>.  Note: the TrackItems returned do not contain play
 * count information only things like track title, artist name, album name, etc.
 * @author Chris Tilden
 */
public class MockItunesDbParser implements TrackItemParser {
    /**
     * Stores a boolean value that will be passed into <code>TrackItem</code>.
     */
    boolean parseVariousArtists;

    /**
     * Default constructor.
     */
    public MockItunesDbParser() {
        /* Default constructor. */
    }

    /**
     * Performs parsing.
     * @return  A <code>List</code> containing <code>TrackItems</code>.  It
     * contains all tracks from the iTunes database.
     */
    public List parse() {
        List trackList = new ArrayList();
        TrackItem trackItem = null;

        for (int i = 0; i < 2617; i++) {
            trackItem = new TrackItem();
            trackList.add(trackItem);
        }

        trackItem = (TrackItem) trackList.get(2489);
        trackItem.setTrackid(20598);
        trackItem.setLength(427);
        trackItem.setArtist("Bob Marley & The Wailers");
        trackItem.setAlbum("Legend");
        trackItem.setTrack("No Woman, No Cry (live)");

        return trackList;
    }

    /**
     * Does nothing for this implementation.
     * @param trackList  Does nothing for this implementation.
     */
    public void setTrackList(List trackList) {
        /* Do nothing. */
    }
}
