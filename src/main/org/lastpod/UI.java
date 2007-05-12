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

import org.lastpod.util.SwingWorker;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;

import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
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
    private JTextArea logtextarea;

    /**
     * The submit button.
     */
    private JButton submitButton;

    /**
     * Displays the progress of the submit.
     */
    private JProgressBar progressBar = null;

    /**
     * This worker is used to perform the submission and is a nice threaded
     * implementation.
     */
    private SwingWorker worker;
    private JFrame frame;

    /**
     * Gets the user interface's JFrame.
     * @return  The user interface's JFrame.
     */
    public JFrame getFrame() {
        return frame;
    }

    public void buildUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame("LastPod");

        /* If enabled launch iTunes after exiting the application. */
        frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent windowEvent) {
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

                    System.exit(0);
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
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;

        this.recentpanel = new RecentPanel();
        layout.setConstraints(this.recentpanel, c);
        frame.getContentPane().add(this.recentpanel);

        c.weighty = 0.5;
        this.logtextarea = new JTextArea("=====LOG=====\n");
        this.logtextarea.setLineWrap(true);
        this.logtextarea.setWrapStyleWord(true);
        this.logtextarea.setEditable(false);

        JScrollPane scrollpane = new JScrollPane(this.logtextarea);
        layout.setConstraints(scrollpane, c);
        frame.getContentPane().add(scrollpane);

        c.gridwidth = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;

        //TODO Add button mnemonics for kbd shortcut access
        JButton button;
        button = new JButton("Preferences..");
        button.setMnemonic(KeyEvent.VK_P);
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    PreferencesEditor prefeditor = new PreferencesEditor(frame);
                    prefeditor.buildUI();
                }
            });
        layout.setConstraints(button, c);
        frame.getContentPane().add(button);

        button = new JButton("Unselect All");
        button.setMnemonic(KeyEvent.VK_A);
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    JButton selectionButton = (JButton) ev.getSource();

                    if ("Unselect All".equals(selectionButton.getText())) {
                        LastPod.unselectAll();
                        selectionButton.setText("Select All");
                    } else if ("Select All".equals(selectionButton.getText())) {
                        LastPod.selectAll();
                        selectionButton.setText("Unselect All");
                    }

                    frame.repaint();
                }
            });
        layout.setConstraints(button, c);
        frame.getContentPane().add(button);

        submitButton = new JButton("Submit Tracks");
        submitButton.setMnemonic(KeyEvent.VK_S);
        submitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    submitButton.setEnabled(false);

                    /* Invoking start() on the SwingWorker causes a new Thread
                     * to be created that will call construct(), and then
                     * finished().  Note that finished() is called even if
                     * the worker is interrupted because we catch the
                     * InterruptedException in doWork().
                     */
                    worker =
                        new SwingWorker() {
                                public Object construct() {
                                    return LastPod.submitTracks();
                                }

                                public void finished() {
                                    submitButton.setEnabled(true);
                                }
                            };
                    worker.start();
                }
            });
        layout.setConstraints(submitButton, c);
        frame.getContentPane().add(submitButton);

        progressBar = new JProgressBar();
        layout.setConstraints(progressBar, c);
        frame.getContentPane().add(progressBar);
    }

    public void makeVisable() {
        frame.setVisible(true);
    }

    public void newTrackListAvailable() {
        this.recentpanel.newTrackListAvailable();
    }

    public JTextArea getLogtextarea() {
        return this.logtextarea;
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
}
