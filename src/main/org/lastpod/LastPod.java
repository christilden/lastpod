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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The LastPod controller.
 * @author muti
 * @author Chris Tilden
 * @version $Id$
 */
public class LastPod {
    public final static String NO_PREFS_ERROR =
        "You have not setup your preferences.\n"
        + "Please click Preferences below to configure the location of "
        + "your iTunesDB (it's on your iPod's drive) and your AudioScrobbler "
        + "username and password.";
    private UI userInterface;
    private List recentplayed; //parsed using DbReader class

    /**
     * Loads the recent played information from the iPod and constructs the
     * GUI to display it.
     */
    private void load() {
        recentplayed = new ArrayList();

        Model model = new ModelImpl();
        model.setRecentlyPlayed(recentplayed);
        userInterface = new UI(model);
        userInterface.buildUI();

        Logger logger = Logger.getLogger(LastPod.class.getPackage().getName());
        logger.setLevel(Level.ALL);
        logger.addHandler(new LogHandler(userInterface));

        model.parsePlayCounts(userInterface);

        userInterface.makeVisable();
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    LastPod lastPod = new LastPod();
                    lastPod.load();
                }
            });
    }
}
