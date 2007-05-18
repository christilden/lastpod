package org.lastpod.action;

import org.lastpod.LastPod;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * A <code>javax.swing.Action</code> class that is used to unselect all tracks.
 * @author Chris Tilden
 */
public class UnselectAll extends AbstractAction {
    /**
     * Required for serializable classes.
     */
    public static final long serialVersionUID = 200705171731L;

    /**
     * A reference to the main application frame.
     */
    private JFrame mainAppFrame = null;

    /**
     * If <code>true</code> this action selects all tracks.  If
     * <code>false</code> this action unselects all tracks.
     */
    private Boolean selectAllType = Boolean.FALSE;

    /**
     * Constructs this action.
     * @param mainAppFrame  The main frame for this application.
     * @param text  The action's text.
     * @param icon  The action's icon.
     * @param desc  The action's detailed description.
     * @param mnemonic  The action's mnemonic.
     */
    public UnselectAll(JFrame mainAppFrame, String text, ImageIcon icon, String desc, int mnemonic) {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        this.mainAppFrame = mainAppFrame;
    }

    /**
     * Gets the action's select all type.
     * @return  The action's select all type.
     */
    public Boolean getSelectAllType() {
        return selectAllType;
    }

    /**
     * Sets the action's select all type.
     * @param selectAllType  The action's select all type.
     */
    public void setSelectAllType(Boolean selectAllType) {
        this.selectAllType = selectAllType;
    }

    /**
     * Performs some processing when the action is triggered.
     * @param e  The event that triggered the action.
     */
    public void actionPerformed(ActionEvent e) {
        if (selectAllType.booleanValue()) {
            LastPod.selectAll();
            setSelectAllType(Boolean.FALSE);
            putValue(NAME, "Unselect All");
            putValue(SHORT_DESCRIPTION, "Unselects All Tracks");
        } else {
            LastPod.unselectAll();
            setSelectAllType(Boolean.TRUE);
            putValue(NAME, "Select All");
            putValue(SHORT_DESCRIPTION, "Selects All Tracks");
        }

        mainAppFrame.repaint();
    }
}
