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

import org.lastpod.action.DeletePlayCounts;
import org.lastpod.action.ExitApplication;
import org.lastpod.action.OpenPreferencesEditor;
import org.lastpod.action.SubmitTracks;
import org.lastpod.action.UnselectAll;
import org.lastpod.action.ViewLog;

import org.lastpod.util.SwingUtils;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

/**
 * Contains the LastPod user interface. (UI)  The UI interacts with the LastPod
 * controller.
 * @author muti
 * @author Chris Tilden
 * @version $Id$
 */
public class UI implements ChunkProgress {
    private RecentPanel recentpanel;

    /**
     * A status label for the submission.
     */
    private JLabel submitStatus = null;

    /**
     * Displays the progress of the submit.
     */
    private JProgressBar progressBar = null;

    /**
     * The label used to display the idle and busy icons.
     */
    private JLabel statusAnimationLabel;
    private JFrame frame;

    /**
     * The action that opens the PreferencesEditor.
     */
    private final Action actionOpenPreferences;

    /**
     * The action that unselects all tracks.
     */
    private final Action actionUnselectAll;

    /**
     * The action that submits tracks to Last.fm.
     */
    private final Action actionSubmitTracks;

    /**
     * The action that views the log.
     */
    private final Action actionViewLog;

    /**
     * The action that deletes the iPod's play counts file.
     */
    private final Action actionDeletePlayCounts;

    /**
     * The action that exits the application.
     */
    private final Action actionExit;

    /**
     * Constructs the user interface and some icon elements.
     */
    public UI(Model model) {
        frame = new JFrame("LastPod");

        submitStatus = new JLabel();

        ImageIcon idleIcon = SwingUtils.createImageIcon(UI.class, "images/busyicons/idle-icon.png");
        statusAnimationLabel = new JLabel();
        statusAnimationLabel.setIcon(idleIcon);

        ImageIcon iconOpenPreferences =
            SwingUtils.createImageIcon(UI.class, "images/preferences-desktop.png");
        ImageIcon iconUnselectAll = SwingUtils.createImageIcon(UI.class, "images/stock_to-top.png");
        ImageIcon iconSubmitTracks =
            SwingUtils.createImageIcon(UI.class, "images/applications-system.png");
        ImageIcon iconDeletePlayCounts =
            SwingUtils.createImageIcon(UI.class, "images/stock_data-delete-table.png");
        ImageIcon iconExit = SwingUtils.createImageIcon(UI.class, "images/application-exit.png");

        actionOpenPreferences = new OpenPreferencesEditor(this, model, "Preferences",
                iconOpenPreferences, "Opens Preferences Editor", KeyEvent.VK_P);
        actionUnselectAll = new UnselectAll(frame, model, "Unselect All", iconUnselectAll,
                "Unselects All Tracks", KeyEvent.VK_A);
        actionSubmitTracks = new SubmitTracks(this, model, "Submit Tracks", iconSubmitTracks,
                "Submits the selected tracks to Last.fm", KeyEvent.VK_S);
        actionViewLog = new ViewLog(this, "View Log", iconOpenPreferences,
                "Opens Preferences Editor", KeyEvent.VK_L);
        actionDeletePlayCounts = new DeletePlayCounts(this, model, "Delete Play Counts",
                iconDeletePlayCounts, "Removes the play counts file from the iPod.", KeyEvent.VK_D);
        actionExit = new ExitApplication(frame, "Exit", iconExit,
                "Exits the application.  May launch iTunes", KeyEvent.VK_X);
    }

    /**
     * Gets the user interface's JFrame.
     * @return  The user interface's JFrame.
     */
    public JFrame getFrame() {
        return frame;
    }

