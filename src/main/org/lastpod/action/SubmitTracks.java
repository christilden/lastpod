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

import org.lastpod.util.SwingUtils;
import org.lastpod.util.SwingWorker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.Timer;

/**
 * A <code>javax.swing.Action</code> class that is used to submit selected
 * tracks to Last.fm.
 * @author Chris Tilden
 */
public class SubmitTracks extends AbstractAction {
    /**
     * Required for serializable classes.
     */
    public static final long serialVersionUID = 200705180016L;

    /**
     * The application's user interface.
     */
    private UI userInterface = null;

    /**
     * The application's model.
     */
    private Model model = null;

    /**
     * A timer used for the busy icon.
     */
    private Timer busyIconTimer = null;

    /**
     * The idle icon.
     */
    private Icon idleIcon;

    /**
     * A series of 15 busy icons.
     */
    private Icon[] busyIcons = new Icon[15];

    /**
     * The current busy icon.
     */
    private int busyIconIndex = 0;

    /**
     * This worker is used to perform the submission and is a nice threaded
     * implementation.
     */
    private SwingWorker worker;

    /**
     * Constructs this action.
     * @param userInterface  The application's user interface.
     * @param model  The application's model.
     * @param text  The action's text.
     * @param icon  The action's icon.
     * @param desc  The action's detailed description.
     * @param mnemonic  The action's mnemonic.
     */
    public SubmitTracks(UI userInterface, Model model, String text, ImageIcon icon, String desc,
        int mnemonic) {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        this.userInterface = userInterface;
        this.model = model;

        idleIcon = SwingUtils.createImageIcon(UI.class, "images/busyicons/idle-icon.png");
        setupBusyIcon();
    }

    private void setupBusyIcon() {
        int busyAnimationRate = 30;

        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = SwingUtils.createImageIcon(UI.class,
                    "images/busyicons/busy-icon" + i + ".png");
        }

        busyIconTimer =
            new Timer(busyAnimationRate,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                        userInterface.getStatusAnimationLabel().setIcon(busyIcons[busyIconIndex]);
                    }
                });
    }

    /**
     * Performs some processing when the action is triggered.
     * @param e  The event that triggered the action.
     */
    public void actionPerformed(ActionEvent e) {
        setEnabled(false);

        /* Invoking start() on the SwingWorker causes a new Thread
         * to be created that will call construct(), and then
         * finished().  Note that finished() is called even if
         * the worker is interrupted because we catch the
         * InterruptedException in doWork().
         */
        worker =
            new SwingWorker() {
                    public Object construct() {
                        userInterface.getStatusAnimationLabel().setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();

                        return model.submitTracks(userInterface);
                    }

                    public void finished() {
                        busyIconTimer.stop();
                        userInterface.getStatusAnimationLabel().setIcon(idleIcon);
                        userInterface.getUnselectAll().reset();
                        setEnabled(true);
                    }
                };
        worker.start();
    }
}
