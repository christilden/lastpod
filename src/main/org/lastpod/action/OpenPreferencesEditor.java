package org.lastpod.action;

import org.lastpod.PreferencesEditor;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

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
     * A reference to the main application frame.
     */
    private JFrame mainAppFrame = null;

    /**
     * Constructs this action.
     * @param mainAppFrame  The main frame for this application.
     * @param text  The action's text.
     * @param icon  The action's icon.
     * @param desc  The action's detailed description.
     * @param mnemonic  The action's mnemonic.
     */
    public OpenPreferencesEditor(JFrame mainAppFrame, String text, ImageIcon icon, String desc,
        int mnemonic) {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        this.mainAppFrame = mainAppFrame;
    }

    /**
     * Performs some processing when the action is triggered.
     * @param e  The event that triggered the action.
     */
    public void actionPerformed(ActionEvent e) {
        PreferencesEditor prefeditor = new PreferencesEditor(mainAppFrame);
        prefeditor.buildUI();
    }
}
