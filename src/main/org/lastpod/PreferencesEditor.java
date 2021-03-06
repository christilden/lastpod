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

import org.lastpod.util.MiscUtilities;
import org.lastpod.util.SpringUtilities;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.File;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

/**
 * @author Muti
 * @version $Id: /lastpod/local/src/main/org/lastpod/PreferencesEditor.java 7692
 *          2007-05-07T05:45:17.332896Z chris $
 */
public class PreferencesEditor {
    private Preferences fPrefs = Preferences.userRoot().node("ws/afterglo/audioPod");

    /**
     * The application's user interface.
     */
    private UI userInterface = null;

    /**
     * The application's model.
     */
    private Model model = null;
    private JDialog frame;
    private SpringLayout layout;
    private JTextField userfield;
    private JPasswordField passfield;
    private JTextField dbfield;
    private JTextField backupUrlField;
    private JCheckBox parseVariousArtistsCheck;
    private JCheckBox parseMultiPlayTracksCheck;
    private JTextField iTunesfield;
    private JTextField parseVariousArtistsField;
    private JCheckBox iTCheck;
    private JLabel iTunesStatus;
    private JLabel parseVariousArtistsStatus;
    private JButton browsebuttoniTunes;

    /**
     * Constructs this object.
     * @param userInterface  The application's user interface.
     * @param model  The application's model.
     */
    public PreferencesEditor(UI userInterface, Model model) {
        this.userInterface = userInterface;
        this.model = model;
    }

