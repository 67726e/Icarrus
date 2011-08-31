package com.hexcoder.icarrus.dao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * User: 67726e
 * Date: 8/6/11
 * Time: 11:02 AM
 */
public class LoggingDAO {
    private static final File logFile = new File("data/log.txt");
    public enum Status {
        INFORMATION("INFORMATION"), WARNING("WARNING"), ERROR("ERROR"), FATAL_ERROR("FATAL ERROR");

        private String stringValue;

        Status(String stringValue) {
            this.stringValue = stringValue;
        }

        public String toString() {return this.stringValue;}
    }

    private LoggingDAO() {}

    public static void logToFile(String title, String message, Status messageType) {
        FileWriter logWriter = null;
        try {
            try {
                logWriter = new FileWriter(logFile, true);
                logWriter.write("\t[" + messageType.toString()  + "] - " + title + ": " + message + "\n");
            } finally {
                logWriter.close();
            }
        } catch (IOException ignored) {}
    }
}
