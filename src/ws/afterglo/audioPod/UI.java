package ws.afterglo.audioPod;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author muti
 *
 */
public class UI {
    private RecentPanel	recentpanel;
    private JTextArea	logtextarea;
    private JFrame frame;
    
    public void buildUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame("AudioPod");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenSize.width *= 0.90;
        screenSize.height *= 0.85;
        frame.setSize(screenSize);
        
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
                PreferencesEditor prefeditor = new PreferencesEditor();
                prefeditor.buildUI();
            }
        });
        layout.setConstraints(button, c);
        frame.getContentPane().add(button);
        
        button = new JButton("Unselect All");
        button.setMnemonic(KeyEvent.VK_A);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                JButton button = (JButton) ev.getSource();
                if ("Unselect All".equals(button.getText())) {
                    AudioPod.unselectAll();
                    button.setText("Select All");
                } else if ("Select All".equals(button.getText())) {
                    AudioPod.selectAll();
                    button.setText("Unselect All");
                }

                frame.repaint();
            }
        });
        layout.setConstraints(button, c);
        frame.getContentPane().add(button);
        
        button = new JButton("Submit Tracks");
        button.setMnemonic(KeyEvent.VK_S);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                AudioPod.SubmitTracks();
            }
        });
        layout.setConstraints(button, c);
        frame.getContentPane().add(button);
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
}