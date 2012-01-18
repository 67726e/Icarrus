package com.hexhaus.icarrus.dao;

import com.hexhaus.icarrus.handler.CredentialHandler;
import com.hexhaus.icarrus.handler.MessageHandler;
import com.hexhaus.icarrus.handler.SettingsHandler;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * User: 67726e
 * Date: 8/6/11
 * Time: 2:20 PM
 */
public class LoginDao {
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
            // Attempt to form the URL for the login server
            loginServerURL = new URL(SettingsHandler.getLoginServerURL());
        } catch (MalformedURLException e) {
            MessageHandler.postMessage("Login URL Error",
                    "The URL provided was invalid. Please check the URL in the settings.", LoggingDao.Status.Error);
        }

        try {
            // Open the connection to the login server
            URLConnection loginServerConnection = loginServerURL.openConnection();
            loginServerConnection.setDoOutput(true);

            OutputStreamWriter loginServerWriter = new OutputStreamWriter(loginServerConnection.getOutputStream());
            loginServerWriter.write("username=" + username + "&password=" + password);
            loginServerWriter.close();

        	parseLoginServerResponse(loginServerConnection);
        } catch (IOException e) {
            MessageHandler.postMessage("Unable To Login",
                    "Icarrus is unable to write to the login server.", LoggingDao.Status.Error);
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
        List<Map<String, String>> response;
        BufferedReader in;

        try {
            in = new BufferedReader(new InputStreamReader(loginServerConnection.getInputStream()));

			response = IdatDao.readIdatFromBuffer(in, "Response {");

            in.close();
        } catch (IOException e) {
            MessageHandler.postMessage("Login Error",
                    "An invalid response was given by the login server. You could not be logged in.", LoggingDao.Status.Error);
            return;
        }

        if (response != null) retrieveValues(response);
    }

    /**
     * Private method is called to assign the proper parameter values to their corresponding setting variables for use
     * by the rest of the application.
     *
     * @param response
     */
    private void retrieveValues(List<Map<String, String>> response) {
		Map<String, String> map = response.get(0);

        // Set the logged-in status to true if it was a valid login attempt
		this.loginStatus = (map.containsKey("status") && map.get("status").equals("valid"));
		// TODO: Determine how to handle other possible parameters
		// TODO: Update Icarrus Server to write out the properly formatted response
		// TODO: Write a specification for the new IDAT data/file format
    }

    /**
     * Constructor for LoginDAO and is used to connect and send the login data to the designated login server
     * and then retrieve the server response and parse it for the appropriate data.
     *
     * @param username is the username being sent to the server for login
     * @param password is the password used to authenticate the login
     */
    public LoginDao(String username, char[] password) {
        this.loginStatus = false;

        this.sendLoginServerRequest(username, new String(password));
        if (this.loginStatus) {
            // Set the current username upon a successful login
            CredentialHandler.setUsername(username);
            CredentialHandler.setPassword(new String(password));
        }
    }
}
