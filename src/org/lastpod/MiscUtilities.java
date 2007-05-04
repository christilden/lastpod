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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Miscellaneous utilities used by LastPod.
 * @author Chris Tilden
 * @version $Id$
 */
public class MiscUtilities {
    /**
     * Make sure this class is not instantiated directly.
     */
    private MiscUtilities() {
        /* Make sure this class is not instantiated directly. */
    }

    /**
     * Encodes a byte array into a hexidecimal String.
     * @param array  The byte array to encode.
     * @return  A heidecimal String representing the byte array.
     */
    public static String hexEncode(byte[] array) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < array.length; ++i) {
            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
        }

        return sb.toString();
    }

    /**
     * Creates a MD5 digest String from a given password.
     * @param password  The password to digest.
     * @return  The MD5 digested password.
     */
    public static String md5DigestPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            return hexEncode(md.digest(password.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No MD5 algorithm present on the system");
        }
    }
}