    public void buildUI() {
        this.frame = new JDialog(userInterface.getFrame(), "Preferences...", true);
        this.layout = new SpringLayout();
        this.frame.getContentPane().setLayout(this.layout);

        this.addElements();
        this.matchPreferences(); //gets preferences from LastPod and updates UI

        this.frame.pack();

        int x =
            (int) ((Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (frame.getWidth() / 2));
        int y =
            (int) ((Toolkit.getDefaultToolkit().getScreenSize().height / 2)
            - (frame.getHeight() / 2));
        frame.setLocation(x, y);
        this.frame.setResizable(false);
        this.frame.setVisible(true);
    }

    private void addElements() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(true);
        this.frame.setContentPane(p);

        //Username and password panel
        JPanel p2 = new JPanel();
        p2.setLayout(new SpringLayout());

        TitledBorder b2 = BorderFactory.createTitledBorder("AudioScrobbler/Last.fm:");
        p2.setBorder(b2);
        p2.setToolTipText(
            "<html>This entry is used if you want to <b>Submit Tracks</b> to AudioScrobbler/Last.fm");

        JLabel userlabel = new JLabel("Username:");
        p2.add(userlabel);
        this.userfield = new JTextField();
        p2.add(this.userfield);

        JLabel passlabel = new JLabel("Password:");
        p2.add(passlabel);
        this.passfield = new JPasswordField();
        p2.add(this.passfield);

        SpringUtilities.makeCompactGrid(p2, 2, 2, 5, 4, 3, 4);
        p.add(p2);

        //iPod Panel
        JPanel p1 = new JPanel();
        p1.setLayout(new SpringLayout());

        TitledBorder b1 = BorderFactory.createTitledBorder("iPod:");
        p1.setBorder(b1);
        p1.setToolTipText(
            "<html>Path of the iTunesDB:<br>Located on your iPod, e.g. <b>M</b>:\\iPod_Control\\iTunes,<br>where M is the drive letter of your iPod.");

        JLabel dblabel = new JLabel("Location of iTunesDB:");
        p1.add(dblabel);
        this.dbfield = new JTextField();
        this.dbfield.setPreferredSize(new Dimension(200, 20));
        p1.add(this.dbfield);

        JButton BrowseButtoniPod = new JButton("Browse..");
        BrowseButtoniPod.setMnemonic(KeyEvent.VK_B);
        BrowseButtoniPod.addActionListener(new BrowseButtonListeneriPod());
        p1.add(BrowseButtoniPod);
        SpringUtilities.makeCompactGrid(p1, 1, 3, 5, 4, 3, 4);
        p.add(p1);

        //iTunes Panel
        JPanel p4 = new JPanel();
        p4.setLayout(new SpringLayout());
        p.add(new JLabel());

        TitledBorder b4 = BorderFactory.createTitledBorder("iPod Manager (iTunes, gtkpod, etc):");
        p4.setBorder(b4);
        p4.setToolTipText("Enable if you want to use lastpod with an iPod Manager.");

        JPanel p41 = new JPanel();
        iTCheck = new JCheckBox();
        iTCheck.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (iTCheck.isSelected()) {
                        iTunesStatus.setText("Enabled");
                        iTunesfield.setEditable(true);
                        browsebuttoniTunes.setEnabled(true);
                    } else {
                        iTunesStatus.setText("Disabled");
                        iTunesfield.setEditable(false);
                        browsebuttoniTunes.setEnabled(false);
                    }
                }
            });
        p41.add(iTCheck);
        iTunesStatus = new JLabel();
        iTunesStatus.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    if (iTunesStatus.getText().equals("Disabled")) {
                        iTCheck.setSelected(true);
                        iTunesStatus.setText("Enabled");
                        iTunesfield.setEditable(true);
                        browsebuttoniTunes.setEnabled(true);
                    } else {
                        iTCheck.setSelected(false);
                        iTunesStatus.setText("Disabled");
                        iTunesfield.setEditable(false);
                        browsebuttoniTunes.setEnabled(false);
                    }
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }

                public void mouseReleased(MouseEvent e) {
                }

                public void mousePressed(MouseEvent e) {
                }
            });
        p41.add(iTunesStatus);

        p4.add(p41);
        p4.add(new JLabel());
        p4.add(new JLabel());

        JLabel exelabel = new JLabel("Location of iPod Manager (such as iTunes.exe):");
        p4.add(exelabel);
        this.iTunesfield = new JTextField();
        this.iTunesfield.setPreferredSize(new Dimension(200, 20));
        p4.add(this.iTunesfield);

        browsebuttoniTunes = new JButton("Browse..");
        browsebuttoniTunes.setMnemonic(KeyEvent.VK_R);
        browsebuttoniTunes.addActionListener(new BrowseButtonListenerITunes());
        p4.add(browsebuttoniTunes);
        SpringUtilities.makeCompactGrid(p4, 2, 3, 5, 4, 3, 4);
        p.add(p4);

        // Various Artists Panel
        JPanel p6 = new JPanel();
        p6.setLayout(new SpringLayout());
        p.add(new JLabel());

        TitledBorder b6 = BorderFactory.createTitledBorder("Various Artists:");
        p6.setBorder(b6);
        p6.setToolTipText("<html>If the parse various artists option is enabled,<br>"
            + "LastPod will parse the track information when the artist is<br>"
            + "\"Various Artists\". The parsing consists of spliting the artist<br>"
            + "and track from the original track String.  (For example, \"Bing<br>"
            + "Crosby - I'll Be Home for Christmas\" becomes artist=Bing<br>"
            + "Crosby and track name=I'll Be Home for Christmas.)<br><br>"
            + "You may also specify a semi-colon seperated list of various<br>"
            + "artist strings.  For example, you could have the following:<br>"
            + "Various Artists;__Soundtracks;__Compilations");

        JPanel p61 = new JPanel();
        parseVariousArtistsCheck = new JCheckBox();
        parseVariousArtistsCheck.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (parseVariousArtistsCheck.isSelected()) {
                        parseVariousArtistsStatus.setText("Enabled");
                        parseVariousArtistsField.setEditable(true);
                    } else {
                        parseVariousArtistsStatus.setText("Disabled");
                        parseVariousArtistsField.setEditable(false);
                    }
                }
            });
        p61.add(parseVariousArtistsCheck);
        parseVariousArtistsStatus = new JLabel();
        parseVariousArtistsStatus.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    if (parseVariousArtistsStatus.getText().equals("Disabled")) {
                        parseVariousArtistsCheck.setSelected(true);
                        parseVariousArtistsStatus.setText("Enabled");
                        parseVariousArtistsField.setEditable(true);
                    } else {
                        parseVariousArtistsCheck.setSelected(false);
                        parseVariousArtistsStatus.setText("Disabled");
                        parseVariousArtistsField.setEditable(false);
                    }
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }

                public void mouseReleased(MouseEvent e) {
                }

                public void mousePressed(MouseEvent e) {
                }
            });
        p61.add(parseVariousArtistsStatus);

        p6.add(p61);
        p6.add(new JLabel());

        JLabel variousArtistsLabel = new JLabel("Various Artists String:");
        p6.add(variousArtistsLabel);
        parseVariousArtistsField = new JTextField();
        parseVariousArtistsField.setPreferredSize(new Dimension(250, 20));
        p6.add(parseVariousArtistsField);

        SpringUtilities.makeCompactGrid(p6, 2, 2, 5, 4, 3, 4);
        p.add(p6);

        //Options Panel
        JPanel p3 = new JPanel();
        p3.setLayout(new SpringLayout());

        TitledBorder b3 = BorderFactory.createTitledBorder("Options:");
        p3.setBorder(b3);

        String toolTip =
            "<html>If a URL is entered the play information will be<br>"
            + "submitted to both Last.fm and the given URL.  This allows one<br>"
            + "to perform a backup of the Last.fm data.<br><br>"
            + "If parse multi-play tracks is enabled, LastPod will generate<br>"
            + "a new track as needed.  This provides more accurate statistics,<br>"
            + "but invalid play times.";
        p3.setToolTipText(toolTip);

        JLabel backupUrlLabel = new JLabel("Backup URL: ");
        p3.add(backupUrlLabel);
        backupUrlField = new JTextField();
        p3.add(backupUrlField);

        JLabel parseMultiPlayTracksLabel = new JLabel("Parse Multi-Play Tracks: ");
        p3.add(parseMultiPlayTracksLabel);
        parseMultiPlayTracksCheck = new JCheckBox();
        parseMultiPlayTracksCheck.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (parseMultiPlayTracksCheck.isSelected()) {
                        parseMultiPlayTracksCheck.setText("Enabled");
                    } else {
                        parseMultiPlayTracksCheck.setText("Disabled");
                    }
                }
            });
        p3.add(parseMultiPlayTracksCheck);

        SpringUtilities.makeCompactGrid(p3, 2, 2, 5, 2, 3, 4);
        p.add(p3);

        JPanel p5 = new JPanel();

        JButton findPathsButton = new JButton("Find Paths");
        findPathsButton.setMnemonic(KeyEvent.VK_F);
        findPathsButton.setToolTipText("Will try to find the paths for iTunesDB and iTunes.exe");
        findPathsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    /* Scan for iTunesDB. */
                    File itdb = new File(dbfield.getText());
                    File itdbFile = new File(dbfield.getText() + "\\Play Counts");
                    boolean dbFound = false;

                    if (itdbFile.isFile() || itdb.isFile()
                            || itdb.getAbsolutePath().endsWith(":\\iPod_Control\\iTunes")) {
                        iTunesfield.setText(itdb.getAbsolutePath());
                        dbFound = true;
                    } else {
                        for (int i = (int) 'B'; i <= (int) 'Z'; i++) {
                            itdb = new File((char) i + ":\\iPod_Control\\iTunes\\iTunesDB");

                            if (itdb.isFile()) {
                                dbfield.setText(itdb.getParent());
                                dbFound = true;

                                break;
                            }
                        }

                        if (!dbFound) {
                            JOptionPane.showMessageDialog(new JFrame(),
                                "iTunesDB location not found. Are you sure your iPod is plugged in?");
                            dbfield.setText("<iPod iTunes Database Location>");
                        }
                    }

                    /* Scan for iTunes. */
                    File itProgram = new File(iTunesfield.getText());
                    File itProgramFile = new File(iTunesfield.getText() + "\\iTunes.exe");

                    if (itProgram.isFile()) {
                        iTunesfield.setText(itProgram.getParent());
                    } else if (itProgramFile.isFile()) {
                        iTunesfield.setText(itProgramFile.getParent());
                    } else {
                        for (int i = (int) 'B'; i <= (int) 'Z'; i++) {
                            itProgram = new File((char) i + ":\\Program Files\\iTunes\\iTunes.exe");

                            if (itProgram.isFile()) {
                                iTunesfield.setText(itProgram.getParent());

                                break;
                            }
                        }
                    }
                }
            });
        p5.add(findPathsButton);

        JButton okbutton = new JButton("OK");
        okbutton.setMnemonic(KeyEvent.VK_O);
        okbutton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    savePreferences();
                    frame.dispose();
                }
            });
        p5.add(okbutton);

        JButton cancelbutton = new JButton("Cancel");
        cancelbutton.setMnemonic(KeyEvent.VK_C);
        cancelbutton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                }
            });
        p5.add(cancelbutton);

        p.add(p5);
    }

    private void matchPreferences() {
        this.userfield.setText(fPrefs.get("Username", "<Username>"));
        this.dbfield.setText(fPrefs.get("iTunes Path", "<iPod iTunes Database Location>"));
        this.backupUrlField.setText(fPrefs.get("backupUrl", ""));
        this.iTunesfield.setText(fPrefs.get("iT Path", ""));
        parseVariousArtistsField.setText(fPrefs.get("variousArtistsString", "Various Artists"));

        if (fPrefs.get("iTunes Status", "Enabled").equals("Enabled")) {
            this.iTunesStatus.setText("Enabled");
            this.iTCheck.setSelected(true);
            this.iTunesfield.setEditable(true);
            this.browsebuttoniTunes.setEnabled(true);
        } else {
            this.iTunesStatus.setText("Disabled");
            this.iTCheck.setSelected(false);
            this.iTunesfield.setEditable(false);
            this.browsebuttoniTunes.setEnabled(false);
        }

        if (fPrefs.get("parseVariousArtists", "1").equals("1")) {
            parseVariousArtistsStatus.setText("Enabled");
            parseVariousArtistsCheck.setSelected(true);
            parseVariousArtistsField.setEditable(true);
        } else {
            parseVariousArtistsStatus.setText("Disabled");
            parseVariousArtistsCheck.setSelected(false);
            parseVariousArtistsField.setEditable(false);
        }

        if (fPrefs.get("parseMultiPlayTracks", "1").equals("1")) {
            parseMultiPlayTracksCheck.setText("Enabled");
            parseMultiPlayTracksCheck.setSelected(true);
        } else {
            parseMultiPlayTracksCheck.setText("Disabled");
            parseMultiPlayTracksCheck.setSelected(false);
        }
    }

    private void savePreferences() {
        String oldItunesPath = fPrefs.get("iTunes Path", "<iPod iTunes Database Location>");
        String newItunesPath = dbfield.getText();

        fPrefs.put("Username", this.userfield.getText());

        String password = new String(passfield.getPassword());

        if (password.length() != 0) {
            String encryptedPassword = MiscUtilities.md5DigestPassword(password);
            fPrefs.put("encryptedPassword", encryptedPassword);
        }

        boolean selected = parseVariousArtistsCheck.isSelected();
        boolean selected2 = parseMultiPlayTracksCheck.isSelected();
        String parseVariousArtists = selected ? "1" : "0";
        String parseMultiPlayTracks = selected2 ? "1" : "0";

        fPrefs.put("iTunes Path", newItunesPath);
        fPrefs.put("backupUrl", this.backupUrlField.getText());
        fPrefs.put("iT Path", this.iTunesfield.getText());
        fPrefs.put("variousArtistsString", parseVariousArtistsField.getText());
        fPrefs.put("iTunes Status", this.iTunesStatus.getText());
        fPrefs.put("parseVariousArtists", parseVariousArtists);
        fPrefs.put("parseMultiPlayTracks", parseMultiPlayTracks);

        try {
            fPrefs.flush();
        } catch (Exception e) {
            Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
            logger.log(Level.WARNING, "Unable to save preferences: " + e.toString());
        }

        /* Only reloads the track list when the iTunes Path has been
         * changed.
         */
        if (!oldItunesPath.equals(newItunesPath)) {
            model.clearRecentlyPlayed();
            model.parsePlayCounts(userInterface);
            userInterface.newTrackListAvailable(model.getRecentlyPlayed());
        }
    }

    private class BrowseButtonListeneriPod implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            fc.setFileHidingEnabled(false);
            fc.setFileFilter(new ItunesdbFilter());

            File d = new File(dbfield.getText());

            if (d.isDirectory()) {
                fc.setCurrentDirectory(d);
            }

            int returnVal = fc.showOpenDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                dbfield.setText(f.getParent());
            }
        }
    }

    private class BrowseButtonListenerITunes implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            fc.setFileHidingEnabled(false);

            File d = new File(iTunesfield.getText());

            if (d.isDirectory()) {
                fc.setCurrentDirectory(d);
            } else if (d.isFile()) {
                fc.setCurrentDirectory(d.getParentFile());
            }

            fc.setFileFilter(new ExeFileFilter());

            int returnVal = fc.showOpenDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                iTunesfield.setText(f.getPath());
            }
        }
    }
}
