package org.lastpod.action;

import org.lastpod.LastPod;
import org.lastpod.UI;

import org.lastpod.util.SwingUtils;
import org.lastpod.util.SwingWorker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
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
     * The label used to display the idle and busy icons.
     */
    private JLabel statusAnimationLabel;

    /**
     * This worker is used to perform the submission and is a nice threaded
     * implementation.
     */
    private SwingWorker worker;

    /**
     * Constructs this action.
     * @param text  The action's text.
     * @param icon  The action's icon.
     * @param desc  The action's detailed description.
     * @param mnemonic  The action's mnemonic.
     */
    public SubmitTracks(JLabel statusAnimationLabel, String text, ImageIcon icon, String desc,
        int mnemonic) {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        this.statusAnimationLabel = statusAnimationLabel;

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
                        statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
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
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();

                        return LastPod.submitTracks();
                    }

                    public void finished() {
                        busyIconTimer.stop();
                        statusAnimationLabel.setIcon(idleIcon);
                        setEnabled(true);
                    }
                };
        worker.start();
    }
}
