package com.hexcoder.icarrus.ui;

import com.hexcoder.icarrus.dao.HistoryDAO;
import com.hexcoder.icarrus.dao.ImageDAO;
import com.hexcoder.icarrus.dao.SettingsDAO;
import com.hexcoder.icarrus.dto.SettingsHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: 67726e
 * Date: 8/6/11
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ControlPanelForm extends JFrame {
    private final int WIDTH = 500, HEIGHT = 300;
    private JFrame form = this;                                                                 // Pointer to the JFrame for use by inner classes
    private ControlPanelPanel panel;                                                            // Pointer to the JPanel for this form

    public enum ControlPanelTab {HISTORY, SETTINGS, ABOUT}                                     // Enum used to determine which tab to switch to

    public void switchToTab(ControlPanelTab tab) {panel.switchToTab(tab);}

    public ControlPanelForm() {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = environment.getDefaultScreenDevice();
        DisplayMode displayMode = device.getDisplayMode();                                      // Get object containing the size of the available screen real estate
        int windowX = ((displayMode.getWidth() - WIDTH) / 2);                                   // Calculate X coordinate so the window is horizontally centered
        int windowY = ((displayMode.getHeight() - HEIGHT) / 2);                                 // Calculate Y coordinate so the window is vertically centered

        panel = new ControlPanelPanel();

        this.setBounds(windowX, windowY, WIDTH, HEIGHT);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.setTitle("Icarrus ~ Settings");
        this.getContentPane().add(panel);                                                       // Add the JPanel with the form's contents
        try {
            this.setIconImage(ImageDAO.getImage("wingIcon-32x32.png"));
        } catch (IOException e) {

        }

        /** Setup JMenuBar **/
        JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
                JMenuItem exit = new JMenuItem("Exit");
                exit.addActionListener(new ExitMenuListener());
                fileMenu.add(exit);
            menuBar.add(fileMenu);
            JMenu helpMenu = new JMenu("Help");
                JMenuItem about = new JMenuItem("About");
                about.addActionListener(new AboutItemListener());
                helpMenu.add(about);
                JMenuItem help = new JMenuItem("Help");
                help.addActionListener(new HelpItemListener());
                helpMenu.add(help);
            menuBar.add(helpMenu);
        this.setJMenuBar(menuBar);
    }

    private class ControlPanelPanel extends JPanel {
        private JTabbedPane tabbedPane;

        /**
         * Method called to switch between the tabs on the control panel. ExtendedTrayIcon's menu items will indirectly
         * call this method via ControlPanelForm which in turn calls this method. Also the "About" menu item action
         * listener on the menu bar uses this method.
         *
         * @param tab is the designator for the tab to switch to.
         */
        public void switchToTab(ControlPanelTab tab) {
            switch (tab) {
                case HISTORY:
                    tabbedPane.setSelectedIndex(0);
                    break;
                case SETTINGS:
                    tabbedPane.setSelectedIndex(1);
                    break;
                case ABOUT:
                    tabbedPane.setSelectedIndex(2);
                    break;
            }
        }

        public ControlPanelPanel() {
            this.setLayout(null);
            this.setBounds(0, 0, 500, 300);

            tabbedPane = new JTabbedPane();
            tabbedPane.setBounds(0, 0, 500, 280);
            tabbedPane.addTab("History", new HistoryTab());
            tabbedPane.addTab("Settings", new SettingsTab());
            tabbedPane.addTab("About", new AboutTab());
            this.add(tabbedPane);
        }
    }

    private class HistoryTab extends JPanel {
        private JTable historyTable;
        private DefaultTableModel tableModel;
        private final int SCROLL_WIDTH = 440, SCROLL_HEIGHT = 150;

        public HistoryTab() {
            this.setLayout(null);

            java.util.List<List<String[]>> history = new HistoryDAO().loadHistoryFromFile();                            // Get a List containing all of the history data
            Object[][] data = listToObject(history);                                                                    // Convert the List to a properly formatted Object array for the JTable
            String[] columns = {"File Name", "URL", "Size", "Date"};

            tableModel = new DefaultTableModel(data, columns);
            historyTable = new JTable(tableModel);
            historyTable.setPreferredScrollableViewportSize(new Dimension(SCROLL_WIDTH, SCROLL_HEIGHT));
            historyTable.setFillsViewportHeight(true);
            historyTable.addMouseListener(new HistoryMouseListener());

            JScrollPane scrollPane = new JScrollPane(historyTable);
            scrollPane.setBounds(20, 20, SCROLL_WIDTH, SCROLL_HEIGHT);
            this.add(scrollPane);
        }

        public void insertRow(Object[] data) {tableModel.insertRow(0, data);}
        public void clearTable() {tableModel.getDataVector().removeAllElements();}

        /**
         * Private utility method used to convert the List containing history entries into a format readable
         * by the JTable that displays the history. Function iterates through the key/value pairs and places
         * the value in the corresponding slot of the array.
         *
         * @param list contains the key/value pairs for each row in the history table
         * @return Object[][] containing the data for the rows of the JTable
         */
        private Object[][] listToObject(List<List<String[]>> list) {
            Object[][] data = new Object[list.size()][0];

            int rowCount = 0;
            for (List<String[]> row : list) {
                Object[] rowObject = {"", "", "", ""};                                                                  // Current temporary row object
                for (String[] pair : row) {                                                                             // Iterate through the key/value pair to determine what column it belongs in
                    if (pair[0].equals("filename")) {
                        rowObject[0] = pair[1];
                    } else if (pair[0].equals("fileurl")) {
                        rowObject[1] = pair[1];
                    } else if (pair[0].equals("filesize")) {
                        rowObject[2] = pair[1];
                    } else if (pair[0].equals("uploaddate")) {
                        rowObject[3] = pair[1];
                    }
                }
                data[rowCount++] = rowObject;                                                                           // Pass the new Object[] for the current row into the final row object
            }

            return data;
        }

        private class HistoryMouseListener implements MouseListener {
            public void mouseClicked(MouseEvent event) {}
            public void mouseEntered(MouseEvent event) {}
            public void mouseExited(MouseEvent event) {}
            public void mousePressed(MouseEvent event) {}
            public void mouseReleased(MouseEvent event) {}
        }
    }

    private class SettingsTab extends JPanel {
        private JCheckBox deleteHistory, saveHistory, copyURL, saveLog, displayMessages;
        private JTextField uploadURL, loginURL;

        public SettingsTab() {
            this.setLayout(null);
            new SettingsDAO().loadSettingsFromFile();                                                                   // Load the settings from the Settings file for a proper display of user settings

            deleteHistory = new JCheckBox("Delete History", false);
            deleteHistory.setBounds(20, 20, 100, 20);
            this.add(deleteHistory);
            saveHistory = new JCheckBox("Save History", SettingsHandler.getSaveHistoryToFile());
            saveHistory.setBounds(20, 50, 100, 20);
            this.add(saveHistory);
            copyURL = new JCheckBox("Copy URLs", SettingsHandler.getCopyURLToClipboard());
            copyURL.setBounds(20, 80, 100, 20);
            this.add(copyURL);
            saveLog = new JCheckBox("Save Logs", SettingsHandler.getSaveMessagesToLog());
            saveLog.setBounds(20, 110, 100, 20);
            this.add(saveLog);
            displayMessages = new JCheckBox("Display Messages", SettingsHandler.getDisplayMessagesToUser());
            displayMessages.setBounds(20, 140, 120, 20);
            this.add(displayMessages);


            JLabel loginURLLabel = new JLabel("Login Server URL:");
            loginURLLabel.setBounds(160, 20, 100, 20);
            this.add(loginURLLabel);
            loginURL = new JTextField(SettingsHandler.getLoginServerURL());
            loginURL.setBounds(260, 20, 200, 20);
            this.add(loginURL);
            JLabel uploadURLLabel = new JLabel("Upload Server URL:");
            uploadURLLabel.setBounds(160, 50, 100, 20);
            this.add(uploadURLLabel);
            uploadURL = new JTextField(SettingsHandler.getUploadServerURL());
            uploadURL.setBounds(260, 50, 200, 20);
            this.add(uploadURL);



            JButton apply = new JButton("Apply");
            apply.setBounds(20, 190, 80, 25);
            apply.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateSettings();
                }
            });
            this.add(apply);
            JButton cancel = new JButton("Cancel");
            cancel.setBounds(110, 190, 80, 25);
            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cancelSettings();
                }
            });
            this.add(cancel);
        }

        /**
         * Private method called when the "Apply" button is clicked. Method writes out the settings
         * to the settings file via SettingsDAO and sets the settings for the application runtime in
         * SettingsHandler.
         */
        private void updateSettings() {
            SettingsHandler.setCopyURLToClipboard(copyURL.isSelected());
            SettingsHandler.setDisplayMessagesToUser(displayMessages.isSelected());
            SettingsHandler.setLoginServerURL(loginURL.getText());
            SettingsHandler.setSaveHistoryToFile(saveHistory.isSelected());
            SettingsHandler.setSaveMessagesToLog(saveLog.isSelected());
            SettingsHandler.setUploadServerURL(uploadURL.getText());

            new SettingsDAO().writeSettingsToFile();                                            // Write out the newly updated settings to the Settings file
        }

        /**
         * Private method called when the "Cancel" button is clicked to reset any changes to the settings
         * by returning their statuses to those in the SettingsHandler class.
         */
        private void cancelSettings() {
            deleteHistory.setSelected(false);
            saveHistory.setSelected(SettingsHandler.getSaveHistoryToFile());
            copyURL.setSelected(SettingsHandler.getCopyURLToClipboard());
            saveLog.setSelected(SettingsHandler.getSaveMessagesToLog());
            displayMessages.setSelected(SettingsHandler.getDisplayMessagesToUser());

            uploadURL.setText(SettingsHandler.getUploadServerURL());
            loginURL.setText(SettingsHandler.getLoginServerURL());
        }
    }

    private class AboutTab extends JPanel {
        public AboutTab() {
            this.setLayout(null);

            JLabel authors = new JLabel("Authors: ");
            authors.setBounds(20, 20, 100, 20);
            this.add(authors);
            JLabel authorGlenn = new JLabel("Glenn Nelson");
            authorGlenn.setBounds(100, 20, 240, 20);
            this.add(authorGlenn);
            JLabel homepage = new JLabel("Homepage: ");
            homepage.setBounds(20, 70, 100, 20);
            this.add(homepage);
            JLabel homepageLink = new JLabel("http://icarr.us/");
            homepageLink.setBounds(100, 70, 90, 22);
            this.add(homepageLink);
        }
    }


    /***** Event Listeners *****/

    private class ExitMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            form.setVisible(false);                                                             // Hide this form for it is resource intensive to retrieve
        }
    }

    private class AboutItemListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            switchToTab(ControlPanelTab.ABOUT);                                                 // Switch to the "About" tab
        }
    }

    private class HelpItemListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            // TODO: Implement code to open help documentation
        }
    }
}
