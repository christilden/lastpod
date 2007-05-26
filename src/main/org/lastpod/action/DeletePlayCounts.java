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

import org.lastpod.Model;
import org.lastpod.UI;

import java.awt.event.ActionEvent;

import java.io.File;

import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * A <code>javax.swing.Action</code> class that is used to delete the play
 * counts file from the iPod.
 * @author Chris Tilden
 */
public class DeletePlayCounts extends AbstractAction {
    /**
     * Required for serializable classes.
     */
    public static final long serialVersionUID = 200705171718L;

    /**
     * The application's user interface.
     */
    private UI userInterface = null;

    /**
     * The application's model.
     */
    private Model model = null;

    /**
     * Constructs this action.
     * @param userInterface  The application's user interface.
     * @param model  The application's model.
     * @param text  The action's text.
     * @param icon  The action's icon.
     * @param desc  The action's detailed description.
     * @param mnemonic  The action's mnemonic.
     */
    public DeletePlayCounts(UI userInterface, Model model, String text, ImageIcon icon,
        String desc, int mnemonic) {
        super(text, icon);
        this.userInterface = userInterface;
        this.model = model;
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
    }

    /**
     * Performs some processing when the action is triggered.
     * @param e  The event that triggered the action.
     */
    public void actionPerformed(ActionEvent e) {
        int choice = JOptionPane.NO_OPTION;
        String message = "Are you sure you want to\npermanently delete your Play Counts?";
        String title = "Confirm delete";
        int opt = JOptionPane.YES_NO_OPTION;
        choice = JOptionPane.showConfirmDialog(userInterface.getFrame(), message, title, opt);

        if (choice == JOptionPane.YES_OPTION) {
            deletePlayCounts();
        }
    }

    /**
     * Deletes the play counts file from the iPod.
     */
    private void deletePlayCounts() {
        Preferences fPrefs = Preferences.userRoot().node("ws/afterglo/audioPod");
        String iTunesPath = fPrefs.get("iTunes Path", "default");

        if (!iTunesPath.endsWith(File.separator)) {
            iTunesPath += File.separator;
        }

        File playCountsFile = new File(iTunesPath + "Play Counts");

        boolean succuss = false;

        if (playCountsFile.exists()) {
            succuss = playCountsFile.delete();
        }

        if (succuss) {
            /* Clear recent track list. */
            model.clearRecentlyPlayed();

            /* Refresh track list. */
            userInterface.newTrackListAvailable(model.getRecentlyPlayed());
        } else {
            String message = "The play counts file was not deleted.";
            String title = "Delete failure";
            int opt = JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog(userInterface.getFrame(), message, title, opt);
        }
    }
}
