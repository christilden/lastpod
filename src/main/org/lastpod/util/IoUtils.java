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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import java.math.BigInteger;

/**
 * Contains various I/O utility functions.
 * @author Chris Tilden
 */
public final class IoUtils {
    /**
     * Cannot construct this utility class.
     */
    private IoUtils() {
        /* Default constructor. */
    }

    /**
     * Given a <code>Reader</code> object> and/or <code>Writer</code> object
     * attempt to close the resource.  This method is null safe.
     * @param reader  The resource to attempt to close.
     * @param writer  The resource to attempt to close.
     */
    public static void cleanup(Reader reader, Writer writer) {
        try {
            if (reader != null) {
                reader.close();
                reader = null;
            }
        } catch (IOException e) {
            /* Cannot close, just give up. */
        }

        try {
            if (writer != null) {
                writer.close();
                writer = null;
            }
        } catch (IOException e) {
            /* Cannot close, just give up. */
        }
    }

    /**
     * Given an <code>InputStream</code> and/or <code>OutputStream</code> object
     * attempt to close the resource.  This method is null safe.
     * @param in  The resource to attempt to close.
     * @param out  The resource to attempt to close.
     */
    public static void cleanup(InputStream in, OutputStream out) {
        try {
            if (in != null) {
                in.close();
                in = null;
            }
        } catch (IOException e) {
            /* Cannot close, just give up. */
        }

        try {
            if (out != null) {
                out.close();
                out = null;
            }
        } catch (IOException e) {
            /* Cannot close, just give up. */
        }
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
    public static void skipFully(InputStream stream, long bytes)
            throws IOException {
        for (long i = stream.skip(bytes); i < bytes; i += stream.skip(bytes - i)) {
            /* The loop itself performs all the logic needed to skip. */
        }
    }
}
