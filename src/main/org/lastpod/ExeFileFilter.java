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

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Implements a simple <code>FileFilter</code> to display only .exe files.
 * @author Muti
 * @author Chris Tilden
 */
public class ExeFileFilter extends FileFilter {
    /**
     * Filters a given file and returns <code>true</code> if it should be
     * displayed.
     * @param f  The file to filter.
     * @return  <code>true</code> if the file should be displayed.
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        if (f.getName().endsWith(".exe")) {
            return true;
        }

        return false;
    }

    /**
     * Gets the description that will be displayed in the dialog box.
     * @return  The description.
     */
    public String getDescription() {
        return "*.exe";
    }
}
