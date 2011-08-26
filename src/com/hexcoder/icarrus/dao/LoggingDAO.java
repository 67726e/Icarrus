package com.hexcoder.icarrus.dao;

/**
 * User: 67726e
 * Date: 8/6/11
 * Time: 11:02 AM
 */
public class LoggingDAO {
    public static final int INFORMATION = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;
    public static final int FATAL_ERROR = 3;

    private LoggingDAO() {}

    public static void logToFile(String title, String message, int messageType) {
        // TODO: Write given message to the log file
    }
}
