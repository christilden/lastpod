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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.lastpod.parser.MockItunesDbParser;
import org.lastpod.parser.PlayCountsParser;

import java.util.List;

/**
 * Tests the <code>DbReader</code> object.
 * @author Chris Tilden
 */
public class DbReaderTest extends TestCase {
    /**
     * The location of the test Play Counts file.
     */
    private static final String PLAY_COUNTS_FILE_LOCATION = "../src/test";

    /**
     * Returns a JUnit TestSuite for this test case.
     * @return  A JUnit TestSuite for this test case.
     */
    public static Test suite() {
        return new TestSuite(DbReaderTest.class);
    }

    /**
     * Tests the DbReader with no play counts.
     */
    public void testDbReaderDoNotParsePlayCounts() {
        boolean parsePlayCounts = false;
        MockItunesDbParser itunesDbParser = new MockItunesDbParser();
        PlayCountsParser playCountsParser =
            new PlayCountsParser(PLAY_COUNTS_FILE_LOCATION, parsePlayCounts);
        DbReader reader = new DbReader(itunesDbParser, playCountsParser);
        List recentlyPlayed = null;

        try {
            reader.parse();
            recentlyPlayed = reader.getRecentplays();
        } catch (Exception e) {
            fail(e.toString());
        }

        assertTrue(recentlyPlayed.size() == 15);

        TrackItem track = (TrackItem) recentlyPlayed.get(14);
        assertEquals(20598, track.getTrackid());
        assertEquals(427, track.getLength());
        assertEquals("Bob Marley & The Wailers", track.getArtist());
        assertEquals("Legend", track.getAlbum());
        assertEquals("No Woman, No Cry (live)", track.getTrack());
        assertEquals(2, track.getPlaycount());
        assertEquals(1181489924, track.getLastplayed());
    }

    /**
     * Tests the DbReader with play counts.
     */
    public void testDbReaderParsePlayCounts() {
        boolean parsePlayCounts = true;
        MockItunesDbParser itunesDbParser = new MockItunesDbParser();
        PlayCountsParser playCountsParser =
            new PlayCountsParser(PLAY_COUNTS_FILE_LOCATION, parsePlayCounts);
        DbReader reader = new DbReader(itunesDbParser, playCountsParser);
        List recentlyPlayed = null;

        try {
            reader.parse();
            recentlyPlayed = reader.getRecentplays();
        } catch (Exception e) {
            fail(e.toString());
        }

        assertTrue(recentlyPlayed.size() == 16);

        TrackItem track = (TrackItem) recentlyPlayed.get(14);
        assertEquals(20598, track.getTrackid());
        assertEquals(427, track.getLength());
        assertEquals("Bob Marley & The Wailers", track.getArtist());
        assertEquals("Legend", track.getAlbum());
        assertEquals("No Woman, No Cry (live)", track.getTrack());
        assertEquals(1, track.getPlaycount());
        assertEquals(1181489924, track.getLastplayed());

        track = (TrackItem) recentlyPlayed.get(15);
        assertEquals(20598, track.getTrackid());
        assertEquals(427, track.getLength());
        assertEquals("Bob Marley & The Wailers", track.getArtist());
        assertEquals("Legend", track.getAlbum());
        assertEquals("No Woman, No Cry (live)", track.getTrack());
        assertEquals(1, track.getPlaycount());
        assertEquals(1181490351, track.getLastplayed());
    }
}
