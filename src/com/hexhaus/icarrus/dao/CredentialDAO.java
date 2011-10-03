package com.hexhaus.icarrus.dao;

import com.hexhaus.icarrus.handler.MessageHandler;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * User: 67726e
 * Date: 8/6/11
 * Time: 2:08 PM
 */
public class CredentialDAO {
    private static final File accountCredentialFile =
                new File("../data/AccountCredentials.idat");                                    // Default file for all user account credentials

    private CredentialDAO() {}

    public static void storeCredentials(String username, char[] password) {
        String passwordHash = "";

        for (char c : password) { passwordHash += c; }

        try {
            MessageDigest passwordHasher = MessageDigest.getInstance("MD5");
            passwordHasher.update(passwordHash.getBytes(), 0, password.length);                 // Create the password hash
            passwordHash = new BigInteger(1, passwordHasher.digest()).toString(16);             // Get hexadecimal String representation of hash
        } catch (NoSuchAlgorithmException e) {
            MessageHandler.postMessage("Credentials Storage Error", "Your account credentials could not be securely stored. You will not be automatically logged in", LoggingDAO.Status.WARNING);
            return;
        }

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(accountCredentialFile));
            out.write("~Username: " + username + "\n");
            out.write("~Password: " + passwordHash + "\n");
            out.close();
        } catch (IOException e) {
            MessageHandler.postMessage("Credentials Storage Error", "Your account credentials could not stored. You will not be automatically logged in.", LoggingDAO.Status.ERROR);
        }
    }
}