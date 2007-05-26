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

import org.lastpod.UI;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * A <code>javax.swing.Action</code> class that is used to view the application
 * log.
 * @author Chris Tilden
 */
public class ViewLog extends AbstractAction {
    /**
     * Required for serializable classes.
     */
    public static final long serialVersionUID = 200705260158L;

    /**
     * The application's user interface.
     */
    private UI userInterface = null;
    private JDialog frame;
    private JTextArea logTextArea;

    /**
     * Constructs this action.
     * @param userInterface  The application's user interface.
     * @param text  The action's text.
     * @param icon  The action's icon.
     * @param desc  The action's detailed description.
     * @param mnemonic  The action's mnemonic.
     */
    public ViewLog(UI userInterface, String text, ImageIcon icon, String desc, int mnemonic) {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        this.userInterface = userInterface;

        logTextArea = new JTextArea("=====LOG=====\n");
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);
        logTextArea.setEditable(false);
    }

    public JTextArea getLogTextArea() {
        return logTextArea;
    }

    /**
     * Performs some processing when the action is triggered.
     * @param e  The event that triggered the action.
     */
    public void actionPerformed(ActionEvent e) {
        frame = new JDialog(userInterface.getFrame(), "Log View", true);

        JScrollPane scrollpane = new JScrollPane(logTextArea);
        frame.getContentPane().add(scrollpane);

        frame.pack();
        frame.setSize(800, 400);

        int x =
            (int) ((Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (frame.getWidth() / 2));
        int y =
            (int) ((Toolkit.getDefaultToolkit().getScreenSize().height / 2)
            - (frame.getHeight() / 2));
        frame.setLocation(x, y);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
