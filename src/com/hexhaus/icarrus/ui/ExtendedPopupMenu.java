package com.hexhaus.icarrus.ui;

import com.hexhaus.icarrus.handler.CredentialHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class ExtendedPopupMenu extends JPopupMenu {
	private static JDialog popupDialog;
	private static JMenuItem login;
	private static ExtendedTrayIcon trayIcon;
	private static ExtendedPopupMenu popupMenu;
	private static ControlPanelForm controlPanelForm;

	public static ExtendedPopupMenu getExtendedPopupMenu(ExtendedTrayIcon _trayIcon, ControlPanelForm _controlPanelForm) {
		if (popupMenu == null) {
			popupMenu = new ExtendedPopupMenu(_trayIcon, _controlPanelForm);
		}

		return popupMenu;
	}

	private ExtendedPopupMenu(ExtendedTrayIcon _trayIcon, ControlPanelForm _controlPanelForm) {
		trayIcon = _trayIcon;
		controlPanelForm = _controlPanelForm;

		popupMenu = this;

		popupDialog = new JDialog();
		popupDialog.setUndecorated(true);
		popupDialog.setAlwaysOnTop(true);

		SettingsMenuListener settingsMenuListener = new SettingsMenuListener();
		JMenu settingsMenu = new JMenu("Settings");
		JMenuItem history = new JMenuItem("History");
		history.addActionListener(settingsMenuListener);
		settingsMenu.add(history);
		JMenuItem settings = new JMenuItem("Settings");
		settings.addActionListener(settingsMenuListener);
		settingsMenu.add(settings);
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(settingsMenuListener);
		settingsMenu.add(about);
		this.add(settingsMenu);

		JMenuItem calibrate = new JMenuItem("Calibrate");
		calibrate.addActionListener(new CalibrateListener());
		this.add(calibrate);
		login = new JMenuItem("Login");
		login.addActionListener(new LoginListener());
		this.add(login);
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ExitListener());
		this.add(exit);
	}

	public static void showPopupMenu(MouseEvent event) {
		if (event.getButton() == 3) {
			// Place the popup-menu so that it's bottom left coordinate is at the click location
			Dimension size = popupMenu.getPreferredSize();
			int x = event.getXOnScreen();
			int y = event.getYOnScreen() - size.height;

			popupDialog.setLocation(x, y);
			popupDialog.setVisible(true);
			popupMenu.show(popupDialog.getContentPane(), 0, 0);
			popupDialog.toFront();
		}
	}


	/**
	 * * Event Listeners ***
	 */
	private class SettingsMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			JMenuItem item = (JMenuItem) event.getSource();
			String text = item.getText();

			if ("About".equals(text)) {
				controlPanelForm.switchToTab(ControlPanelForm.ControlPanelTab.ABOUT);
			} else if ("History".equals(text)) {
				controlPanelForm.switchToTab(ControlPanelForm.ControlPanelTab.HISTORY);
			} else if ("Settings".equals(text)) {
				controlPanelForm.switchToTab(ControlPanelForm.ControlPanelTab.SETTINGS);
			}

			controlPanelForm.setVisible(true);
			controlPanelForm.toFront();
		}
	}

	private class LoginListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if ("Login".equals(login.getText())) {
				LoginForm.display(true);
			} else {
				LoginForm.destroy();
				login.setText("Login");
				CredentialHandler.setLoginStatus(false);
			}
		}
	}

	private class ExitListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if (trayIcon != null) {
				SystemTray.getSystemTray().remove(trayIcon);
			}

			System.exit(0);
		}
	}

	private class CalibrateListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if (trayIcon != null) {
				trayIcon.calibrateDropForm();
			}
		}
	}
}
