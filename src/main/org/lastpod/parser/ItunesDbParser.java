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
import java.util.List;

/**
 * Parses the iTunes DB file from the iPod an creates a <code>List</code> of
 * <code>TrackItems</code>.  Note: the TrackItems returned do not contain play
 * count information only things like track title, artist name, album name, etc.
 * @author Chris Tilden
 */
public class ItunesDbParser implements TrackItemParser {
    /**
     * The location of the iTunes database file.
     */
    private String iTunesFile;

    /**
     * Stores a boolean value that will be passed into <code>TrackItem</code>.
     */
    boolean parseVariousArtists;

    /**
     * Stores the strings used to parse various artists.
     */
    String[] variousArtistsStrings;

    /**
     * Default constructor should not be used.
     */
    private ItunesDbParser() {
        /* Default constructor. */
    }

    /**
     * Initializes the class with the locations of the iPod DB files.
     *
     * @param iTunesPath  Directory containing the iTunesDB and the corresponding
     *                         Play Counts, including trailing "\" or "/".
     * @param parseVariousArtists  If <code>true</code> then parses "Various
     * Artists"
     * @param variousArtistsStrings  A String array containing the various artist
     * strings that should be parsed.
     */
    public ItunesDbParser(String iTunesPath, boolean parseVariousArtists,
        String[] variousArtistsStrings) {
        if (!iTunesPath.endsWith(File.separator)) {
            iTunesPath += File.separator;
        }

        this.iTunesFile = iTunesPath + "iTunesDB";
        this.parseVariousArtists = parseVariousArtists;
        this.variousArtistsStrings = variousArtistsStrings;
    }

    /**
     * Performs parsing.
     * @return  A <code>List</code> containing <code>TrackItems</code>.  It
     * contains all tracks from the iTunes database.
     */
    public List parse() {
        InputStream itunesFileIn = null;
        InputStream itunesBufferedIn = null;

        try {
            itunesFileIn = new FileInputStream(iTunesFile);
            itunesBufferedIn = new BufferedInputStream(itunesFileIn, 65535);

            return parseitunesdb(itunesBufferedIn);
        } catch (IOException e) {
            throw new RuntimeException("Error reading iTunes Database");
        } finally {
            IoUtils.cleanup(itunesFileIn, null);
            IoUtils.cleanup(itunesBufferedIn, null);
        }
    }

    /**
     * Parses track information from the iTunesDB.
     * @param itunesistream  A stream that reads the iTunes database file.
     * @return  A <code>List</code> containing <code>TrackItems</code>.  It
     * contains all tracks from the iTunes database.
     * @throws IOException  Thrown if errors occur.
     */
    private List parseitunesdb(InputStream itunesistream)
            throws IOException {
        byte[] buf = new byte[1];
        List trackList = new ArrayList();

        //we seek one at a time because the mhit marker won't always be at a multiple of four
        while (itunesistream.read(buf) != -1) {
            if (buf[0] == 'm') { //Search for MHIT
                itunesistream.mark(1048576);
                buf = new byte[3];
                itunesistream.read(buf);

                if (new String(buf).equals("hit")) {
                    trackList.add(parsemhit(itunesistream));
                } else {
                    itunesistream.reset();
                }
            }

            buf = new byte[1];
        }

        return trackList;
    }

    /**
     * Parses an MHIT object from the iTunes Database.
     * @param itunesistream  A stream that reads the iTunes database file.
     * @return Returns parsed track object.
     * @throws IOException  Thrown if errors occur.
     */
    public TrackItem parsemhit(InputStream itunesistream)
            throws IOException {
        byte[] dword = new byte[4];
        TrackItem track = new TrackItem();
        track.setParseVariousArtists(parseVariousArtists);
        track.setVariousArtistsStrings(variousArtistsStrings);

        itunesistream.mark(1048576); //mark beginning of MHIT location

        itunesistream.read(dword);

        long headersize = IoUtils.littleEndianToBigInt(dword).longValue();

        IoUtils.skipFully(itunesistream, 4);
        itunesistream.read(dword);

        long nummhods = IoUtils.littleEndianToBigInt(dword).longValue();

        itunesistream.read(dword);
        track.setTrackid(IoUtils.littleEndianToBigInt(dword).longValue());

        IoUtils.skipFully(itunesistream, 20);
        itunesistream.read(dword);
        track.setLength(IoUtils.littleEndianToBigInt(dword).longValue() / 1000);

        itunesistream.reset();
        IoUtils.skipFully(itunesistream, headersize - 4); //skip to end of MHIT

        for (long i = 0; i < nummhods; i++) {
            parsemhod(track, itunesistream);
        }

        return track;
    }

    /**
     * Parses an MHOD object and sets proper fields in the track item object.
     * @param track  Track Item.
     * @param itunesistream  A stream that reads the iTunes database file.
     * @throws IOException  Thrown if errors occur.
     */
    public void parsemhod(TrackItem track, InputStream itunesistream)
            throws IOException {
        byte[] dword = new byte[4];

        itunesistream.mark(1048576); //mark beginning of MHOD location

        IoUtils.skipFully(itunesistream, 8);

        itunesistream.read(dword);

        long totalsize = IoUtils.littleEndianToBigInt(dword).longValue();

        itunesistream.read(dword);

        int mhodtype = IoUtils.littleEndianToBigInt(dword).intValue();

        if ((mhodtype == 1) || (mhodtype == 3) || (mhodtype == 4)) {
            IoUtils.skipFully(itunesistream, 12);
            itunesistream.read(dword);

            int strlen = IoUtils.littleEndianToBigInt(dword).intValue();

            IoUtils.skipFully(itunesistream, 8);

            byte[] data = new byte[strlen];
            itunesistream.read(data);

            String stringdata = new String(data, "UTF-16LE");

            switch (mhodtype) {
            case 1:
                track.setTrack(stringdata);

                break;

            case 3:
                track.setAlbum(stringdata);

                break;

            case 4:
                track.setArtist(stringdata);

                break;
            }
        }

        itunesistream.reset();
        IoUtils.skipFully(itunesistream, totalsize);
    }

    /**
     * Does nothing for this implementation.
     * @param trackList  Does nothing for this implementation.
     */
    public void setTrackList(List trackList) {
        /* Do nothing. */
    }
}
