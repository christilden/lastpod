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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.lastpod.TrackItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests the ItunesStatsParser, which is responsible for parsing the "iTunesStats"
 * file.  This file is used by the 2nd generation iPod shuffle to store the play
 * count information.  This parser is dependent on an iTunesDB parser to supply
 * the track information.
 * @author Chris Tilden
 */
public class ItunesStatsParserTest extends TestCase {
    /**
     * Returns a JUnit TestSuite for this test case.
     * @return  A JUnit TestSuite for this test case.
     */
    public static Test suite() {
        return new TestSuite(ItunesStatsParserTest.class);
    }

    /**
     * Performs the necessary tests.
     */
    public void testItunesStatsParser() {
        TrackItemParser itunesDbParser = new ItunesDbParserTester();
        ItunesStatsParser itunesStatsParser = new ItunesStatsParser("../src/test", false);
        itunesStatsParser.setTrackList(itunesDbParser.parse());

        List recentPlays = itunesStatsParser.parse();

        assertTrue(recentPlays.size() == 1);

        TrackItem track = (TrackItem) recentPlays.get(0);
        assertEquals(track.getLength(), 233);
        assertEquals(track.getArtist(), "Korn");
        assertEquals(track.getAlbum(), "Issues");
        assertEquals(track.getTrack(), "Beg for Me");
        assertEquals(track.getPlaycount(), 1);
        assertTrue(track.getLastplayed() != 0);
    }

    /**
     * Mocks parsing the iTunes DB file from the iPod and creates a <code>List</code> of
     * <code>TrackItems</code>.  Note: This mock parser is used only for the test in the
     * parent class.
     * @author Chris Tilden
     */
    private class ItunesDbParserTester implements TrackItemParser {
        /**
         * Stores a boolean value that will be passed into <code>TrackItem</code>.
         */
        boolean parseVariousArtists;

        /**
         * Default constructor.
         */
        public ItunesDbParserTester() {
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

            for (int i = 0; i < 131; i++) {
                trackItem = new TrackItem();
                trackList.add(trackItem);
            }

            trackItem = (TrackItem) trackList.get(0);
            trackItem.setTrackid(65900);
            trackItem.setLength(233);
            trackItem.setArtist("Korn");
            trackItem.setAlbum("Issues");
            trackItem.setTrack("Beg for Me");

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
}
