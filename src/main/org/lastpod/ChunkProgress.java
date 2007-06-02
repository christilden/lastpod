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


/**
 * An interface to track the Chunk's progress.
 * @author Chris Tilden
 */
public interface ChunkProgress {
    /**
     * When the progress needs to be updated.
     * @param currentChunk  The progress value.
     */
    void updateCurrentChunk(final int currentChunk);

    /**
     * Sets the number of chunks to be submitted.
     * @param numberOfChunks  The number of chunks to be submitted.
     */
    void setNumberOfChunks(final int numberOfChunks);

    /**
     * Set to <code>true</code> if the submission was successful.
     * @param completionStatus  <code>true</code> if the submission was
     * successful.
     */
    void setCompletionStatus(boolean completionStatus);

    /**
     * Sets the message for the submitStatus label.
     * @param submitStatusMessage  The message for the submitStatus label.
     */
    void setSubmitStatusMessage(String submitStatusMessage);
}
