package com.hexhaus.icarrus.dao;

import com.hexhaus.icarrus.handler.SettingsHandler;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * User: 67726e
 * Date: 8/15/11
 * Time: 6:58 PM
 */
class ClipboardDao {
	public ClipboardDao() {
	}

	/**
	 * Method copies the contents of a String to the user's clipboard if the user has specified this as allowable via settings.
	 *
	 * @param contents the String to be copied to the clipboard
	 */
	public void copyURLToClipboard(String contents) {
		// Do not perform any action if the user does not have this setting as active.
		if (!SettingsHandler.getCopyURLToClipboard()) return;

		StringSelection selection = new StringSelection(contents);
		Clipboard board = Toolkit.getDefaultToolkit().getSystemClipboard();
		board.setContents(selection, null);
	}
}
