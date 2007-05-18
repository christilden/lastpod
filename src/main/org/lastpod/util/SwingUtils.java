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

import java.net.URL;

import javax.swing.ImageIcon;

/**
 * Contains various <code>javax.swing</code> utility functions.
 * @author Chris Tilden
 */
public final class SwingUtils {
    /**
     * Cannot construct this utility class.
     */
    private SwingUtils() {
        /* Default constructor. */
    }

    /**
     * Returns an ImageIcon, or throws a RuntimeException if the path was
     * invalid.
     * @param clazz  The class to "baseline" the path off of.
     * @param path  The path from the location of the "baseline" class (clazz).
     * @return  An ImageIcon object.
     */
    public static ImageIcon createImageIcon(Class clazz, String path) {
        URL imageUrl = clazz.getResource(path);

        if (imageUrl != null) {
            return new ImageIcon(imageUrl);
        }

        throw new RuntimeException("Couldn't find file: " + path);
    }
}
