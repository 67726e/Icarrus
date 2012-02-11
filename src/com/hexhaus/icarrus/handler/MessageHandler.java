package com.hexhaus.icarrus.handler;

import com.hexhaus.icarrus.dao.LoggingDao;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

/**
 * User: 67726e
 * Date: 8/6/11
 * Time: 11:05 AM
 */
public class MessageHandler {
    // Used to get a time stamp for the message
    private static Calendar calendar = Calendar.getInstance();
    // Pointer to the TrayIcon for messaging
    private static TrayIcon trayIcon;

    private MessageHandler() {}

	public static void setTrayIcon(TrayIcon _trayIcon) {trayIcon = _trayIcon;}

    /**
     * Method called from in class as a utility method to get a String representation of the time that the
     * message was posted as part of the logging data.
     *
     * @return A String representation of the current date and time of the message being posted
     */
    private static String getTimeStamp() {
        // Return a String representation of the Date object
        return calendar.getTime().toString();
    }

    /**
     * Method called as a part of the message logging process. A TrayIcon message (or JOptionPane message)
     * is shown to inform the user of the message. The way the message is displayed is depended on application
     * settings and rules.
     *
     * @param title The title of the message
     * @param message The message content
     * @param messageType The type of message such as an error or warning
     */
    private static void displayMessageToUser(String title, String message, LoggingDao.Status messageType) {
        boolean displayViaTray = true;
        // Determines the display via TrayIcon or JOptPane
        if (messageType == LoggingDao.Status.FatalError) displayViaTray = false;
        if (trayIcon == null) displayViaTray = false;

        if (displayViaTray) {
            // Display the message via the TrayIcon
	    	trayIcon.displayMessage(title, message, messageType.toTrayType());
        } else {
            // Display the message via a dialog
	        JOptionPane.showMessageDialog(null, message, title, messageType.toPaneType());
        }
    }

    /**
     * Method called from any part of the application to report errors, warnings, or results of activities.
     * Messages may be written to the log file, displayed as a dialog to the user, and printed to the command
     * line based on the user's preferences.
     *
     * @param title the overall topic of the message being posted.
     * @param message the details of the message being sent. Should not be more than a line or two if being displayed.
     * @param messageType determines the severity of the message being sent. Use LoggingDao's fields passing severity.
     */
    public static void postMessage(String title, String message, LoggingDao.Status messageType) {
        // Print formatted message to command line
        System.out.println("\t[" + messageType.toString()  + "] - " + title + ": " + message);
        if (SettingsHandler.getDisplayMessagesToUser())
            // Call method to display the message to the user if applicable
            displayMessageToUser(title, message, messageType);
        if (SettingsHandler.getSaveMessagesToLog())
            // Call logging method to write this message to the log
            LoggingDao.logToFile(title, message, messageType);
        // Exit with a general error code upon a fatal error
        if (messageType == LoggingDao.Status.FatalError) System.exit(-1);
    }
}
