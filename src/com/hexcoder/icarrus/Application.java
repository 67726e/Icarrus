package com.hexcoder.icarrus;

import com.hexcoder.icarrus.dao.ImageDAO;
import com.hexcoder.icarrus.dao.LoggingDAO;
import com.hexcoder.icarrus.dto.MessageHandler;
import com.hexcoder.icarrus.ui.ControlPanelForm;
import com.hexcoder.icarrus.ui.DropForm;
import com.hexcoder.icarrus.ui.ExtendedTrayIcon;
import com.hexcoder.icarrus.ui.LoginForm;
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

        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultDevice = environment.getDefaultScreenDevice();
        if (!defaultDevice.isWindowTranslucencySupported(
                GraphicsDevice.WindowTranslucency.TRANSLUCENT)) {                               // Check to see if the OS supports translucency
            MessageHandler.postMessage("Unsupported Feature",
                    "Icarrus relies on features not provided by your current OS or configuration.", LoggingDAO.FATAL_ERROR);
        }

        ExtendedTrayIcon trayIcon = new ExtendedTrayIcon(ImageDAO.getImage("tray_icon.png"));                           // Create TrayIcon for the user to interact with
    }
}
