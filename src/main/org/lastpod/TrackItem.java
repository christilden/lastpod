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

import java.util.Date;

/**
 * @author muti
 * @version $Id$
 */
public class TrackItem implements Comparable {
    private long trackid;
    private Boolean active;
    private long length; //in seconds
    private String artist;
    private String album;
    private String track;
    private long playcount;
    private long lastplayed;
    private boolean parseVariousArtists;

    /**
     * A set of strings that will be used to parse various artists.
     */
    private String[] variousArtistsStrings;

    /**
     * Default constructor.
     */
    public TrackItem() {
        this.trackid = 0;
        this.active = new Boolean(true);
        this.length = 0;
        this.artist = "";
        this.album = "";
        this.track = "";
        this.playcount = 0;
        this.lastplayed = 0;
    }

    /**
     * Constructs a new TrackItem based off the given one.
     * @param trackItem  The TrackItem to effectively clone.
     */
    public TrackItem(TrackItem trackItem) {
        super();
        this.setTrackid(trackItem.getTrackid());
        this.setActive(trackItem.isActive());
        this.setLength(trackItem.getLength());
        this.setArtist(trackItem.getArtist());
        this.setAlbum(trackItem.getAlbum());
        this.setTrack(trackItem.getTrack());
        this.setPlaycount(trackItem.getPlaycount());
        this.setLastplayed(trackItem.getLastplayed());
        this.setParseVariousArtists(trackItem.isParseVariousArtists());
    }

    /**
     * If <code>true</code>; sets this track to be submitted, otherwise do not
     * submit.
     * @return Returns <code>true</code> if this track should be submitted.
     */
    public Boolean isActive() {
        return active;
    }

    /**
     * If <code>true</code>; sets this track to be submitted, otherwise do not
     * submit.
     * @param active  <code>true</code> if this track should be submitted.
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * @return Returns the album.
     */
    public String getAlbum() {
        return album;
    }

    /**
     * @param album The album to set.
     */
    public void setAlbum(String album) {
        this.album = album;
    }

    /**
     * Gets the artist of this track.
     * @return Returns the artist.
     */
    public String getArtist() {
        /* If required parse the track String to obtain the proper artist. */
        if (isVariousArtistAlbum(parseVariousArtists, variousArtistsStrings)) {
            return track.split("-")[0].trim();
        }

        return artist;
    }

    /**
     * Returns <code>true</code> if this track belongs to a "Various Artist"
     * album.
     * @param parseVariousArtists  <code>true</code> Will cause the track to be
     * checked to see if it is a "Various Artist" track.  If so, the artist
     * information will be parsed from the track title.
     * @param variousArtistsString  The set of Strings to are used for various arists.
     * @return  <code>true</code> if this is a "Various Artist" album.
     */
    public boolean isVariousArtistAlbum(boolean parseVariousArtists, String[] variousArtistsString) {
        /* Returns false if "Various Artist" tracks should not be parsed. */
        if (!parseVariousArtists) {
            return false;
        }

        /* Ensures that variousArtistsString is being used. */
        if ((variousArtistsString == null) || (variousArtistsString.length < 1)) {
            return false;
        }

        /* In addition to other checks, this makes sure the track is not null,
         * because track will need to be parsed. */
        if ((track != null) && (artist != null)) {
            /* If the track matches then return true right away. */
            for (int i = 0; i < variousArtistsString.length; i++) {
                if (artist.trim().toLowerCase().equals(variousArtistsString[i].toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @param artist The artist to set.
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * @return Returns the lastplayed.
     */
    public long getLastplayed() {
        return lastplayed;
    }

    /**
     * @param lastplayed UNIX timestamp, in seconds.
     */
    public void setLastplayed(long lastplayed) {
        this.lastplayed = lastplayed;
    }

    /**
     * @return Returns the length.
     */
    public long getLength() {
        return length;
    }

    /**
     * @param length The length to set.
     */
    public void setLength(long length) {
        this.length = length;
    }

    /**
     * @return Returns the playcount.
     */
    public long getPlaycount() {
        return playcount;
    }

    /**
     * @param playcount The playcount to set.
     */
    public void setPlaycount(long playcount) {
        this.playcount = playcount;
    }

    /**
     * Gets the track name of the track.
     * @return Returns the track.
     */
    public String getTrack() {
        /* If required parse the track String to obtain the proper track. */
        if (isVariousArtistAlbum(parseVariousArtists, variousArtistsStrings)) {
            return track.split("-")[1].trim();
        }

        return track;
    }

    /**
     * @param track The track to set.
     */
    public void setTrack(String track) {
        this.track = track;
    }

    /**
     * @return Returns the trackid.
     */
    public long getTrackid() {
        return trackid;
    }

    /**
     * @param trackid The trackid to set.
     */
    public void setTrackid(long trackid) {
        this.trackid = trackid;
    }

    /**
     * Returns <code>true</code> if the track should be parsed for "Various
     * Artists".
     * @return  Returns <code>true</code> if the track should be parsed for
     * "Various Artists".
     */
    public boolean isParseVariousArtists() {
        return parseVariousArtists;
    }

    /**
     * Set this to <code>true</code> if the track should be parsed for "Various
     * Artists".
     * @param parseVariousArtists  <code>true</code> Will cause the track to be
     * checked to see if it is a "Various Artist" track.  If so, the artist
     * information will be parsed from the track title.
     */
    public void setParseVariousArtists(boolean parseVariousArtists) {
        this.parseVariousArtists = parseVariousArtists;
    }

    /**
     * Gets the set of strings that will be used to parse various artists.
     * @return  The set of strings that will be used to parse various artists.
     */
    public String[] getVariousArtistsStrings() {
        return variousArtistsStrings;
    }

    /**
     * Sets the set of strings that will be used to parse various artists.
     * @param variousArtistsStrings  The set of strings that will be used to parse
     * various artists.
     */
    public void setVariousArtistsStrings(String[] variousArtistsStrings) {
        this.variousArtistsStrings = variousArtistsStrings;
    }

    public int compareTo(Object trackItem) {
        TrackItem temptrack = (TrackItem) trackItem;

        if (this.lastplayed < temptrack.getLastplayed()) {
            return -1;
        } else if (this.lastplayed > temptrack.getLastplayed()) {
            return 1;
        }

        return 0;
    }

    public String toString() {
        String tempstring;

        tempstring = "Track ID: " + trackid + "\n";
        tempstring += ("Length: " + length + "\n");
        tempstring += ("Artist: " + getArtist() + "\n");
        tempstring += ("Album: " + album + "\n");
        tempstring += ("Track: " + getTrack() + "\n");
        tempstring += ("Play Count: " + playcount + "\n");
        tempstring += ("Last Played: " + new Date(lastplayed * 1000) + "\n");

        return tempstring;
    }
}
