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
package org.lastpod;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.File;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 * @author Muti
 * @version $Id$
 */
public class PreferencesEditor {
    private Preferences fPrefs = Preferences.userRoot().node("ws/afterglo/audioPod");
    private JFrame frame;
    private SpringLayout layout;
    private JTextField userfield;
    private JPasswordField passfield;
    private JTextField dbfield;

    public void buildUI() {
        this.frame = new JFrame("Preferences...");
        this.layout = new SpringLayout();
        this.frame.getContentPane().setLayout(this.layout);

        this.addElements();
        this.matchPreferences(); //gets preferences from AudioPod and updates UI

        this.frame.pack();
        this.frame.setVisible(true);
    }

    private void addElements() {
        //TODO add mnemonics to buttons
        JPanel p = new JPanel(new SpringLayout());
        p.setOpaque(true);
        this.frame.setContentPane(p);

        JLabel userlabel = new JLabel("AudioScrobbler Username:");
        p.add(userlabel);
        this.userfield = new JTextField();
        p.add(this.userfield);
        p.add(new JLabel());

        JLabel passlabel = new JLabel("AudioScrobbler Password:");
        p.add(passlabel);
        this.passfield = new JPasswordField();
        p.add(this.passfield);
        p.add(new JLabel());

        JLabel dblabel = new JLabel("Location of iTunesDB:");
        p.add(dblabel);
        this.dbfield = new JTextField();
        p.add(this.dbfield);

        JButton browsebutton = new JButton("Browse..");
        browsebutton.setMnemonic(KeyEvent.VK_R);
        browsebutton.addActionListener(new BrowseButtonListener());
        p.add(browsebutton);

        p.add(new JLabel());

        JButton okbutton = new JButton("OK");
        okbutton.setMnemonic(KeyEvent.VK_O);
        okbutton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    savePreferences();
                    frame.dispose();
                }
            });
        p.add(okbutton);

        JButton cancelbutton = new JButton("Cancel");
        cancelbutton.setMnemonic(KeyEvent.VK_C);
        cancelbutton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                }
            });
        p.add(cancelbutton);

        SpringUtilities.makeCompactGrid(p, 4, 3, 6, 6, 6, 6);
    }

    private void matchPreferences() {
        this.userfield.setText(fPrefs.get("Username", "<Username>"));
        this.passfield.setText(fPrefs.get("Password", ""));
        this.dbfield.setText(fPrefs.get("iTunes Path", "<iPod iTunes Database Location>"));
    }

    private void savePreferences() {
        fPrefs.put("Username", this.userfield.getText());
        fPrefs.put("Password", new String(this.passfield.getPassword()));
        fPrefs.put("iTunes Path", this.dbfield.getText());

        try {
            fPrefs.flush();
        } catch (Exception e) {
            Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
            logger.log(Level.WARNING, "Unable to save preferences: " + e.toString());
        }

        LastPod.ParsePlayCounts();
    }

    private class BrowseButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            fc.setFileHidingEnabled(false);
            fc.setFileFilter(new ItunesdbFilter());

            int returnVal = fc.showOpenDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                dbfield.setText(f.getParent());
            }
        }
    }
}
