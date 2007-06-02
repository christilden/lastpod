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

import org.lastpod.util.IoUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Stores the history of tracks that have been submitted to Last.fm.  Any tracks
 * that are in LastPod that are also in the history file are unchecked by
 * default.  This prevents users from accidentally submiting tracks twice.
 *
 * @author morgan guerin: morgan_guerin@yahoo.fr
 * @author Chris Tilden
 */
public class History {
    private static History _instance = null;
    private static final String URL = "history.txt";
    private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
    private File historyFile = null;
    private List histories = null;
    private List newHistories = null;

    /**
     * Constructs this object with the history file.
     * @param historyFile  This file will be used to read and write the history
     * of tracks that have been submitted to Last.fm.
     */
    private History(File historyFile) {
        this.historyFile = historyFile;
        histories = new ArrayList();
        newHistories = new ArrayList();
    }

    /**
     * Singleton that reads the history file and creates this
     * <code>History</code> object.
     * @param iTunesPath  The path to the iPod's iTunes DB.  LastPod's history
     * data will be stored there.
     * @return  This <code>History</code> object with a List of histories.
     */
    public static History getInstance(String iTunesPath) {
        if (_instance == null) {
            if (iTunesPath == null) {
                throw new RuntimeException("iTunes path was not supplied.");
            }

            if (!iTunesPath.endsWith(File.separator)) {
                iTunesPath += File.separator;
            }

            _instance = new History(new File(iTunesPath + URL));
            _instance.read();
        }

        return _instance;
    }

    /**
     * Reads the history file (persistent storage) and loads it into a
     * <code>List</code>.
     */
    private void read() {
        FileInputStream in = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;

        try {
            in = new FileInputStream(historyFile);

            /* Here BufferedReader is added for fast reading. */
            inputStreamReader = new InputStreamReader(in);
            reader = new BufferedReader(inputStreamReader);

            while (reader.ready()) {
                histories.add(reader.readLine());
            }
        } catch (FileNotFoundException e) {
            logger.warning("Can't find history file");
        } catch (IOException e) {
            logger.warning("Can't read history file");
        } finally {
            /* Dispose of all the resources after using them. */
            IoUtils.cleanup(reader, null);
            IoUtils.cleanup(inputStreamReader, null);
            IoUtils.cleanup(in, null);
        }
    }

    /**
     * Adds a new track to the history list.  This list will be written to the
     * histories file once the tracks are submitted.
     * @param historyTime  The time the track was last played.
     */
    public void addhistory(long historyTime) {
        histories.add(Long.toString(historyTime));
        newHistories.add(Long.toString(historyTime));
    }

    /**
     * Writes the history list to the history file for persistent storage.
     */
    public void write() {
        FileWriter out = null;
        BufferedWriter bufferedWriter = null;

        try {
            out = new FileWriter(historyFile);
            bufferedWriter = new BufferedWriter(out);

            for (int i = 0; i < newHistories.size(); i++) {
                bufferedWriter.write((String) newHistories.get(i) + "\n");
            }
        } catch (IOException e) {
            logger.warning("Error while writting in history file");
        } finally {
            /* Dispose of the resource after using it. */
            IoUtils.cleanup(null, bufferedWriter);
            IoUtils.cleanup(null, out);
        }
    }

    /**
     * Returns <code>true</code> if the track is present in the history list.
     * If it is this means the track has been submitted to Last.fm.
     * @param historyTime  The time the track was last played.
     * @return  <code>true</code> if the track is present in the history list.
     */
    public boolean isInHistory(long historyTime) {
        return histories.contains(Long.toString(historyTime));
    }
}
