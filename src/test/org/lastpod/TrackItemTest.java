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

/**
 * Tests the <code>TrackItem</code> object.
 * @author Chris Tilden
 */
public class TrackItemTest extends TestCase {
    /**
     * A constant that stores the default "Various Artists" string.
     */
    private static final String[] VARIOUS_ARTISTS_STRING =
        { "Various Artists", "__Compilations", "__Soundtracks" };

    /**
     * Returns a JUnit TestSuite for this test case.
     * @return  A JUnit TestSuite for this test case.
     */
    public static Test suite() {
        return new TestSuite(TrackItemTest.class);
    }

    /**
     * Tests that the copy constructor works properly.
     */
    public void testCopyConstructor() {
        TrackItem trackItem2 = new TrackItem();
        trackItem2.setTrackid(1);
        trackItem2.setActive(Boolean.TRUE);
        trackItem2.setLength(60);
        trackItem2.setArtist("My Chemical Romance");
        trackItem2.setAlbum("The Black Parade");
        trackItem2.setTrack("Welcome To The Black Parade");
        trackItem2.setPlaycount(1);
        trackItem2.setLastplayed(1);
        trackItem2.setParseVariousArtists(true);

        TrackItem trackItem = new TrackItem(trackItem2);

        assertEquals(1, trackItem.getTrackid());
        assertEquals(Boolean.TRUE, trackItem.isActive());
        assertEquals(60, trackItem.getLength());
        assertEquals("My Chemical Romance", trackItem.getArtist());
        assertEquals("The Black Parade", trackItem.getAlbum());
        assertEquals("Welcome To The Black Parade", trackItem.getTrack());
        assertEquals(1, trackItem.getPlaycount());
        assertEquals(1, trackItem.getLastplayed());
        assertEquals(true, trackItem.isParseVariousArtists());

        /* Make sure the copy constructor worked properly. */
        trackItem2.setArtist("Chris Tilden");
        assertEquals("Chris Tilden", trackItem2.getArtist());
        assertEquals("My Chemical Romance", trackItem.getArtist());
    }

    /**
     * Tests a normal track (not "Various Artists").
     */
    public void testNormalTrack() {
        TrackItem trackItem = new TrackItem();
        trackItem.setTrackid(1);
        trackItem.setActive(Boolean.TRUE);
        trackItem.setLength(60);
        trackItem.setArtist("My Chemical Romance");
        trackItem.setAlbum("The Black Parade");
        trackItem.setTrack("Welcome To The Black Parade");
        trackItem.setPlaycount(1);
        trackItem.setLastplayed(1);
        trackItem.setParseVariousArtists(true);

        assertFalse(trackItem.isVariousArtistAlbum(true, VARIOUS_ARTISTS_STRING));
        assertFalse(trackItem.isVariousArtistAlbum(false, VARIOUS_ARTISTS_STRING));
        assertEquals("My Chemical Romance", trackItem.getArtist());
        assertEquals("The Black Parade", trackItem.getAlbum());
        assertEquals("Welcome To The Black Parade", trackItem.getTrack());

        trackItem.setParseVariousArtists(false);
        assertEquals("My Chemical Romance", trackItem.getArtist());
        assertEquals("Welcome To The Black Parade", trackItem.getTrack());
    }

    /**
     * Tests a non-parsed "Various Artists" track.
     */
    public void testNonParsedVariousArtistsTrack() {
        TrackItem trackItem = new TrackItem();
        trackItem.setTrackid(1);
        trackItem.setActive(Boolean.TRUE);
        trackItem.setLength(60);
        trackItem.setArtist("Various Artists");
        trackItem.setAlbum("A 1940's Christmas");
        trackItem.setTrack("Bing Crosby - I'll Be Home for Christmas");
        trackItem.setPlaycount(1);
        trackItem.setLastplayed(1);
        trackItem.setParseVariousArtists(false);

        assertTrue(trackItem.isVariousArtistAlbum(true, VARIOUS_ARTISTS_STRING));
        assertFalse(trackItem.isVariousArtistAlbum(false, VARIOUS_ARTISTS_STRING));
        assertEquals("Various Artists", trackItem.getArtist());
        assertEquals("A 1940's Christmas", trackItem.getAlbum());
        assertEquals("Bing Crosby - I'll Be Home for Christmas", trackItem.getTrack());
    }

