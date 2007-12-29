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
package org.lastpod.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Checks for the existence of the file "iTunesStats".
 * @author Chris Tilden
 */
public class ItunesStatsFilter implements FilenameFilter {
    /**
     * Returns <code>true</code> if the file "iTunesStats" is present.
     * @param dir  The directory to check.
     * @param name  The name of the file.
     * @return  <code>true</code> if the file is present.
     */
    public boolean accept(File dir, String name) {
        return (name.equals("iTunesStats"));
    }
}
