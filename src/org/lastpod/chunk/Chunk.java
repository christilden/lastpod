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
 */
package org.lastpod.chunk;

import java.util.List;

/**
 * Stores a chunk of tracks to be submitted to Last.fm
 * @author Chris Tilden
 */
public class Chunk {
    /**
     * The number of this chunk.  (In the sequence of chunks).
     */
    private int chunkNumber;

    /**
     * The total number of chunks in this sequence.
     */
    private int totalChunks;

    /**
     * The tracks that make up this chunk.
     */
    private List content;

    /**
     * Constructs a chunk using the given parameters.
     * @param chunkNumber  The number of this chunk.  (In the sequence of chunks).
     * @param totalPages  The total number of chunks in this sequence.
     * @param content  The tracks that make up this chunk.
     */
    public Chunk(final int chunkNumber, final int totalPages, final List content) {
        this.chunkNumber = chunkNumber;
        this.totalChunks = totalPages;
        this.content = content;
    }

    /**
     * Gets the number of this chunk.  (In the sequence of chunks).
     * @return  The number of this chunk.  (In the sequence of chunks).
     */
    public int getChunkNumber() {
        return chunkNumber;
    }

    /**
     * Gets the total number of chunks in this sequence.
     * @return  The total number of chunks in this sequence.
     */
    public int getTotalChunks() {
        return totalChunks;
    }

    /**
     * Gets the size of this chunk. (Number of tracks that it contains.)
     * @return  The size of this chunk. (Number of tracks that it contains.)
     */
    public int getChunkSize() {
        return (content == null) ? 0 : content.size();
    }

    /**
     * Gets the tracks that make up this chunk.
     * @return  The tracks that make up this chunk.
     */
    public List getContent() {
        return content;
    }

    /**
     * Returns <code>true</code> if this is the first chunk in the sequence.
     * @return  <code>true</code> if this is the first chunk.
     */
    public boolean isFirstChunk() {
        return chunkNumber == 1;
    }

    /**
     * Returns <code>true</code> if this is the last chunk in the sequence.
     * @return  <code>true</code> if this is the last chunk.
     */
    public boolean isLastChunk() {
        return chunkNumber == totalChunks;
    }
}
