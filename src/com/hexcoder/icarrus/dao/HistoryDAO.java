package com.hexcoder.icarrus.dao;

import com.hexcoder.icarrus.dto.MessageHandler;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: 67726e
 * Date: 8/11/11
 * Time: 8:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class HistoryDAO {
    private final File historyFile = new File("data/UploadHistory.idat");


    public HistoryDAO() {}

    public List<String[]> loadHistoryFromFile() {
        List<String[]> historyRows = new LinkedList<String[]>();

        // TODO: Implement History loader code based on the specification

        return historyRows;
    }

    /**
     * Function is called when a new entry is added to the upload history. The history description
     * is appended to the end of the current history file.
     *
     * @param historyEntry is an array containing the key/value pairs for the history data
     */
    public void writeHistoryToFile(String[][] historyEntry) {
        boolean generalError = false;
        StringBuilder writeString = new StringBuilder();
        writeString.append("\n~File {\n");

        for (int i = 0; i < historyEntry.length; i++) {
            if (historyEntry[i][1].contains("\n") || historyEntry[i][1].contains("~")) generalError = true;             // The value section cannot contain newline '\n' or tilde '~' characters
            if (historyEntry[i][0].contains(" ")) generalError = true;

            if (generalError) break;                                                                                    // Skip any additional processing if there were errors

            historyEntry[i][1] = historyEntry[i][1].replace(":", "~");                                                  // Replace all colons with tildes as per the specification
            writeString.append("\t~").append(historyEntry[i][0]).append(":").append(historyEntry[i][1]).append("\n");   // Append this line of the File lbock to
        }

        writeString.append("}\n");

        if (generalError) {
            MessageHandler.postMessage("History Content Error", "The history data was invalid and could not be written to file.", LoggingDAO.ERROR);
        }

        try { if (!historyFile.exists()) generalError = historyFile.createNewFile(); }                                  // Create a new history file if one does not exist
        catch (IOException e) { generalError = true; }
        if (generalError) {
            MessageHandler.postMessage("History File Error",
                    "A new history file could not be created. The history will not be stored.", LoggingDAO.ERROR);      // Inform the user that the history data file could not be created.
            return;
        }

        FileWriter out = null;
        try {
            out = new FileWriter(historyFile, true);                                                                    // Open a writer to the history file and append the data
            try { out.write(writeString.toString()); }                                                                  // Write out the final contents of the StringBuilder if all is well
            finally {
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            MessageHandler.postMessage("History Write Error", "The upload history could not be written to file.", LoggingDAO.ERROR);
        }
    }
}
