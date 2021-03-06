package com.hexhaus.icarrus.ui;

import com.hexhaus.icarrus.dao.*;
import com.hexhaus.icarrus.handler.MessageHandler;
import com.hexhaus.icarrus.handler.SettingsHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

/**
 * User: 67726e
 * Date: 8/6/11
 * Time: 2:12 PM
 */
public class ControlPanelForm extends JFrame {
	private final int WIDTH = 500, HEIGHT = 300;
	private JFrame form = this;                                                                 // Pointer to the JFrame for use by inner classes
	private ControlPanelPanel panel;                                                            // Pointer to the JPanel for this form
	private String helpURL = "http://help.icarr.us/";                                           // URL to the Icarrus help pages

	public enum ControlPanelTab {HISTORY, SETTINGS, ABOUT}                                      // Enum used to determine which tab to switch to

	public void switchToTab(ControlPanelTab tab) {
		panel.switchToTab(tab);
	}

	public ControlPanelForm() {
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = environment.getDefaultScreenDevice();
		DisplayMode displayMode = device.getDisplayMode();                                      // Get object containing the size of the available screen real estate
		int windowX = ((displayMode.getWidth() - WIDTH) / 2);                                   // Calculate X coordinate so the window is horizontally centered
		int windowY = ((displayMode.getHeight() - HEIGHT) / 2);                                 // Calculate Y coordinate so the window is vertically centered

		panel = new ControlPanelPanel();

		this.setBounds(windowX, windowY, WIDTH, HEIGHT);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setResizable(false);
		this.setLayout(null);
		this.setTitle("Icarrus ~ Settings");
		this.getContentPane().add(panel);                                                       // Add the JPanel with the form's contents
		this.setIconImage(ImageDao.getImage("wingIcon-32x32.png"));


		/** Setup JMenuBar **/
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ExitMenuListener());
		fileMenu.add(exit);
		menuBar.add(fileMenu);
		JMenu helpMenu = new JMenu("Help");
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new AboutItemListener());
		helpMenu.add(about);
		JMenuItem help = new JMenuItem("Help");
		help.addActionListener(new HelpItemListener());
		helpMenu.add(help);
		menuBar.add(helpMenu);
		this.setJMenuBar(menuBar);
	}

	private class ControlPanelPanel extends JPanel {
		private JTabbedPane tabbedPane;

		/**
		 * Method called to switch between the tabs on the control panel. ExtendedTrayIcon's menu items will indirectly
		 * call this method via ControlPanelForm which in turn calls this method. Also the "About" menu item action
		 * listener on the menu bar uses this method.
		 *
		 * @param tab is the designator for the tab to switch to.
		 */
		public void switchToTab(ControlPanelTab tab) {
			switch (tab) {
				case HISTORY:
					tabbedPane.setSelectedIndex(0);
					break;
				case SETTINGS:
					tabbedPane.setSelectedIndex(1);
					break;
				case ABOUT:
					tabbedPane.setSelectedIndex(2);
					break;
			}
		}

		public ControlPanelPanel() {
			this.setLayout(null);
			this.setBounds(0, 0, 500, 300);

			tabbedPane = new JTabbedPane();
			tabbedPane.setBounds(0, 0, 500, 280);
			tabbedPane.addTab("History", new HistoryTab());
			tabbedPane.addTab("Settings", new SettingsTab());
			tabbedPane.addTab("About", new AboutTab());
			this.add(tabbedPane);
		}
	}

	private class SettingsTab extends JPanel {
		private JCheckBox deleteHistory, saveHistory, copyURL, saveLog, displayMessages;
		private JTextField uploadURL, loginURL;

		public SettingsTab() {
			this.setLayout(null);
			new SettingsDao().loadSettingsFromFile();                                                                   // Load the settings from the Settings file for a proper display of user settings

			deleteHistory = new JCheckBox("Delete History", false);
			deleteHistory.setBounds(20, 20, 100, 20);
			this.add(deleteHistory);
			saveHistory = new JCheckBox("Save History", SettingsHandler.getSaveHistoryToFile());
			saveHistory.setBounds(20, 50, 100, 20);
			this.add(saveHistory);
			copyURL = new JCheckBox("Copy URLs", SettingsHandler.getCopyURLToClipboard());
			copyURL.setBounds(20, 80, 100, 20);
			this.add(copyURL);
			saveLog = new JCheckBox("Save Logs", SettingsHandler.getSaveMessagesToLog());
			saveLog.setBounds(20, 110, 100, 20);
			this.add(saveLog);
			displayMessages = new JCheckBox("Display Messages", SettingsHandler.getDisplayMessagesToUser());
			displayMessages.setBounds(20, 140, 120, 20);
			this.add(displayMessages);


			JLabel loginURLLabel = new JLabel("Login Server URL:");
			loginURLLabel.setBounds(160, 20, 100, 20);
			this.add(loginURLLabel);
			loginURL = new JTextField(SettingsHandler.getLoginServerURL());
			loginURL.setBounds(260, 20, 200, 20);
			this.add(loginURL);
			JLabel uploadURLLabel = new JLabel("Upload Server URL:");
			uploadURLLabel.setBounds(160, 50, 100, 20);
			this.add(uploadURLLabel);
			uploadURL = new JTextField(SettingsHandler.getUploadServerURL());
			uploadURL.setBounds(260, 50, 200, 20);
			this.add(uploadURL);


			JButton apply = new JButton("Apply");
			apply.setBounds(20, 190, 80, 25);
			apply.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					updateSettings();
				}
			});
			this.add(apply);
			JButton cancel = new JButton("Cancel");
			cancel.setBounds(110, 190, 80, 25);
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cancelSettings();
				}
			});
			this.add(cancel);
		}

		/**
		 * Private method called when the "Apply" button is clicked. Method writes out the settings
		 * to the settings file via SettingsDAO and sets the settings for the application runtime in
		 * SettingsHandler.
		 */
		private void updateSettings() {
			SettingsHandler.setCopyURLToClipboard(copyURL.isSelected());
			SettingsHandler.setDisplayMessagesToUser(displayMessages.isSelected());
			SettingsHandler.setLoginServerURL(loginURL.getText());
			SettingsHandler.setSaveHistoryToFile(saveHistory.isSelected());
			SettingsHandler.setSaveMessagesToLog(saveLog.isSelected());
			SettingsHandler.setUploadServerURL(uploadURL.getText());

			new SettingsDao().writeSettingsToFile();                                            // Write out the newly updated settings to the Settings file
		}

		/**
		 * Private method called when the "Cancel" button is clicked to reset any changes to the settings
		 * by returning their statuses to those in the SettingsHandler class.
		 */
		private void cancelSettings() {
			deleteHistory.setSelected(false);
			saveHistory.setSelected(SettingsHandler.getSaveHistoryToFile());
			copyURL.setSelected(SettingsHandler.getCopyURLToClipboard());
			saveLog.setSelected(SettingsHandler.getSaveMessagesToLog());
			displayMessages.setSelected(SettingsHandler.getDisplayMessagesToUser());

			uploadURL.setText(SettingsHandler.getUploadServerURL());
			loginURL.setText(SettingsHandler.getLoginServerURL());
		}
	}

	private class AboutTab extends JPanel {
		public AboutTab() {
			this.setLayout(null);

			JLabel authors = new JLabel("Authors: ");
			authors.setBounds(20, 20, 100, 20);
			this.add(authors);
			JLabel authorGlenn = new JLabel("Glenn Nelson");
			authorGlenn.setBounds(100, 20, 240, 20);
			this.add(authorGlenn);
			JLabel homepage = new JLabel("Homepage: ");
			homepage.setBounds(20, 70, 100, 20);
			this.add(homepage);
			JLabel homepageLink = new JLabel("http://icarr.us/");
			homepageLink.setBounds(100, 70, 90, 22);
			this.add(homepageLink);
		}
	}


	/**
	 * ** Event Listeners ****
	 */

	private class ExitMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			form.setVisible(false);                                                             // Hide this form for it is resource intensive to retrieve
		}
	}

	private class AboutItemListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			switchToTab(ControlPanelTab.ABOUT);                                                 // Switch to the "About" tab
		}
	}

	private class HelpItemListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if (!Desktop.isDesktopSupported())
				MessageHandler.postMessage("Help Error",
						"Could not open Icarrus Help pages. See " + helpURL, LoggingDao.Status.Warning);                // Check if a web browser can be opened
			try {
				Desktop.getDesktop().browse(new URI(helpURL));                                                          // Open the default browser to the Icarrus Help pages
			} catch (Exception e) {
				MessageHandler.postMessage("Help Error",
						"Could not open Icarrus Help pages. See " + helpURL, LoggingDao.Status.Warning);                // Inform the user he/she has to manually visit the help pages
			}
		}
	}
}
