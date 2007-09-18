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
 * Tests the <code>DbReader</code> object.
 * @author Chris Tilden
 */
public class ModelImplTest extends TestCase {
    /**
     * Returns a JUnit TestSuite for this test case.
     * @return  A JUnit TestSuite for this test case.
     */
    public static Test suite() {
        return new TestSuite(ModelImplTest.class);
    }

    /**
     * Tests spliting a ; seperated String of various artist strings.
     *
     */
    public void testSplitVariousArtistsString() {
        String variousArtistsString = "Various Artists;__Soundtracks;__Compilations";
        String[] splitVariousArtistsStrings =
            ModelImpl.splitVariousArtistsString(variousArtistsString);

        assertEquals("Various Artists", splitVariousArtistsStrings[0]);
        assertEquals("__Soundtracks", splitVariousArtistsStrings[1]);
        assertEquals("__Compilations", splitVariousArtistsStrings[2]);
    }

    /**
     * Tests spliting a "; " seperated String of various artist strings.
     *
     */
    public void testSplitVariousArtistsString2() {
        String variousArtistsString = "Various Artists; __Soundtracks; __Compilations ";
        String[] splitVariousArtistsStrings =
            ModelImpl.splitVariousArtistsString(variousArtistsString);

        assertEquals("Various Artists", splitVariousArtistsStrings[0]);
        assertEquals("__Soundtracks", splitVariousArtistsStrings[1]);
        assertEquals("__Compilations", splitVariousArtistsStrings[2]);
    }

    /**
     * Tests spliting a " ; " seperated String of various artist strings.
     *
     */
    public void testSplitVariousArtistsString3() {
        String variousArtistsString = " Various Artists ; __Soundtracks ; __Compilations ";
        String[] splitVariousArtistsStrings =
            ModelImpl.splitVariousArtistsString(variousArtistsString);

        assertEquals("Various Artists", splitVariousArtistsStrings[0]);
        assertEquals("__Soundtracks", splitVariousArtistsStrings[1]);
        assertEquals("__Compilations", splitVariousArtistsStrings[2]);
    }
}
