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


/**
 * @author morgan guerin: morgan_guerin@yahoo.fr
 */

package org.lastpod;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class History {

	private Logger logger = Logger.getLogger(this.getClass().getPackage()
			.getName());

	private static History _instance = null;
	private static final String URL = "history.txt";
	private File historyFile = null;
	private List histories = null;

	public static History getInstance() {
		if (_instance == null) {
			_instance = new History(new File(URL));
			_instance.load();
		}
		return _instance;
	}

	private History(File historyFile) {
		this.historyFile = historyFile;
		histories = new ArrayList();
	}

	private void load() {

		try {
			FileInputStream fis = new FileInputStream(historyFile);

			// Here BufferedInputStream is added for fast reading.
			BufferedInputStream bis = new BufferedInputStream(fis);
			DataInputStream dis = new DataInputStream(bis);

			// dis.available() returns 0 if the file does not have more lines.
			while (dis.available() != 0) {
				histories.add(dis.readLine());
			}

			// dispose all the resources after using them.
			fis.close();
			bis.close();
			dis.close();

		} catch (FileNotFoundException e) {
			logger.warning("Can't find history file");
		} catch (IOException e) {
			logger.warning("Can't read history file");
		}
	}

	public void addhistory(long historyTime) {
		histories.add(Long.toString(historyTime));
	}

	public void write() {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(historyFile));

			for (int i = 0; i < histories.size(); i++) {
				out.write((String) histories.get(i) + "\n");
			}

			out.close();
		} catch (IOException e) {
			logger.warning("Error while writting in history file");
		}
	}

	public boolean isInHistory(long historyTime) {
		return histories.contains(Long.toString(historyTime));
	}

}
