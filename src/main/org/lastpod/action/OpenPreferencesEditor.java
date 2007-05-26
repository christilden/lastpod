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

import org.lastpod.PreferencesEditor;
import org.lastpod.UI;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

/**
 * A <code>javax.swing.Action</code> class that is used to open the
 * PreferencesEditor.
 * @author Chris Tilden
 */
public class OpenPreferencesEditor extends AbstractAction {
    /**
     * Required for serializable classes.
     */
    public static final long serialVersionUID = 200705171718L;

    /**
     * The application's user interface.
     */
    private UI userInterface = null;

    /**
     * Constructs this action.
     * @param userInterface  The application's user interface.
     * @param text  The action's text.
     * @param icon  The action's icon.
     * @param desc  The action's detailed description.
     * @param mnemonic  The action's mnemonic.
     */
    public OpenPreferencesEditor(UI userInterface, String text, ImageIcon icon, String desc,
        int mnemonic) {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        this.userInterface = userInterface;
    }

    /**
     * Performs some processing when the action is triggered.
     * @param e  The event that triggered the action.
     */
    public void actionPerformed(ActionEvent e) {
        PreferencesEditor prefeditor = new PreferencesEditor(userInterface);
        prefeditor.buildUI();
    }
}
