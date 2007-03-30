/* 
 * LastPod is an application used to publish one's iPod play counts to Last.fm.
 * Copyright (C) 2007  muti, Chris Tilden
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
 * package org.lastpod;
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

    /**
     * Default constructor
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
     * @return Returns the artist.
     */
    public String getArtist() {
        return artist;
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
     * @return Returns the track.
     */
    public String getTrack() {
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
        tempstring += ("Artist: " + artist + "\n");
        tempstring += ("Album: " + album + "\n");
        tempstring += ("Track: " + track + "\n");
        tempstring += ("Play Count: " + playcount + "\n");
        tempstring += ("Last Played: " + new Date(lastplayed * 1000) + "\n");

        return tempstring;
    }
}
