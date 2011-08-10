package com.hexcoder.icarrus.dto;

/**
 * Created by IntelliJ IDEA.
 * User: 67726e
 * Date: 8/9/11
 * Time: 7:02 PM
 * To change this template use File | Settings | File Templates.
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

    private CredentialHandler() {}
}
