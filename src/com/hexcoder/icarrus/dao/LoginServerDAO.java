package com.hexcoder.icarrus.dao;

import com.hexcoder.icarrus.dto.SettingsHandler;
import com.hexcoder.icarrus.messaging.MessageHandler;

import javax.print.DocFlavor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: 67726e
 * Date: 8/6/11
 * Time: 2:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginServerDAO {
    private boolean loginStatus;

    public boolean getLoginStatus() {return loginStatus;}

    private void sendLoginServerRequest(String username, String password) {
        URL loginServerURL = null;

        try {
            loginServerURL = new URL(SettingsHandler.getLoginServerURL());                      // Attempt to form the URL for the login server
        } catch (MalformedURLException e) {
            // TODO: Report URL error
        }

        try {
            URLConnection loginServerConnection = loginServerURL.openConnection();              // Open the connection to the login server
            loginServerConnection.setDoOutput(true);

            OutputStreamWriter loginServerWriter = new OutputStreamWriter(loginServerConnection.getOutputStream());
            loginServerWriter.write("username=" + username + "&password=" + password);
            loginServerWriter.close();

            this.parseLoginServerResponse(loginServerConnection);
        } catch (IOException e) {
            // TODO: Report inability to write to login server
        }
    }

    private void parseLoginServerResponse(URLConnection loginServerConnection) {
        // TODO: Create spec for server Icarrus server responses for logins
        // TODO: Implement parser based on the specification
    }

    /**
     * Constructor for LoginServerDAO and is used to connect and send the login data to the designated login server
     * and then retrieve the server response and parse it for the appropriate data.
     *
     * @param username is the username being sent to the server for login
     * @param password is the password used to authenticate the login
     */
    public LoginServerDAO(String username, char[] password) {
        String passwordHash;

        try {
            MessageDigest passwordHasher = MessageDigest.getInstance("MD5");
            passwordHasher.update(Arrays.toString(password).getBytes(), 0, password.length);    // Create the password hash
            passwordHash = new BigInteger(1, passwordHasher.digest()).toString();               // Get hexadecimal String representation of hash
        } catch (NoSuchAlgorithmException e) {
            MessageHandler.postMessage("Credentials Storage Error", "Your account credentials could not be securely stored. You will not be automatically logged in", LoggingDAO.WARNING);
            return;
        }

        this.sendLoginServerRequest(username, passwordHash);
    }
}
