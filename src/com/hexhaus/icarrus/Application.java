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
		try {
			// Set the UI as the System's UI
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {

		}

		// Determine if the host OS supports translucency
		if (!AWTUtilities.isTranslucencySupported(AWTUtilities.Translucency.TRANSLUCENT)) {
			// End the application with a fatal error due to lack of required feature
			MessageHandler.postMessage("Unsupported Feature",
					"Your OS does not offer features required for Icarrus to run.", LoggingDao.Status.FatalError);
		} else {
			// Get a list of graphics devices being used to determine if the OS is translucency capable
			GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] devices = environment.getScreenDevices();
			boolean isTranslucencyCapable = true;

			for (GraphicsDevice device : devices) {
				GraphicsConfiguration[] configurations = device.getConfigurations();

				// Iterate through an array of configurations for each device
				for (GraphicsConfiguration configuration : configurations) {
					// Check if the configuration allows for translucency
					if (!AWTUtilities.isTranslucencyCapable(configuration)) {
						// If the configuration is off but available, report it to the user
						isTranslucencyCapable = false;
						break;
					}
				}

				if (!isTranslucencyCapable) {
					break;
				}
			}

			// Inform the user of the need to have translucency enabled, exit with fatal error
			if (!isTranslucencyCapable) {
				MessageHandler.postMessage("Disabled Feature",
						"You must enable translucency in your graphics configurations to use Icarrus.",
						LoggingDao.Status.FatalError);
			}
		}

		// Create TrayIcon for the user to interact with
		new ExtendedTrayIcon(ImageDao.getImage("tray_icon.png"));
	}
}
