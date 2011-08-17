package com.hexcoder.icarrus.ui;

import com.hexcoder.icarrus.dao.LoggingDAO;
import com.hexcoder.icarrus.dto.CredentialHandler;
import com.hexcoder.icarrus.dto.MessageHandler;
import com.sun.org.apache.bcel.internal.generic.NEW;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.security.PrivateKey;

/**
 * Created by IntelliJ IDEA.
 * User: 67726e
 * Date: 8/6/11
 * Time: 12:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class ExtendedTrayIcon extends TrayIcon {
    private JPopupMenu popupMenu;                                                               // Swing popupmenu used in lieu of the AWT popup menu used by TrayIcon
    private JDialog popupDialog;
    private static JMenuItem login;
    private TrayIcon trayIcon = this;                                                           // Get a pointer to the TrayIcon so it can be removed on exit
    private LoginForm loginForm;                                                                // Form to allow the user to login
    private ControlPanelForm controlPanelForm;                                                  // Form for upload history, application settings, and application about page
    private DropForm dropForm;                                                                  // Form that accepts the user's file drops

    public static void setLoginStatus(String status) {login.setText(status);}

    public ExtendedTrayIcon(BufferedImage image) {
        super(image);
        this.setImageAutoSize(false);
        this.addMouseListener(new trayMouseListener());

        popupDialog = new JDialog();
        popupDialog.setAlwaysOnTop(true);
        popupDialog.setUndecorated(true);

        popupMenu = new JPopupMenu();

        // Create UI for the popupMenu
        SettingsMenuListener settingsMenuListener = new SettingsMenuListener();
        JMenu settingsMenu = new JMenu("Settings");
            JMenuItem history = new JMenuItem("History");
            history.addActionListener(settingsMenuListener);
            settingsMenu.add(history);
            JMenuItem settings = new JMenuItem("Settings");
            settings.addActionListener(settingsMenuListener);
            settingsMenu.add(settings);
            JMenuItem about = new JMenuItem("About");
            about.addActionListener(settingsMenuListener);
            settingsMenu.add(about);
        popupMenu.add(settingsMenu);
        JMenuItem calibrate = new JMenuItem("Calibrate");
        calibrate.addActionListener(new CalibrateListener());
        popupMenu.add(calibrate);
        login = new JMenuItem("Login");
        login.addActionListener(new LoginListener());
        popupMenu.add(login);
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ExitListener());
        popupMenu.add(exit);

        try {
            SystemTray.getSystemTray().add(this);
        } catch (AWTException e) {
            MessageHandler.postMessage("System Tray Error", "The tray icon could not be added to your system tray.", LoggingDAO.FATAL_ERROR);
        }

        dropForm = new DropForm(this.getSize());                                               // Create new form to accept file drops
        dropForm.setVisible(true);
        // TODO: Create method to determine the location of the TrayIcon
        // TODO: Initial calibration/positioning of dropForm
    }

    public void showPopupMenu(MouseEvent event) {
        if (event.isPopupTrigger() && popupMenu != null) {
            // TODO: Perform check to ensure the menu does not appear offscreen

            Dimension size = popupMenu.getPreferredSize();
            int x = event.getX();
            int y = event.getY() - size.height;                                                 // Place the popupmenu so that it's bottom left coordinate is at the click location

            popupDialog.setLocation(x, y);
            popupDialog.setVisible(true);
            popupMenu.show(popupDialog.getContentPane(), 0, 0);
            popupDialog.toFront();
        }
    }

    private class trayMouseListener implements MouseListener {
        public void mouseClicked(MouseEvent event) {}
        public void mouseEntered(MouseEvent event) {}
        public void mouseExited(MouseEvent event) {}
        public void mousePressed(MouseEvent event) {}
        public void mouseReleased(MouseEvent event) {
            showPopupMenu(event);                                                               // Call action to show the Swing based popupmenu
        }
    }



    /****** Event Listener Classes *******/

    private class SettingsMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            JMenuItem item = (JMenuItem)event.getSource();
            String text = item.getText();                                                       // Get the text of the item to determine which item was clicked

            if (text.equals("About")) {                                                         // Determine which item was clicked and switch to the appropriate panel
                controlPanelForm.switchToTab(ControlPanelForm.ControlPanelTab.ABOUT);
            } else if (text.equals("History")) {
                controlPanelForm.switchToTab(ControlPanelForm.ControlPanelTab.HISTORY);
            } else {
                controlPanelForm.switchToTab(ControlPanelForm.ControlPanelTab.SETTINGS);
            }

            controlPanelForm.setVisible(true);
            controlPanelForm.toFront();
        }
    }

    private class CalibrateListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            // TODO: Create functionality to locate the TrayIcon
            // TODO: Call method to relocate the TrayIcon
        }
    }

    private class LoginListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (login.getText().equals("Login")) {
                if (loginForm != null) return;                                                  // Do nothing if the login form is already displayed
                loginForm = new LoginForm();                                                    // Display the login form so the user may login
            } else {
                if (loginForm != null) loginForm.dispose();                                     // Get rid of the current login form if one is somehow still displayed
                login.setText("Login");                                                         // Change the text to show we are no longer logged in

                CredentialHandler.setLoginStatus(false);                                        // Clear last logged in user's credentials
                CredentialHandler.setToken("");
                CredentialHandler.setUsername("");
                CredentialHandler.setPassword("");
            }
        }
    }

    private class ExitListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            SystemTray.getSystemTray().remove(trayIcon);                                        // Remove the TrayIcon from the system's tray
            System.exit(0);                                                                     // Clean exit
        }
    }
}
