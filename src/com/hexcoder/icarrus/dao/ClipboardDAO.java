package com.hexcoder.icarrus.dao;

import com.hexcoder.icarrus.dto.SettingsHandler;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Created by IntelliJ IDEA.
 * User: 67726e
 * Date: 8/15/11
 * Time: 6:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClipboardDAO {
    public ClipboardDAO() {}

    /**
     * Method copies the contents of a String to the user's clipboard if the user has specified this as allowable via settings.
     *
     * @param contents the String to be copied to the clipboard
     */
    public void copyURLToClipboard(String contents) {
        if (!SettingsHandler.getCopyURLToClipboard()) return;                                                           // Do not perform any action if the user does not have this setting as active.

        StringSelection selection = new StringSelection(contents);
        Clipboard board = Toolkit.getDefaultToolkit().getSystemClipboard();
        board.setContents(selection, null);
    }
}
