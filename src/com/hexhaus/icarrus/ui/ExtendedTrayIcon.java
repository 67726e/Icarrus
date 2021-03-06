package com.hexhaus.icarrus.ui;

import com.hexhaus.icarrus.dao.ImageDao;
import com.hexhaus.icarrus.dao.LoggingDao;

import com.hexhaus.icarrus.handler.MessageHandler;
import com.hexhaus.imagelocator.ImageLocator;
import com.hexhaus.imagelocator.RandomImage;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.TimerTask;

public class ExtendedTrayIcon extends TrayIcon {
	private TrayIcon trayIcon = this;
	private DropForm dropForm;
	private ImageLocator imageLocator;
	private ExtendedPopupMenu popupMenu;

	public static void setLoginStatus(String status) {
//		login.setText(status);
	}

	public ExtendedTrayIcon(Image image) {
		super(image);
		this.setImageAutoSize(false);
		this.addMouseListener(new TrayMouseListener());

		try {
			SystemTray.getSystemTray().add(this);
		} catch (AWTException e) {
			MessageHandler.postMessage("System Tray Error",
					"The tray icon could not be added to your system tray.", LoggingDao.Status.FatalError);
		}

		// Create and position the dialog used to catch file drops
		calibrateDropForm();

		ControlPanelForm controlPanelForm = new ControlPanelForm();

		// Used as in lieu of the standard AWT popup menu used by TrayIcon
		popupMenu = ExtendedPopupMenu.getExtendedPopupMenu(this, controlPanelForm);

		LoginForm loginForm = LoginForm.getLoginForm();
		loginForm.setVisible(true);

		// Brings the tray icon to the front of the display
		java.util.Timer timer = new java.util.Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (!popupMenu.isVisible()) {
					dropForm.toFront();
				}
			}
		}, 0, 1000);

		// Provide the message handler the tray icon for displaying tray icon messages
		MessageHandler.setTrayIcon(this);
	}

	private GraphicsDevice getDeviceWithTrayIcon() throws RuntimeException {
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = environment.getScreenDevices();

		for (GraphicsDevice device : devices) {
			if (deviceContainsTrayIcon(device))
				return device;
		}

		throw new RuntimeException("Could not locate the tray icon!");
	}

	private boolean deviceContainsTrayIcon(GraphicsDevice device) {
		Robot robot;
		try {
			robot = new Robot(device);
		} catch (AWTException e) {
			MessageHandler.postMessage("Robot Failure", "Could not create a screen capture.", LoggingDao.Status.Error);
			return false;
		}

		int trayWidth = trayIcon.getSize().width;
		int trayHeight = trayIcon.getSize().height;
		BufferedImage randomImage = new RandomImage(trayWidth, trayHeight,
				RandomImage.ImageStyle.Stripe3Column).getImage();

		trayIcon.setImage(randomImage);
		BufferedImage screenCapture = robot.createScreenCapture(
				new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		trayIcon.setImage(ImageDao.getImage("tray_icon.png"));

		imageLocator = new ImageLocator(screenCapture, randomImage);
		imageLocator.search(2);

		return imageLocator.isImageFound();
	}

	public void calibrateDropForm() {
		if (dropForm != null) {
			dropForm.setVisible(false);
		}

		try {
			GraphicsDevice device = getDeviceWithTrayIcon();

			if (device != null) {
				if (dropForm == null) {
					dropForm = new DropForm(this.getSize(), this);
				}

				dropForm.setLocation(((int) (imageLocator.getFirstOccurrence().getX() + device.getDefaultConfiguration().getBounds().getX())),
						(int) (imageLocator.getFirstOccurrence().getY() + device.getDefaultConfiguration().getBounds().getY()));
			}
		} catch (RuntimeException e) {
			MessageHandler.postMessage("Runtime Exception",
					"Could not locate the system tray icon", LoggingDao.Status.Error);
		}

		if (dropForm != null) {
			dropForm.setVisible(true);
		}
	}

	private class TrayMouseListener implements MouseListener {
		public void mouseClicked(MouseEvent event) {

		}

		public void mouseEntered(MouseEvent event) {

		}

		public void mouseExited(MouseEvent event) {

		}

		public void mousePressed(MouseEvent event) {
			// TODO: Add display of an alternative file drop
		}

		public void mouseReleased(MouseEvent event) {
			// Call action to show the Swing based popup-menu
			ExtendedPopupMenu.showPopupMenu(event);
		}
	}
}
