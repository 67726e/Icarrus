package com.hexhaus.icarrus.handler;

/**
 * User: 67726e
 * Date: 8/6/11
 * Time: 2:56 PM
 */
public class SettingsHandler {
	private static boolean saveMessagesToLog = true;
	private static boolean saveHistoryToFile = true;
	private static boolean displayMessagesToUser = true;
	private static boolean copyURLToClipboard = true;
	private static String loginServerURL = "http://icarrusserver/app/login.php";
	private static String uploadServerURL = "http://icarrusserver/app/upload.php";
	// TODO: Point default loginServerURL to HexCoder run Icarrus server
	// TODO: Point default uploadServerURL to HexCoder run Icarrus server


	public static boolean getSaveMessagesToLog() {
		return saveMessagesToLog;
	}

	public static void setSaveMessagesToLog(boolean setting) {
		saveMessagesToLog = setting;
	}

	public static boolean getDisplayMessagesToUser() {
		return displayMessagesToUser;
	}

	public static void setDisplayMessagesToUser(boolean setting) {
		displayMessagesToUser = setting;
	}

	public static String getLoginServerURL() {
		return loginServerURL;
	}

	public static void setLoginServerURL(String setting) {
		loginServerURL = setting;
	}

	public static String getUploadServerURL() {
		return uploadServerURL;
	}

	public static void setUploadServerURL(String setting) {
		uploadServerURL = setting;
	}

	public static boolean getSaveHistoryToFile() {
		return saveHistoryToFile;
	}

	public static void setSaveHistoryToFile(boolean setting) {
		saveHistoryToFile = setting;
	}

	public static boolean getCopyURLToClipboard() {
		return copyURLToClipboard;
	}

	public static void setCopyURLToClipboard(boolean setting) {
		copyURLToClipboard = setting;
	}

	private SettingsHandler() {
	}
}
