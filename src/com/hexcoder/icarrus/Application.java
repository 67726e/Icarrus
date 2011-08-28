package com.hexcoder.icarrus;

import com.hexcoder.icarrus.dao.ImageDAO;
import com.hexcoder.icarrus.dao.LoggingDAO;
import com.hexcoder.icarrus.dto.MessageHandler;
import com.hexcoder.icarrus.ui.ControlPanelForm;
import com.hexcoder.icarrus.ui.DropForm;
import com.hexcoder.icarrus.ui.ExtendedTrayIcon;
import com.hexcoder.icarrus.ui.LoginForm;
import com.sun.awt.AWTUtilities;
import sun.rmi.runtime.Log;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * User: 67726e
 * Date: 8/5/11
 * Time: 10:17 PM
 */
public class Application {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }            // Set the UI as the System's UI
		catch (Exception ignored) {}

        if (!AWTUtilities.isTranslucencySupported(AWTUtilities.Translucency.TRANSLUCENT)) {                             // Determine if the host OS supports translucency
            // TODO: Research alternate ways of dipslaying the DropFrom that do not require translucency
            MessageHandler.postMessage("Unsupported Feature",
                        "Your OS does not offer features required for Icarrus to run.", LoggingDAO.FATAL_ERROR);        // End the application with a fatal error due to lack of required feature
        } else {
            GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] devices = environment.getScreenDevices();                                                  // Get a list of graphics devices being used to determine if the OS is translucency capable
            boolean isTranslucencyCapable = true;

            for (GraphicsDevice device : devices) {
                GraphicsConfiguration[] configurations = device.getConfigurations();

                for (GraphicsConfiguration configuration : configurations) {                                            // Iterate through an array of configurations for each device
                    if (!AWTUtilities.isTranslucencyCapable(configuration)) {                                           // Check if the configuration allows for translucency
                        isTranslucencyCapable = false;                                                                  // If the configuration is off but available, report it to the user
                        break;
                    }
                }

                if (!isTranslucencyCapable) break;
            }

            if (!isTranslucencyCapable) MessageHandler.postMessage("Disabled Feature",
                        "You must enable translucency in your graphics configurations to use Icarrus.",
                        LoggingDAO.FATAL_ERROR);                                                                        // Inform the user of the need to have translucency enabled, exit with fatal error
        }


        ExtendedTrayIcon trayIcon = new ExtendedTrayIcon(ImageDAO.getImage("tray_icon.png"));                           // Create TrayIcon for the user to interact with
    }
}
