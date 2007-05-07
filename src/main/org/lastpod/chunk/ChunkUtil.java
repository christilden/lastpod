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
package org.lastpod.chunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A utility class that is used to create a sequence of Chunks from a given
 * List.
 * @author Chris Tilden
 */
public final class ChunkUtil {
    /**
     * The original list.
     */
    private List originalList;

    /**
     * The chunk size.
     */
    private int chunkSize;

    /**
     * Constructs this class with the given parameters.
     * @param originalList  The original list.
     * @param chunkSize  The chunk size.
     */
    private ChunkUtil(final List originalList, final int chunkSize) {
        this.originalList = originalList;
        this.chunkSize = chunkSize;
    }

    /**
     * Creates a List of Chunks from a given original List.
     * @param orignalList  The original list.
     * @param chunkSize  The chunk size.
     * @return  A List of Chunks from a given original List.
     */
    public static List createChunks(final List orignalList, final int chunkSize) {
        if ((orignalList == null) || (orignalList.size() == 0)) {
            return Collections.EMPTY_LIST;
        }

        ChunkUtil chunkUtil = new ChunkUtil(orignalList, chunkSize);

        Chunk chunk = null;
        int totalChunks = chunkUtil.getTotalChunks();

        List chunkList = new ArrayList();

        for (int i = 0; i < totalChunks; i++) {
            chunk = chunkUtil.getNextChunk(chunk);
            chunkList.add(chunk);
        }

        return chunkList;
    }

    /**
     * Gets the next chunk in the sequence.
     * @param currentChunk  The current chunk.
     * @return  The next chunk in the sequence.
     */
    private Chunk getNextChunk(final Chunk currentChunk) {
        if ((originalList == null) || (originalList.size() == 0)) {
            return null;
        }

        if ((currentChunk != null) && currentChunk.isLastChunk()) {
            throw new RuntimeException("Out of Bounds.");
        }

        Chunk currentChunk2 =
            (currentChunk == null) ? new Chunk(0, getTotalChunks(), null) : currentChunk;
        Chunk nextChunk = null;

        List content = iterateFrom(currentChunk2.getChunkNumber() * chunkSize);
        nextChunk = new Chunk(currentChunk2.getChunkNumber() + 1, currentChunk2.getTotalChunks(),
                content);

        return nextChunk;
    }

    /**
     * Creates a sub-List based on the chunkSize and ending size of the original
     * List.
     * @param startIndex  The item to start from within the List.
     * @return  A sub-List of items.
     */
    private List iterateFrom(final int startIndex) {
        final int totalSize = originalList.size();

        int endIndex = startIndex + chunkSize;

        if (endIndex > totalSize) {
            endIndex = totalSize;
        }

        return originalList.subList(startIndex, endIndex);
    }

    /**
     * Gets the total number of chunks in the sequence.
     * @return  The total number of chunks in the sequence.
     */
    private int getTotalChunks() {
        if ((originalList == null) || (originalList.size() <= 0)) {
            return 0;
        }

        final int totalSize = originalList.size();

        return ((totalSize - 1) / chunkSize) + 1;
    }
}
