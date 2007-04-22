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
 * package org.lastpod;
 */
package org.lastpod;

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
 * @author morgan guerin: morgan_guerin@yahoo.fr
 * @author Chris Tilden
 */
public class History {

	private Logger logger = Logger.getLogger(this.getClass().getPackage()
			.getName());

	private static History _instance = null;
	private static final String URL = "history.txt";
	private File historyFile = null;
	private List histories = null;
    private List newHistories = null;

	public static History getInstance() {
		if (_instance == null) {
			_instance = new History(new File(URL));
			_instance.read();
		}
		return _instance;
	}

	private History(File historyFile) {
		this.historyFile = historyFile;
		histories = new ArrayList();
        newHistories = new ArrayList();
	}

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

	public void addhistory(long historyTime) {
        newHistories.add(Long.toString(historyTime));
	}

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

	public boolean isInHistory(long historyTime) {
		return histories.contains(Long.toString(historyTime));
	}
}