    /**
     * Tests a "Various Artists" track.
     */
    public void testVariousArtistsTrack() {
        TrackItem trackItem = new TrackItem();
        trackItem.setTrackid(1);
        trackItem.setActive(Boolean.TRUE);
        trackItem.setLength(60);
        trackItem.setArtist("Various Artists");
        trackItem.setAlbum("A 1940's Christmas");
        trackItem.setTrack("Bing Crosby - I'll Be Home for Christmas");
        trackItem.setPlaycount(1);
        trackItem.setLastplayed(1);
        trackItem.setParseVariousArtists(true);
        trackItem.setVariousArtistsStrings(VARIOUS_ARTISTS_STRING);

        assertTrue(trackItem.isVariousArtistAlbum(true, VARIOUS_ARTISTS_STRING));
        assertFalse(trackItem.isVariousArtistAlbum(false, VARIOUS_ARTISTS_STRING));
        assertEquals("Bing Crosby", trackItem.getArtist());
        assertEquals("A 1940's Christmas", trackItem.getAlbum());
        assertEquals("I'll Be Home for Christmas", trackItem.getTrack());
    }

    /**
     * Tests a "Various Artists" __Compilations track.
     */
    public void testCompilationTrack() {
        TrackItem trackItem = new TrackItem();
        trackItem.setTrackid(1);
        trackItem.setActive(Boolean.TRUE);
        trackItem.setLength(60);
        trackItem.setArtist("__Compilations");
        trackItem.setAlbum("A 1940's Christmas");
        trackItem.setTrack("Bing Crosby - I'll Be Home for Christmas");
        trackItem.setPlaycount(1);
        trackItem.setLastplayed(1);
        trackItem.setParseVariousArtists(true);
        trackItem.setVariousArtistsStrings(VARIOUS_ARTISTS_STRING);

        assertTrue(trackItem.isVariousArtistAlbum(true, VARIOUS_ARTISTS_STRING));
        assertFalse(trackItem.isVariousArtistAlbum(false, VARIOUS_ARTISTS_STRING));
        assertEquals("Bing Crosby", trackItem.getArtist());
        assertEquals("A 1940's Christmas", trackItem.getAlbum());
        assertEquals("I'll Be Home for Christmas", trackItem.getTrack());
    }

    /**
     * Tests a "Various Artists"  __Soundtracks track.
     */
    public void testSoundtrackTrack() {
        TrackItem trackItem = new TrackItem();
        trackItem.setTrackid(1);
        trackItem.setActive(Boolean.TRUE);
        trackItem.setLength(60);
        trackItem.setArtist("__Soundtracks");
        trackItem.setAlbum("A 1940's Christmas");
        trackItem.setTrack("Bing Crosby - I'll Be Home for Christmas");
        trackItem.setPlaycount(1);
        trackItem.setLastplayed(1);
        trackItem.setParseVariousArtists(true);
        trackItem.setVariousArtistsStrings(VARIOUS_ARTISTS_STRING);

        assertTrue(trackItem.isVariousArtistAlbum(true, VARIOUS_ARTISTS_STRING));
        assertFalse(trackItem.isVariousArtistAlbum(false, VARIOUS_ARTISTS_STRING));
        assertEquals("Bing Crosby", trackItem.getArtist());
        assertEquals("A 1940's Christmas", trackItem.getAlbum());
        assertEquals("I'll Be Home for Christmas", trackItem.getTrack());
    }
}
