package com.hexcoder.icarrus.dto;

import com.hexcoder.icarrus.dao.LoggingDAO;
import com.hexcoder.icarrus.dto.SettingsHandler;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: 67726e
 * Date: 8/6/11
 * Time: 11:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class MessageHandler {
    private static Calendar calendar = Calendar.getInstance();                                  // Used to get a time stamp for the message

    private MessageHandler() {}

    /**
     * Method called from in class as a utility method to get a String representation of the time that the
     * message was posted as part of the logging data.
     *
     * @return A String representation of the current date and time of the message being posted
     */
    private static String getTimeStamp() {
        return calendar.getTime().toString();                                                   // Return a String representation of the Date object
    }

    private static void displayMessageToUser(String title, String message, int messageType) {
        // TODO: Implement code to display messages to the user
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
    public static void postMessage(String title, String message, int messageType) {
        String severity;                                                                   // String representation of the level of severity of the message

        switch (messageType) {
        case LoggingDAO.INFORMATION:
            severity = "INFORMATION";
            break;
        case LoggingDAO.WARNING:
            severity = "WARNING";
            break;
        case LoggingDAO.ERROR:
            severity = "ERROR";
            break;
        case LoggingDAO.FATAL_ERROR:
            severity = "FATAL ERROR";
            break;
        default:
            severity = "INFORMATION";
        }

        System.out.println("\t[" + severity + "] - " + title + ": " + message);                 // Print formatted message to command line
        if (SettingsHandler.getDisplayMessagesToUser())
            displayMessageToUser(title, message, messageType);                                  // Call method to display the message to the user if applicable
        if (SettingsHandler.getSaveMessagesToLog())
            LoggingDAO.logToFile(title, message, messageType);                                  // Call logging method to write this message to the log

        if (messageType == LoggingDAO.FATAL_ERROR) System.exit(-1);                             // Exit with a general error code upon a fatal error
    }
}