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
public class CredentialDao {
    private static final File accountCredentialFile =
                new File("../resource/data/account_credentials.dat");
	
    private CredentialDao() {}

    public static void storeCredentials(String username, char[] password) {
        String passwordHash = "";
        for (char c : password) { passwordHash += c; }

        try {
            MessageDigest passwordHasher = MessageDigest.getInstance("MD5");
            // Create the password hash
            passwordHasher.update(passwordHash.getBytes(), 0, password.length);
            // Get hexadecimal String representation of hash
            passwordHash = new BigInteger(1, passwordHasher.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            MessageHandler.postMessage("Credentials Storage Error",
                    "Your account credentials could not be securely stored. You will not be automatically logged in", LoggingDao.Status.Warning);
            return;
        }

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(accountCredentialFile));
            out.write("~Username: " + username + "\n");
            out.write("~Password: " + passwordHash + "\n");
            out.close();
        } catch (IOException e) {
            MessageHandler.postMessage("Credentials Storage Error",
                    "Your account credentials could not stored. You will not be automatically logged in.", LoggingDao.Status.Error);
        }
    }
}
