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

import org.lastpod.util.IoUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Parses the iTunesStats file from the iPod shuffle and creates a
 * <code>List</code> of <code>TrackItems</code>.  Note: the TrackItems returned
 * contain play count information and things like track title, artist name,
 * album name, etc.
 * @author Chris Tilden
 */
public class ItunesStatsParser implements TrackItemParser {
    /**
     * The location of the iTunes path.
     */
    private String iTunesPath;

    /**
     * The location of the iPod play counts file.
     */
    private String iTunesStatsFile;

    /**
     * Stores a boolean value that will be passed into <code>TrackItem</code>.
     */
    boolean parseMultiPlayTracks;

    /**
     * Contains all tracks from the iTunes database.
     */
    private List trackList;

    /**
     * Default constructor should not be used.
     */
    private ItunesStatsParser() {
        /* Default constructor. */
    }

    /**
     * Initializes the class with the locations of the iPod DB files.
     *
     * @param iTunesPath  Directory containing the iTunesDB and the corresponding
     *                         iTunesStats, including trailing "\" or "/".
     * @param parseMultiPlayTracks  If <code>true</code> parses the play count
     * values.  Manufactures additional TrackItems in the recently played list,
     * in order to properly count tracks that were played more than once.
     */
    public ItunesStatsParser(String iTunesPath, boolean parseMultiPlayTracks) {
        if (!iTunesPath.endsWith(File.separator)) {
            iTunesPath += File.separator;
        }

        this.iTunesPath = iTunesPath;
        this.iTunesStatsFile = iTunesPath + "iTunesStats";
        this.parseMultiPlayTracks = parseMultiPlayTracks;
    }

    /**
     * Sets the List of tracks.
     * @param trackList  The List of tracks.
     */
    public void setTrackList(List trackList) {
        this.trackList = trackList;
    }

    /**
     * Performs parsing.
     * @return A <code>List</code> of complete <code>TrackItem</code>s that
     * were in the play counts file.
     */
    public List parse() {
        if ((trackList == null) || (trackList.size() == 0)) {
            throw new RuntimeException("Programming error, setTrackList() was never performed!");
        }

        InputStream playCountsFileIn = null;
        InputStream playCountsBufferedIn = null;

        try {
            playCountsFileIn = new FileInputStream(iTunesStatsFile);
            playCountsBufferedIn = new BufferedInputStream(playCountsFileIn, 65535);

            return parseitunesStats(playCountsBufferedIn);
        } catch (IOException e) {
            String errorMsg =
                "Error reading iTunesStats Database.\n"
                + "Have you listened to any music on your iPod recently?\n"
                + "This can also be caused if you are running iTunes and you have it setup "
                + "to automatically run iTunes when an iPod is detected.";
            throw new RuntimeException(errorMsg);
        } finally {
            IoUtils.cleanup(playCountsFileIn, null);
            IoUtils.cleanup(playCountsBufferedIn, null);
        }
    }

    /**
     * Parses play counts information from "Play Counts".
     * @param itunesStatsistream  A stream that reads the iPod play counts file.
     * @return A <code>List</code> of complete <code>TrackItem</code>s that
     * were in the play counts file.
     * @throws IOException  Thrown if errors occur.
     */
    private List parseitunesStats(InputStream itunesStatsistream)
            throws IOException {
        byte[] threeBytes = new byte[3];
        List recentPlays = new ArrayList();

        itunesStatsistream.read(threeBytes);

        int numentries = IoUtils.littleEndianToBigInt(threeBytes).intValue();

        IoUtils.skipFully(itunesStatsistream, 3); //skip rest of header

        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < (numentries - 1); i++) {
            /* Skip unused data. */
            IoUtils.skipFully(itunesStatsistream, 12);

            itunesStatsistream.read(threeBytes);

            /* Skip 'skippedcount'. */
            IoUtils.skipFully(itunesStatsistream, 3);

            long playcount = IoUtils.littleEndianToBigInt(threeBytes).longValue();

            if (playcount > 0) {
                TrackItem temptrack = (TrackItem) trackList.get(i);
                temptrack.setPlaycount(playcount);
                calendar.add(Calendar.SECOND, -(int) temptrack.getLength());
                temptrack.setLastplayed(calendar.getTimeInMillis() / 1000);

                recentPlays.add(trackList.get(i));

                if (parseMultiPlayTracks && (playcount > 1)) {
                    long numberToManufacture = playcount - 1;

                    for (long j = 0; j < numberToManufacture; j++) {
                        temptrack = manufactureTrack(temptrack, calendar);
                        recentPlays.add(temptrack);
                    }
                }
            }
        }

        Collections.sort(recentPlays);

        return recentPlays;
    }

    /**
     * Manufactures a Track based on the given Track.
     * @param temptrack  The track to manufacture (if needed).
     * @return  A manufactured <code>TrackItem</code>.
     */
    private TrackItem manufactureTrack(TrackItem temptrack, Calendar calendar) {
        TrackItem manufacturedTrack = new TrackItem(temptrack);
        calendar.add(Calendar.SECOND, -(int) manufacturedTrack.getLength());
        manufacturedTrack.setLastplayed(calendar.getTimeInMillis() / 1000);

        manufacturedTrack.setPlaycount(1);
        temptrack.setPlaycount(1);

        return manufacturedTrack;
    }
}
