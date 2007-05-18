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

    //TODO: This method is also in UI.java, remove one.
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