    public void buildUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);

        /* If enabled launch iTunes after exiting the application. */
        frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent windowEvent) {
                    actionExit.actionPerformed(null);
                }
            });

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = new Dimension(screenSize);

        frameSize.width *= 0.90;
        frameSize.height *= 0.85;
        frame.setSize(frameSize);

        /* Centers the interface on the screen. */
        int x = (screenSize.width / 2) - (frame.getWidth() / 2);
        int y = (screenSize.height / 2) - (frame.getHeight() / 2);
        frame.setLocation(x, y);

        GridBagLayout layout = new GridBagLayout();
        frame.getContentPane().setLayout(layout);

        GridBagConstraints c = new GridBagConstraints();

        JMenuBar menuBar = new JMenuBar();
        JMenu optionsMenu = new JMenu("Options");
        optionsMenu.setMnemonic(KeyEvent.VK_O);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);

        optionsMenu.add(new JMenuItem(actionOpenPreferences));
        optionsMenu.addSeparator();
        optionsMenu.add(new JMenuItem(actionExit));

        editMenu.add(new JMenuItem(actionUnselectAll));
        editMenu.addSeparator();
        editMenu.add(new JMenuItem(actionDeletePlayCounts));

        menuBar.add(optionsMenu);
        menuBar.add(editMenu);
        frame.setJMenuBar(menuBar);

        JToolBar toolBar = new JToolBar();
        layout.setConstraints(toolBar, c);

        JButton button;
        button = new JButton(actionOpenPreferences);
        layout.setConstraints(button, c);
        toolBar.add(button);

        toolBar.addSeparator();

        button = new JButton(actionUnselectAll);
        layout.setConstraints(button, c);
        toolBar.add(button);

        toolBar.addSeparator();

        button = new JButton(actionSubmitTracks);
        layout.setConstraints(button, c);
        toolBar.add(button);

        toolBar.addSeparator();

        button = new JButton(actionViewLog);
        layout.setConstraints(button, c);
        toolBar.add(button);

        toolBar.addSeparator();

        button = new JButton(actionDeletePlayCounts);
        layout.setConstraints(button, c);
        toolBar.add(button);

        toolBar.addSeparator();

        button = new JButton(actionExit);
        layout.setConstraints(button, c);
        toolBar.add(button);

        frame.getContentPane().add(toolBar);

        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;

        this.recentpanel = new RecentPanel();
        layout.setConstraints(this.recentpanel, c);
        frame.getContentPane().add(this.recentpanel);

        c.gridwidth = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(3, 10, 4, 5);
        layout.setConstraints(submitStatus, c);
        frame.getContentPane().add(submitStatus);

        c.gridx = 2;
        c.anchor = GridBagConstraints.LAST_LINE_END;

        JPanel statusBar = new JPanel();
        statusBar.setLayout(layout);
        layout.setConstraints(statusBar, c);

        progressBar = new JProgressBar();
        progressBar.setMinimumSize(new Dimension(150, 0));

        c.gridx = 0;
        c.gridy = 0;
        layout.setConstraints(progressBar, c);
        statusBar.add(progressBar);

        c.gridx = 1;
        c.insets = new Insets(3, 0, 3, 4);
        layout.setConstraints(statusAnimationLabel, c);
        statusBar.add(statusAnimationLabel);

        frame.getContentPane().add(statusBar);
    }

    public void makeVisable() {
        frame.setVisible(true);
    }

    public void newTrackListAvailable(List recentlyPlayed) {
        recentpanel.newTrackListAvailable(recentlyPlayed);
    }

    public JTextArea getLogtextarea() {
        return ((ViewLog) actionViewLog).getLogTextArea();
    }

    public UnselectAll getUnselectAll() {
        return (UnselectAll) actionUnselectAll;
    }

    public JLabel getStatusAnimationLabel() {
        return statusAnimationLabel;
    }

    public JLabel getSubmitStatus() {
        return submitStatus;
    }

    /**
     * When the worker needs to update the GUI we do so by queuing
     * a Runnable for the event dispatching thread with
     * SwingUtilities.invokeLater().  In this case we're just
     * changing the progress bars value.
     * @param currentChunk  The progress bar value.
     */
    public void updateCurrentChunk(final int currentChunk) {
        Runnable doSetProgressBarValue =
            new Runnable() {
                public void run() {
                    progressBar.setValue(currentChunk);
                }
            };

        SwingUtilities.invokeLater(doSetProgressBarValue);
    }

    /**
     * Sets the number of chunks to be submitted.
     * @param numberOfChunks  The number of chunks to be submitted.
     */
    public void setNumberOfChunks(final int numberOfChunks) {
        progressBar.setMaximum(numberOfChunks);
    }

    /**
     * Set to <code>true</code> if the submission was successful.
     * @param completionStatus  <code>true</code> if the submission was
     * successful.
     */
    public void setCompletionStatus(boolean completionStatus) {
        ((ExitApplication) actionExit).setSubmissionSuccessful(completionStatus);
    }
}
