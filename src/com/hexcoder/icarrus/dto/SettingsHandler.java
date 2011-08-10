package com.hexcoder.icarrus.dto;

/**
 * Created by IntelliJ IDEA.
 * User: 67726e
 * Date: 8/6/11
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsHandler {
    private static boolean writeMessagesToLog = true;
    private static boolean displayMessagesToUser = true;
    private static String loginServerURL = "http://127.0.0.1/Icarrus/app/login.php";
    private static String uploadServerURL = "http://127.0.0.1/Icarrus/app/upload.php";
    // TODO: Point default loginServerURL to HexCoder run Icarrus server
    // TODO: Point default uploadServerURL to HexCoder run Icarrus server


    public static boolean getWriteMessagesToLog() {return writeMessagesToLog;}
    public static void setWriteMessagesToLog(boolean setting) {writeMessagesToLog = setting;}
    public static boolean getDisplayMessagesToUser() {return displayMessagesToUser;}
    public static void setDisplayMessagesToUser(boolean setting) {displayMessagesToUser = setting;}
    public static String getLoginServerURL() {return loginServerURL;}
    public static void setLoginServerURL(String setting) {loginServerURL = setting;}
    public static String getUploadServerURL() {return uploadServerURL;}
    public static void setUploadServerURL(String setting) {uploadServerURL = setting;}

    private SettingsHandler() {}
}
