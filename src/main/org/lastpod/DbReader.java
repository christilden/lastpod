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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.math.BigInteger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Reads the iTunes database directly from the iPod.
 * @author muti
 * @author Chris Tilden
 * @version $Id$
 */
public class DbReader {
    /**
     * The location of the iTunes database file.
     */
    private String itunesfile;

    /**
     * The location of the iPod play counts file.
     */
    private String playcountsfile;

    /**
     * A buffered stream that reads the iTunes database file.
     */
    private BufferedInputStream itunesistream;

    /**
     * A buffered stream that reads the iPod play counts file.
     */
    private BufferedInputStream playcountsistream;

    /**
     * A list of all the tracks from the iTunes databaes file.
     */
    private ArrayList tracklist;

    /**
     * Stores a boolean value that will be passed into <code>TrackItem</code>
     */
    boolean parseVariousArtists;

    /**
     * A list of the recently played tracks from the iPod play counts file.
     * (Sorted by play time. This is important because otherwise Last.fm will
     * reject them.)
     */
    private ArrayList recentplays;

    /**
     * Default constructor should not be used.
     */
    private DbReader() {
        /* Default constructor. */
    }

    /**
     * Initializes the class with the locations of the iPod DB files.
     *
     * @param itunespath  Directory containing the iTunesDB and the corresponding
     *                         Play Counts, including trailing "\" or "/".
     */
    public DbReader(String itunespath, boolean parseVariousArtists) {
        if (!itunespath.endsWith(File.separator)) {
            itunespath += File.separator;
        }

        this.itunesfile = itunespath + "iTunesDB";
        this.playcountsfile = itunespath + "Play Counts";
        this.tracklist = new ArrayList();
        this.recentplays = new ArrayList();
        this.parseVariousArtists = parseVariousArtists;
    }

    /**
     * Gets the recent plays.
     * @return Returns recent plays.
     */
    public ArrayList getRecentplays() {
        return this.recentplays;
    }

    /**
     * Attempts to open and parse the DB & Play Counts files, creating
     * the appropriate data structures.
     * @throws IOException  Thrown if errors occur.
     */
    public void parse() throws IOException {
        try {
            FileInputStream itstream = new FileInputStream(this.itunesfile);
            this.itunesistream = new BufferedInputStream(itstream, 65535);
        } catch (IOException e) {
            throw new IOException("Error reading iTunes Database");
        }

        try {
            FileInputStream pcstream = new FileInputStream(this.playcountsfile);
            this.playcountsistream = new BufferedInputStream(pcstream, 65535);
        } catch (IOException e) {
            String errorMsg =
                "Error reading Play Counts Database.\n"
                + "Have you listened to any music on your iPod recently?\n"
                + "This can also be caused if you are running iTunes and you have it setup "
                + "to automatically run iTunes when an iPod is detected.";
            throw new IOException(errorMsg);
        }

        this.parseitunesdb();
        this.parseplaycounts();

        this.itunesistream.close();
        this.playcountsistream.close();
    }

    /**
     * Parses track information from the iTunesDB.
     * @throws IOException  Thrown if errors occur.
     */
    public void parseitunesdb() throws IOException {
        byte[] buf = new byte[1];

        //we seek one at a time because the mhit marker won't always be at a multiple of four
        while (this.itunesistream.read(buf) != -1) {
            if (buf[0] == 'm') { //Search for MHIT
                this.itunesistream.mark(1048576);
                buf = new byte[3];
                this.itunesistream.read(buf);

                if (new String(buf).equals("hit")) {
                    this.tracklist.add(this.parsemhit());
                } else {
                    this.itunesistream.reset();
                }
            }

            buf = new byte[1];
        }
    }

