package com.hexcoder.icarrus.dto;

/**
 * User: 67726e
 * Date: 8/9/11
 * Time: 7:02 PM
 */
public class CredentialHandler {
    private static String username;
    private static String password;
    private static String token;
    private static boolean loginStatus;

    public static void setUsername(String _username) {username = _username;}
    public static void setPassword(String _password) {password = _password;}
    public static void setToken(String _token) {token = _token;}
    public static String getToken() {return token;}
    public static void setLoginStatus(boolean _loginStatus) {loginStatus = _loginStatus;}
    public static boolean getLoginStatus() {return loginStatus;}
    public static String getUsername() {return username;}
    public static String getPassword() {return password;}

    private CredentialHandler() {}
}
