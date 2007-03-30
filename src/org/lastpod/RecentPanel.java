package org.lastpod;

import java.awt.GridLayout;

import java.text.DateFormat;

import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * @author muti
 *
 */
public class RecentPanel extends JPanel {
    private JTable table;
    private RecentModel model;

    public RecentPanel() {
        super(new GridLayout(1, 1));

        this.model = new RecentModel();
        this.table = new JTable(this.model);
        this.table.getColumnModel().getColumn(0).setMaxWidth(30);
        this.table.getColumnModel().getColumn(1).setMaxWidth(60);
        this.table.getColumnModel().getColumn(5).setMaxWidth(60);
        this.table.getColumnModel().getColumn(1)
                  .setCellRenderer(table.getDefaultRenderer(Boolean.class));
        this.table.getColumnModel().getColumn(1).setCellEditor(table.getDefaultEditor(Boolean.class));

        JScrollPane scrollpane = new JScrollPane(this.table);

        add(scrollpane);
    }

    public void newTrackListAvailable() {
        this.model.fireTableDataChanged();
    }

    private class RecentModel extends AbstractTableModel {
        private String[] columnData =
            new String[] { "#", "Submit", "Artist", "Album", "Track", "Length", "Play Time" };

        public int getColumnCount() {
            return this.columnData.length;
        }

        public int getRowCount() {
            if (AudioPod.recentplayed != null) {
                return AudioPod.recentplayed.size();
            }

            return 0;
        }

        public String getColumnName(int col) {
            return this.columnData[col];
        }

        public boolean isCellEditable(int row, int col) {
            /* Only column 1 is editable. */
            return (col == 1);
        }

        public Object getValueAt(int row, int col) {
            TrackItem track;

            if (AudioPod.recentplayed != null) {
                track = (TrackItem) AudioPod.recentplayed.get(row);
            } else {
                return new Object();
            }

            switch (col) {
            case 0:
                return new Integer(row + 1);

            case 1:
                return track.isActive();

            case 2:
                return track.getArtist();

            case 3:
                return track.getAlbum();

            case 4:
                return track.getTrack();

            case 5:
                return this.convertMS(track.getLength());

            case 6:

                Date date = new Date(track.getLastplayed() * 1000);

                return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM)
                                 .format(date);
            }

            return new Object(); //if not found, return empty object
        }

        public void setValueAt(Object value, int row, int col) {
            if (col == 1) {
                TrackItem track;

                if (!(value instanceof Boolean)) {
                    throw new RuntimeException("Active must be a Boolean.");
                }

                if (AudioPod.recentplayed == null) {
                    throw new RuntimeException("Recent Played list is NULL!");
                }

                track = (TrackItem) AudioPod.recentplayed.get(row);
                track.setActive(((Boolean) value));
                fireTableCellUpdated(row, col);
            }
        }

        private String convertMS(long length) {
            long minutes = length / 60;
            String seconds = new Long(length - (minutes * 60)).toString();

            if (seconds.length() == 1) {
                seconds = "0" + seconds;
            }

            return minutes + ":" + seconds;
        }
    }
}
