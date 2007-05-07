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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests the <code>ChunkUtil</code> and <code>Chunk</code> functionality.
 * @author Chris Tilden
 */
public class ChunkTest extends TestCase {
    /**
     * Returns a JUnit TestSuite for this test case.
     * @return  A JUnit TestSuite for this test case.
     */
    public static Test suite() {
        return new TestSuite(ChunkTest.class);
    }

    /**
     * Tests chunking a null list.
     */
    public void testNullList() {
        List chunks = ChunkUtil.createChunks(null, 10);
        assertTrue(chunks.size() == 0);
    }

    /**
     * Tests chunking a list containing 9 items.
     */
    public void testNineItemList() {
        List originalList = new ArrayList();

        for (int i = 0; i < 9; i++) {
            originalList.add("Test");
        }

        List chunks = ChunkUtil.createChunks(originalList, 10);

        assertTrue(chunks.size() == 1);
        assertTrue(((Chunk) chunks.get(0)).isFirstChunk());
        assertEquals(((Chunk) chunks.get(0)).getChunkNumber(), 1);
        assertEquals(((Chunk) chunks.get(0)).getChunkSize(), 9);
        assertEquals(((Chunk) chunks.get(0)).getContent().size(), 9);
        assertTrue(((Chunk) chunks.get(0)).isLastChunk());
    }

    /**
     * Tests chunking a list containing 10 items.
     */
    public void testTenItemList() {
        List originalList = new ArrayList();

        for (int i = 0; i < 10; i++) {
            originalList.add("Test");
        }

        List chunks = ChunkUtil.createChunks(originalList, 10);

        assertTrue(chunks.size() == 1);
        assertTrue(((Chunk) chunks.get(0)).isFirstChunk());
        assertEquals(((Chunk) chunks.get(0)).getChunkNumber(), 1);
        assertEquals(((Chunk) chunks.get(0)).getChunkSize(), 10);
        assertEquals(((Chunk) chunks.get(0)).getContent().size(), 10);
        assertTrue(((Chunk) chunks.get(0)).isLastChunk());
    }

    /**
     * Tests chunking a list containing 11 items.
     */
    public void testElevenItemList() {
        List originalList = new ArrayList();

        for (int i = 0; i < 11; i++) {
            originalList.add("Test");
        }

        List chunks = ChunkUtil.createChunks(originalList, 10);

        assertTrue(chunks.size() == 2);
        assertTrue(((Chunk) chunks.get(0)).isFirstChunk());
        assertEquals(((Chunk) chunks.get(0)).getChunkNumber(), 1);
        assertEquals(((Chunk) chunks.get(0)).getChunkSize(), 10);
        assertEquals(((Chunk) chunks.get(0)).getContent().size(), 10);
        assertFalse(((Chunk) chunks.get(0)).isLastChunk());

        assertFalse(((Chunk) chunks.get(1)).isFirstChunk());
        assertEquals(((Chunk) chunks.get(1)).getChunkNumber(), 2);
        assertEquals(((Chunk) chunks.get(1)).getChunkSize(), 1);
        assertEquals(((Chunk) chunks.get(1)).getContent().size(), 1);
        assertTrue(((Chunk) chunks.get(1)).isLastChunk());
    }

    /**
     * Tests chunking a list containing 50 items.
     */
    public void testFiftyItemList() {
        List originalList = new ArrayList();

        for (int i = 0; i < 50; i++) {
            originalList.add("Test");
        }

        List chunks = ChunkUtil.createChunks(originalList, 10);

        assertTrue(chunks.size() == 5);
        assertTrue(((Chunk) chunks.get(0)).isFirstChunk());
        assertEquals(((Chunk) chunks.get(0)).getChunkNumber(), 1);
        assertEquals(((Chunk) chunks.get(0)).getChunkSize(), 10);
        assertEquals(((Chunk) chunks.get(0)).getContent().size(), 10);
        assertFalse(((Chunk) chunks.get(0)).isLastChunk());

        for (int i = 1; i < 4; i++) {
            assertFalse(((Chunk) chunks.get(i)).isFirstChunk());
            assertEquals(((Chunk) chunks.get(i)).getChunkNumber(), i + 1);
            assertEquals(((Chunk) chunks.get(i)).getChunkSize(), 10);
            assertEquals(((Chunk) chunks.get(i)).getContent().size(), 10);
            assertFalse(((Chunk) chunks.get(i)).isLastChunk());
        }

        assertFalse(((Chunk) chunks.get(4)).isFirstChunk());
        assertEquals(((Chunk) chunks.get(4)).getChunkNumber(), 5);
        assertEquals(((Chunk) chunks.get(4)).getChunkSize(), 10);
        assertEquals(((Chunk) chunks.get(4)).getContent().size(), 10);
        assertTrue(((Chunk) chunks.get(4)).isLastChunk());
    }

    /**
     * Tests chunking a list containing 59 items.
     */
    public void testFiftyNineItemList() {
        List originalList = new ArrayList();

        for (int i = 0; i < 59; i++) {
            originalList.add("Test");
        }

        List chunks = ChunkUtil.createChunks(originalList, 10);

        assertTrue(chunks.size() == 6);
        assertTrue(((Chunk) chunks.get(0)).isFirstChunk());
        assertEquals(((Chunk) chunks.get(0)).getChunkNumber(), 1);
        assertEquals(((Chunk) chunks.get(0)).getChunkSize(), 10);
        assertEquals(((Chunk) chunks.get(0)).getContent().size(), 10);
        assertFalse(((Chunk) chunks.get(0)).isLastChunk());

        for (int i = 1; i < 5; i++) {
            assertFalse(((Chunk) chunks.get(i)).isFirstChunk());
            assertEquals(((Chunk) chunks.get(i)).getChunkNumber(), i + 1);
            assertEquals(((Chunk) chunks.get(i)).getChunkSize(), 10);
            assertEquals(((Chunk) chunks.get(i)).getContent().size(), 10);
            assertFalse(((Chunk) chunks.get(i)).isLastChunk());
        }

        assertFalse(((Chunk) chunks.get(5)).isFirstChunk());
        assertEquals(((Chunk) chunks.get(5)).getChunkNumber(), 6);
        assertEquals(((Chunk) chunks.get(5)).getChunkSize(), 9);
        assertEquals(((Chunk) chunks.get(5)).getContent().size(), 9);
        assertTrue(((Chunk) chunks.get(5)).isLastChunk());
    }
}
