package us.hexcoder.icarrus.dao;

import us.hexcoder.icarrus.dto.CredentialHandler;
import us.hexcoder.icarrus.dto.MessageHandler;
import us.hexcoder.icarrus.dto.SettingsHandler;

import java.io.*;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * User: 67726e
 * Date: 8/6/11
 * Time: 2:20 PM
 */
public class LoginDAO {
    private boolean loginStatus = false;

    public boolean getLoginStatus() {return loginStatus;}

    /**
     * Private method called as part of the analysis of the user's login. Method connects to the login server
     * and sends the username and password to be authenticated for a proper login.
     *
     * @param username is a String containing the account to be logged in
     * @param password is a String containing the password used to authenticate the above account
     */
    private void sendLoginServerRequest(String username, String password) {
        URL loginServerURL = null;

        try {
            loginServerURL = new URL(SettingsHandler.getLoginServerURL());                      // Attempt to form the URL for the login server
        } catch (MalformedURLException e) {
            MessageHandler.postMessage("Login URL Error", "The URL provided was invalid. Please check the URL in the settings.", LoggingDAO.Status.ERROR);
        }

        try {
            URLConnection loginServerConnection = loginServerURL.openConnection();              // Open the connection to the login server
            loginServerConnection.setDoOutput(true);

            OutputStreamWriter loginServerWriter = new OutputStreamWriter(loginServerConnection.getOutputStream());
            loginServerWriter.write("username=" + username + "&password=" + password);
            loginServerWriter.close();

            this.parseLoginServerResponse(loginServerConnection);
        } catch (IOException e) {
            MessageHandler.postMessage("Unable To Login", "Icarrus is unable to write to the login server.", LoggingDAO.Status.ERROR);
        }
    }


    /**
     * Internal method is called as part of the multi-step parsing process for the login server response. This method
     * retrieves all useful data from the response and parses parameter/value pairs to be read for settings/user data
     * to validate the user's login.
     *
     * @param loginServerConnection is the URL connection that provides the server response
     */
    private void parseLoginServerResponse(URLConnection loginServerConnection) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(loginServerConnection.getInputStream()));
            String line = "";

            while ((line = in.readLine()) != null) {
                // TODO: Log warnings for invalid server parameter strings/values

                if (line.length() < 4) continue;                                                // If the String is less than 4 characters (the minimum for a parameter line) then we can skip this line as unrecognized
                if (line.charAt(0) != '~') continue;                                            // Skip this line if it does not start with the designated parameter marker '~'
                String[] arguments = line.split(":");                                           // Split the line along the delimiter
                if (arguments.length > 2) continue;                                             // We have illegal parameters

                parameters.put(arguments[0], arguments[1]);                                     // Place the parameter/value in the HashMap as it has passed all applicable checks
            }

            in.close();
        } catch (IOException e) {
            MessageHandler.postMessage("Login Error", "An invalid response was given by the login server. You could not be logged in.", LoggingDAO.Status.ERROR);
            return;
        }

        this.retrieveValues(parameters);
    }

    /**
     * Private method is called to assign the proper parameter values to their corresponding setting variables for use
     * by the rest of the application.
     *
     * @param parameters is the HashMap containing all parameter/value pairs provided in the login server's response
     */
    private void retrieveValues(HashMap<String, String> parameters) {
        // TODO: Improve upon validation of correct responses from server. Verify the value for "status" is "valid" before any actions are taken

        Iterator iterator = parameters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry)iterator.next();
            String key = (String)pair.getKey();
            String value = (String)pair.getValue();

            if (key.equals("~status") && value.equals("valid")) loginStatus = true;             // Update the login status to true if we have a valid login
            else if (key.equals("~token")) CredentialHandler.setToken(value);                   // Set the value of the current upload token
        }
    }

    /**
     * Constructor for LoginDAO and is used to connect and send the login data to the designated login server
     * and then retrieve the server response and parse it for the appropriate data.
     *
     * @param username is the username being sent to the server for login
     * @param password is the password used to authenticate the login
     */
    public LoginDAO(String username, char[] password) {
        loginStatus = false;
        String passwordHash = "";

        for (char c : password) { passwordHash += c; }

        try {
            MessageDigest passwordHasher = MessageDigest.getInstance("MD5");
            passwordHasher.update(passwordHash.getBytes(), 0, password.length);                 // Create the password hash
            passwordHash = new BigInteger(1, passwordHasher.digest()).toString(16);             // Get hexadecimal String representation of hash
        } catch (NoSuchAlgorithmException e) {
            MessageHandler.postMessage("Login Error", "There was a problem logging in. Please try again.", LoggingDAO.Status.WARNING);
            return;
        }

        this.sendLoginServerRequest(username, passwordHash);
        if (loginStatus) {
            CredentialHandler.setUsername(username);                                            // Set the current username upon a successful login
            CredentialHandler.setPassword(passwordHash);
        }
    }
}
