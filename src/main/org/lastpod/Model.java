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

import java.util.List;

public interface Model {
    List getRecentlyPlayed();

    void setRecentlyPlayed(List recentlyPlayed);

    /**
     * A utility function to clear the recently played track list.
     * @param recentlyPlayed
     */
    void clearRecentlyPlayed();

    void selectAll();

    void unselectAll();

    /**
     * Parses the play counts and track information from the iPod.
     */
    void parsePlayCounts(UI userInterface);

    /**
     * Submits the tracks to Last.fm
     * @param userInterface  The application's user interface.
     * @param online  submission type:
     *                  true - online, false - offline (to Last.fm client cache)
     * @return  A status message upon completion.
     */
    Object submitTracks(UI userInterface, boolean online);
}
