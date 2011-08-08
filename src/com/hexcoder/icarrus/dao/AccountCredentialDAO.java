package com.hexcoder.icarrus.dao;

import com.hexcoder.icarrus.messaging.MessageHandler;
import sun.security.util.BigInt;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: 67726e
 * Date: 8/6/11
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccountCredentialDAO {
    private static String username;
    private static String password;
    private static String token;
    private static boolean loginStatus;
    private static final File accountCredentialFile =
                new File("../data/AccountCredentials.idat");                                    // Default file for all user account credentials

    public static void setUsername(String _username) {username = _username;}
    public static void setPassword(String _password) {password = _password;}
    public static void setToken(String _token) {token = _token;}
    public static String getToken() {return token;}
    public static void setLoginStatus(boolean _loginStatus) {loginStatus = _loginStatus;}
    public static boolean getLoginStatus() {return loginStatus;}

    private AccountCredentialDAO() {}

    public static void storeCredentials(String username, char[] password) {
        String passwordHash;

        try {
            MessageDigest passwordHasher = MessageDigest.getInstance("MD5");
            passwordHasher.update(Arrays.toString(password).getBytes(), 0, password.length);    // Create the password hash
            passwordHash = new BigInteger(1, passwordHasher.digest()).toString();               // Get hexadecimal String representation of hash
        } catch (NoSuchAlgorithmException e) {
            MessageHandler.postMessage("Credentials Storage Error", "Your account credentials could not be securely stored. You will not be automatically logged in", LoggingDAO.WARNING);
            return;
        }

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(accountCredentialFile));
            out.write("~Username: " + username + "\n");
            out.write("~Password: " + passwordHash + "\n");
            out.close();
        } catch (IOException e) {
            MessageHandler.postMessage("Credentials Storage Error", "Your account credentials could not stored. You will not be automatically logged in.", LoggingDAO.ERROR);
            return;
        }
    }
}