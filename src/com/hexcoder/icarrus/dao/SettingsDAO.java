package com.hexcoder.icarrus.dao;

/**
 * Created by IntelliJ IDEA.
 * User: 67726e
 * Date: 8/11/11
 * Time: 8:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsDAO {

    public SettingsDAO() {
        // TODO: Write "Settings File Storage" specification
    }

    /**
     * Function is called to read in the data from the settings file in the data directory. Reads in the files
     * based on the "Settings File Storage" specification and updates the corresponding fields in the SettingsHandler
     * class.
     */
    public void loadSettingsFromFile() {
        // TODO: Write loader code for the settings based on the specification
    }

    /**
     * Function is called whenever the application settings are updated. The settings are read from the
     * SettingsHandler class and written to the settings file based on the "Settings File Storage" specification.
     */
    public void writeSettingsToFile() {
        // TODO: Write writer code for the settings based on the specification
    }
}
