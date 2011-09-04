package com.hexcoder.icarrus.dto;

import com.hexcoder.icarrus.dao.LoggingDAO;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

/**
 * User: 67726e
 * Date: 8/6/11
 * Time: 11:05 AM
 */
public class MessageHandler {
    private static Calendar calendar = Calendar.getInstance();                                  // Used to get a time stamp for the message
    private static TrayIcon trayIcon;                                                           // Pointer to the TrayIcon for messaging

    private MessageHandler() {}

	public static void setTrayIcon(TrayIcon _trayIcon) {trayIcon = _trayIcon;}

    /**
     * Method called from in class as a utility method to get a String representation of the time that the
     * message was posted as part of the logging data.
     *
     * @return A String representation of the current date and time of the message being posted
     */
    private static String getTimeStamp() {
        return calendar.getTime().toString();                                                   // Return a String representation of the Date object
    }

	/**
	 * Method converts a LoggingDAO message type into  a value usable by the JOptionPane
	 *
	 * @param messageType The LoggingDAO message type
	 * @return an int mapping to the given LoggingDAO value
	 */
	private static int convertJOptionPane(LoggingDAO.Status messageType) {
		int type = JOptionPane.INFORMATION_MESSAGE;

		switch (messageType) {
			case INFORMATION:
				type = JOptionPane.INFORMATION_MESSAGE;
				break;
			case WARNING:
				type = JOptionPane.WARNING_MESSAGE;
				break;
			case ERROR:
			case FATAL_ERROR:
				type = JOptionPane.ERROR_MESSAGE;
				break;
		}

		return type;
	}

	/**
	 * Method converts a LoggingDAO message type into a value usable by TrayIcon
	 *
	 * @param messageType The LoggingDAO message type
	 * @return a TrayIcon.MessageType value mapping to the given LoggingDAO value
	 */
	private static TrayIcon.MessageType convertTrayIcon(LoggingDAO.Status messageType) {
		TrayIcon.MessageType type = TrayIcon.MessageType.INFO;

		switch (messageType) {
			default:
			case INFORMATION:
				type = TrayIcon.MessageType.INFO;
				break;
			case WARNING:
				type = TrayIcon.MessageType.WARNING;
				break;
			case ERROR:
				type = TrayIcon.MessageType.ERROR;
				break;
		}

		return type;
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
    private static void displayMessageToUser(String title, String message, LoggingDAO.Status messageType) {
        boolean displayViaTray = true;                                                                                  // Determines the display via TrayIcon or JOptPane
        if (messageType == LoggingDAO.Status.FATAL_ERROR) displayViaTray = false;
        if (trayIcon == null) displayViaTray = false;

        if (displayViaTray) {
	    	trayIcon.displayMessage(title, message, convertTrayIcon(messageType));                                      // Display the message via the TrayIcon
        } else {
	        JOptionPane.showMessageDialog(null, message, title, convertJOptionPane(messageType));
        }
    }

    /**
     * Method called from any part of the application to report errors, warnings, or results of activities.
     * Messages may be written to the log file, displayed as a dialog to the user, and printed to the command
     * line based on the user's preferences.
     *
     * @param title the overall topic of the message being posted.
     * @param message the details of the message being sent. Should not be more than a line or two if being displayed.
     * @param messageType determines the severity of the message being sent. Use LoggingDAO's fields passing severity.
     */
    public static void postMessage(String title, String message, LoggingDAO.Status messageType) {
        System.out.println("\t[" + messageType.toString()  + "] - " + title + ": " + message);  // Print formatted message to command line
        if (SettingsHandler.getDisplayMessagesToUser())
            displayMessageToUser(title, message, messageType);                                  // Call method to display the message to the user if applicable
        if (SettingsHandler.getSaveMessagesToLog())
            LoggingDAO.logToFile(title, message, messageType);                                  // Call logging method to write this message to the log

        if (messageType == LoggingDAO.Status.FATAL_ERROR) System.exit(-1);                      // Exit with a general error code upon a fatal error
    }
}