package com.hexhaus.icarrus.ui;

import com.hexhaus.icarrus.dao.ImageDao;
import com.hexhaus.icarrus.dao.LoggingDao;
import com.hexhaus.icarrus.handler.CredentialHandler;
import com.hexhaus.icarrus.handler.MessageHandler;
import com.hexhaus.imagelocator.ImageLocator;
import com.hexhaus.imagelocator.RandomImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.TimerTask;

public class ExtendedTrayIcon extends TrayIcon {
    private JPopupMenu popupMenu;                                                               // Swing popupmenu used in lieu of the AWT popup menu used by TrayIcon
    private JDialog popupDialog;
    private static JMenuItem login;
    private TrayIcon trayIcon = this;                                                           // Get a pointer to the TrayIcon so it can be removed on exit
    private LoginForm loginForm;                                                                // Form to allow the user to login
    private ControlPanelForm controlPanelForm;                                                  // Form for upload history, application settings, and application about page
    private DropForm dropForm;                                                                  // Form that accepts the user's file drops
    private GraphicsDevice priorDevice;
    private ImageLocator imageLocator;

    public static void setLoginStatus(String status) {login.setText(status);}

    public ExtendedTrayIcon(Image image) {
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
            MessageHandler.postMessage("System Tray Error",
                    "The tray icon could not be added to your system tray.", LoggingDao.Status.FatalError);
        }

        controlPanelForm = new ControlPanelForm();                                              // Create form to display control data

        calibrateDropForm();                                                                    // Determine the position of the TrayIcon and position the drop form over it

        loginForm = new LoginForm();
        loginForm.setVisible(true);

        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {                                             // Set a timer task to run every 1 second
            @Override
            public void run() {
                if (!popupMenu.isVisible()) {                                                   // Check if the popupmenu is being shown
                    dropForm.toFront();                                                         // If not, bring the drop form to the front of the screen
                }
            }
        }, 0, 1000);

	    MessageHandler.setTrayIcon(this);                                                       // Set the pointer to the TrayIcon for messaging
    }

    public void showPopupMenu(MouseEvent event) {
        if (event.getButton() == 3 && popupMenu != null) {
            Dimension size = popupMenu.getPreferredSize();
            int x = event.getXOnScreen();
            int y = event.getYOnScreen() - size.height;                                         // Place the popupmenu so that it's bottom left coordinate is at the click location

            popupDialog.setLocation(x, y);
            popupDialog.setVisible(true);
            popupMenu.show(popupDialog.getContentPane(), 0, 0);
            popupDialog.toFront();
        }
    }

    private GraphicsDevice getDeviceWithTrayIcon() throws RuntimeException {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = environment.getScreenDevices();
        Robot robot;
        
        for (GraphicsDevice device : devices) {
            if (deviceContainsTrayIcon(device))
                return device;
        }

        throw new RuntimeException("Could not locate the tray icon!");
    }
    
    private boolean deviceContainsTrayIcon(GraphicsDevice device) {
        Robot robot;
        try {
            robot = new Robot(device);
        } catch (AWTException e) {
            MessageHandler.postMessage("Robot Failure", "Could not create a screen capture.", LoggingDao.Status.Error);
            return false;
        }

        int trayWidth = trayIcon.getSize().width;
        int trayHeight = trayIcon.getSize().height;
        BufferedImage randomImage = new RandomImage(trayWidth, trayHeight,
                RandomImage.ImageStyle.Stripe3Column).getImage();

        trayIcon.setImage(randomImage);
        BufferedImage screenCapture = robot.createScreenCapture(
                new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        trayIcon.setImage(ImageDao.getImage("tray_icon.png"));

        imageLocator = new ImageLocator(screenCapture, randomImage);
        imageLocator.search(2);

        if (imageLocator.isImageFound()) {
            return true;
        }

        return false;
    }

    private void calibrateDropForm() {
        try {
            GraphicsDevice device = getDeviceWithTrayIcon();

            if (device != null) {
                if (device == priorDevice) {
                    dropForm.setLocation(imageLocator.getFirstOccurrence());
                } else {
                    priorDevice = device;

                    if (dropForm != null) {
                        dropForm.setVisible(false);
                        dropForm.dispose();
                    }

                    dropForm = new DropForm(this.getSize(), this, device);
                }
            }
        } catch (RuntimeException e) {
            MessageHandler.postMessage("Runtime Exception",
                    "Could not locate the system tray icon", LoggingDao.Status.Error);
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
            calibrateDropForm();
        }
    }

    private class LoginListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (login.getText().equals("Login")) {
                loginForm.setVisible(true);                                                     // Display the login form
            } else {
                if (loginForm != null) loginForm.dispose();                                     // Get rid of the current login form if one is somehow still displayed
                login.setText("Login");                                                         // Change the text to show we are no longer logged in

                CredentialHandler.setLoginStatus(false);                                        // Clear last logged in user's credentials
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
