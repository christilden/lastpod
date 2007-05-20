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
package org.lastpod.action;

import java.awt.event.ActionEvent;

import java.io.IOException;

import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

/**
 * A <code>javax.swing.Action</code> class that is used to open the
 * PreferencesEditor.
 * @author Chris Tilden
 */
public class ExitApplication extends AbstractAction {
    /**
     * Required for serializable classes.
     */
    public static final long serialVersionUID = 200705171718L;

    /**
     * Constructs this action.
     * @param mainAppFrame  The main frame for this application.
     * @param text  The action's text.
     * @param icon  The action's icon.
     * @param desc  The action's detailed description.
     * @param mnemonic  The action's mnemonic.
     */
    public ExitApplication(String text, ImageIcon icon, String desc, int mnemonic) {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
    }

    /**
     * Performs some processing when the action is triggered.
     * @param e  The event that triggered the action.
     */
    public void actionPerformed(ActionEvent e) {
        launchItunes();
        System.exit(0);
    }

    /**
     * Launches iTunes if the user has specified this in their preferences.
     */
    private void launchItunes() {
        Preferences fPrefs = Preferences.userRoot().node("ws/afterglo/audioPod");
        String iTunesStatus = fPrefs.get("iTunes Status", "Disabled");

        if (iTunesStatus.equals("Enabled")) {
            String iTunesPath = fPrefs.get("iT Path", "default");

            if (!iTunesPath.endsWith("iTunes.exe")) {
                iTunesPath += "\\iTunes.exe";
            }

            try {
                Runtime rt = Runtime.getRuntime();
                rt.exec(iTunesPath);
            } catch (IOException e) {
                System.out.println(iTunesPath + " not found!  Cannot launch iTunes.");
            }
        }
    }
}