    /**
     * Parses an MHIT object from the iTunes Database.
     * @return Returns parsed track object.
     * @throws IOException  Thrown if errors occur.
     */
    public TrackItem parsemhit() throws IOException {
        byte[] dword = new byte[4];
        TrackItem track = new TrackItem();
        track.setParseVariousArtists(parseVariousArtists);

        this.itunesistream.mark(1048576); //mark beginning of MHIT location

        this.itunesistream.read(dword);

        long headersize = DbReader.littleEndianToBigInt(dword).longValue();

        DbReader.skipFully(this.itunesistream, 4);
        this.itunesistream.read(dword);

        long nummhods = DbReader.littleEndianToBigInt(dword).longValue();

        this.itunesistream.read(dword);
        track.setTrackid(DbReader.littleEndianToBigInt(dword).longValue());

        DbReader.skipFully(this.itunesistream, 20);
        this.itunesistream.read(dword);
        track.setLength(DbReader.littleEndianToBigInt(dword).longValue() / 1000);

        this.itunesistream.reset();
        DbReader.skipFully(this.itunesistream, headersize - 4); //skip to end of MHIT

        for (long i = 0; i < nummhods; i++) {
            this.parsemhod(track);
        }

        return track;
    }

    /**
     * Parses an MHOD object and sets proper fields in the track item object.
     * @param track  Track Item.
     * @throws IOException  Thrown if errors occur.
     */
    public void parsemhod(TrackItem track) throws IOException {
        byte[] dword = new byte[4];

        this.itunesistream.mark(1048576); //mark beginning of MHOD location

        DbReader.skipFully(this.itunesistream, 8);

        this.itunesistream.read(dword);

        long totalsize = DbReader.littleEndianToBigInt(dword).longValue();

        this.itunesistream.read(dword);

        int mhodtype = DbReader.littleEndianToBigInt(dword).intValue();

        if ((mhodtype == 1) || (mhodtype == 3) || (mhodtype == 4)) {
            DbReader.skipFully(this.itunesistream, 12);
            this.itunesistream.read(dword);

            int strlen = DbReader.littleEndianToBigInt(dword).intValue();

            DbReader.skipFully(this.itunesistream, 8);

            byte[] data = new byte[strlen];
            this.itunesistream.read(data);

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

        this.itunesistream.reset();
        DbReader.skipFully(this.itunesistream, totalsize);
    }

    /**
     * Parses play counts information from "Play Counts".
     * @throws IOException  Thrown if errors occur.
     */
    public void parseplaycounts() throws IOException {
        byte[] dword = new byte[4];

        DbReader.skipFully(this.playcountsistream, 8);
        this.playcountsistream.read(dword);

        long entrylen = DbReader.littleEndianToBigInt(dword).longValue();

        this.playcountsistream.read(dword);

        int numentries = DbReader.littleEndianToBigInt(dword).intValue();

        DbReader.skipFully(this.playcountsistream, 80); //skip rest of header

        for (int i = 0; i < (numentries - 1); i++) {
            this.playcountsistream.mark(1048576); //save beginning of entry location

            this.playcountsistream.read(dword);

            long playcount = DbReader.littleEndianToBigInt(dword).longValue();

            if (playcount > 0) {
                this.playcountsistream.read(dword);

                long lastplayed = DbReader.littleEndianToBigInt(dword).longValue();
                lastplayed -= 2082844800; //convert to UNIX timestamp

                Calendar calendar = Calendar.getInstance();
                long offset =
                    calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);
                lastplayed -= (offset / 1000);

                TrackItem temptrack = (TrackItem) this.tracklist.get(i);
                temptrack.setPlaycount(playcount);
                temptrack.setLastplayed(lastplayed - temptrack.getLength());

                if (History.getInstance().isInHistory(temptrack.getLastplayed())) {
                    temptrack.setActive(Boolean.FALSE);
                }

                this.recentplays.add(this.tracklist.get(i));
            }

            this.playcountsistream.reset();
            DbReader.skipFully(this.playcountsistream, entrylen);
        }

        Collections.sort(this.recentplays);
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
    public static void skipFully(BufferedInputStream stream, long bytes)
            throws IOException {
        for (long i = stream.skip(bytes); i < bytes; i += stream.skip(bytes - i)) {
            /* The loop itself performs all the logic needed to skip. */
        }
    }
}
