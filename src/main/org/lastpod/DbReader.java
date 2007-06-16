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

import org.lastpod.parser.TrackItemParser;

import java.io.IOException;
import java.io.InputStream;

import java.math.BigInteger;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads the iTunes database directly from the iPod.
 * @author muti
 * @author Chris Tilden
 * @version $Id$
 */
public class DbReader {
    /**
     * Parses the itunesDatabase.
     */
    private TrackItemParser itunesDbParser;

    /**
     * Parses the Play Counts file.
     */
    private TrackItemParser playCountsParser;

    /**
     * A list of the recently played tracks from the iPod play counts file.
     * (Sorted by play time. This is important because otherwise Last.fm will
     * reject them.)
     */
    private List recentplays;

    /**
     * Default constructor should not be used.
     */
    private DbReader() {
        /* Default constructor. */
    }

    /**
     * Initializes the class with the locations of the iPod DB files.
     *
     */
    public DbReader(TrackItemParser itunesDbParser, TrackItemParser playCountsParser) {
        this.itunesDbParser = itunesDbParser;
        this.recentplays = new ArrayList();
        this.playCountsParser = playCountsParser;
    }

    /**
     * Gets the recent plays.
     * @return Returns recent plays.
     */
    public List getRecentplays() {
        return recentplays;
    }

    /**
     * Attempts to open and parse the DB & Play Counts files, creating
     * the appropriate data structures.
     */
    public void parse() {
        List trackList = itunesDbParser.parse();

        playCountsParser.setTrackList(trackList);
        recentplays = playCountsParser.parse();
    }

    /**
     * This converts any size byte array to a BigInteger.
     * @param num  Little-Endian byte array.
     * @return A BigInt.
     */
    public static BigInteger littleEndianToBigInt(byte[] num) {
        byte temp;

        int upperBound = num.length - 1;
        int lowerBound = 0;

        while (lowerBound < upperBound) {
            temp = num[lowerBound];
            num[lowerBound] = num[upperBound];
            num[upperBound] = temp;
            lowerBound++;
            upperBound--;
        }

        return new BigInteger(1, num);
    }

    /**
     * Guarantees that the specified number of bytes will be skipped.
     * @param stream Input Stream.
     * @param bytes Number of bytes to skip.
     * @throws IOException  Thrown if errors occur.
     */
    public static void skipFully(InputStream stream, long bytes)
            throws IOException {
        for (long i = stream.skip(bytes); i < bytes; i += stream.skip(bytes - i)) {
            /* The loop itself performs all the logic needed to skip. */
        }
    }
}
