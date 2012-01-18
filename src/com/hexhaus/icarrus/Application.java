package com.hexhaus.icarrus;

import com.hexhaus.icarrus.dao.ImageDao;
import com.hexhaus.icarrus.dao.LoggingDao;
import com.hexhaus.icarrus.handler.MessageHandler;
import com.hexhaus.icarrus.ui.ExtendedTrayIcon;
import com.sun.awt.AWTUtilities;

import javax.swing.*;
import java.awt.*;

/**
 * User: 67726e
 * Date: 8/5/11
 * Time: 10:17 PM
 */
public class Application {
    public static void main(String[] args) {
        // TODO: Convert IDAT file format to use standard XML
	    // TODO: Convert IDAT readers to XPATH

	    try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }            // Set the UI as the System's UI
		catch (Exception ignored) {}

        if (!AWTUtilities.isTranslucencySupported(AWTUtilities.Translucency.TRANSLUCENT)) {                             // Determine if the host OS supports translucency
            // TODO: Research alternate ways of displaying the DropFrom that do not require translucency
            MessageHandler.postMessage("Unsupported Feature",
                        "Your OS does not offer features required for Icarrus to run.", LoggingDao.Status.FatalError);  // End the application with a fatal error due to lack of required feature
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
                        LoggingDao.Status.FatalError);                                                                  // Inform the user of the need to have translucency enabled, exit with fatal error
        }


        ExtendedTrayIcon trayIcon = new ExtendedTrayIcon(ImageDao.getImage("tray_icon.png"));                           // Create TrayIcon for the user to interact with
    }
}
