package com.hexcoder.icarrus.ui;

import com.hexcoder.icarrus.dao.ImageDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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

     public enum ControlPanelTab {HISTORY, SETTINGS, ABOUT}                                     // Enum used to determine which tab to switch to

    public ControlPanelForm() {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = environment.getDefaultScreenDevice();
        DisplayMode displayMode = device.getDisplayMode();                                      // Get object containing the size of the available screen real estate
        int windowX = ((displayMode.getWidth() - WIDTH) / 2);                                   // Calculate X coordinate so the window is horizontally centered
        int windowY = ((displayMode.getHeight() - HEIGHT) / 2);                                 // Calculate Y coordinate so the window is vertically centered

        this.setBounds(windowX, windowY, WIDTH, HEIGHT);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.setTitle("Icarrus ~ Settings");
        this.getContentPane().add(new ControlPanelPanel());                                     // Add the JPanel with the form's contents
        try {
            this.setIconImage(ImageDAO.getImage("data/wingIcon-32x32.png"));
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

            tabbedPane = new JTabbedPane();
            tabbedPane.setBounds(0, 0, 500, 280);
            tabbedPane.setLayout(null);
            tabbedPane.add("History", new HistoryTab());
            tabbedPane.add("Settings", new SettingsTab());
            tabbedPane.add("About", new AboutTab());
            this.add(tabbedPane);
        }
    }

    private class HistoryTab extends JPanel {
        public HistoryTab() {}
    }

    private class SettingsTab extends JPanel {
        public SettingsTab() {}
    }

    private class AboutTab extends JPanel {
        public AboutTab() {}
    }

    /***** Event Listeners *****/

    private class ExitMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            form.setVisible(false);                                                             // Hide this form for it is resource intensive to retrieve
        }
    }

    private class AboutItemListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            // TODO: Switch the the "About" tab
        }
    }

    private class HelpItemListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            // TODO: Implement code to open help documentation
        }
    }
}
