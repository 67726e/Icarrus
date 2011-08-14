package com.hexcoder.icarrus;

import com.hexcoder.icarrus.dao.ImageDAO;
import com.hexcoder.icarrus.ui.ControlPanelForm;
import com.hexcoder.icarrus.ui.ExtendedTrayIcon;
import com.hexcoder.icarrus.ui.LoginForm;
import sun.rmi.runtime.Log;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: 67726e
 * Date: 8/5/11
 * Time: 10:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class main {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }            // Set the UI as the System's UI
		catch (Exception e) {}
    }
}
