package com.hexhaus.icarrus.ui;

import com.hexhaus.icarrus.dao.LoggingDao;
import com.hexhaus.icarrus.dao.UploadDao;
import com.hexhaus.icarrus.handler.MessageHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

/**
 * User: 67726e
 * Date: 8/13/11
 * Time: 7:26 PM
 */
class DropForm extends JDialog {

	/**
	 * Creates a new undecorated, non-focusing, ever-present form for use as a drop target
	 *
	 * @param size     is the size of the TrayIcon image as per the host OS
	 * @param trayIcon is the TrayIcon used by the application
	 */
	public DropForm(Dimension size, ExtendedTrayIcon trayIcon) {
		//super(null, "", ModalityType.MODELESS, device.getDefaultConfiguration());

		this.setUndecorated(true);
		this.setAlwaysOnTop(true);
		this.setFocusable(false);
		this.setFocusableWindowState(false);
		this.setSize(size);
		this.getContentPane().add(new DropPanel(size));
		this.setVisible(true);

		try {
			Class<?> awtUtilities = Class.forName("com.sun.awt.AWTUtilities");
			Method setWindowOpacity = awtUtilities.getMethod("setWindowOpacity", Window.class, float.class);
			setWindowOpacity.invoke(null, this, 1f);
		} catch (Exception e) {
			MessageHandler.postMessage("Translucency Error",
					"Required translucency methods could not be accessed.", LoggingDao.Status.FatalError);
		}
	}


	private class DropPanel extends JPanel {

		public DropPanel(Dimension size) {
			this.setLayout(null);

			JTextField dropTargetComponent = new JTextField();
			dropTargetComponent.setBounds(0, 0, size.width, size.height);
			dropTargetComponent.setEnabled(false);
			dropTargetComponent.setDragEnabled(true);
			dropTargetComponent.addMouseListener(new DropMouseListener());
			this.add(dropTargetComponent);

			// Implement the drop target listener on the drop component for this form
			new DropTarget(dropTargetComponent, new DropTargetListenerImpl());
		}

		private class DropMouseListener implements MouseListener {
			public void mousePressed(MouseEvent event) {
			}

			public void mouseReleased(MouseEvent event) {
			}

			public void mouseExited(MouseEvent event) {
			}

			public void mouseEntered(MouseEvent event) {
			}

			public void mouseClicked(MouseEvent event) {
				ExtendedPopupMenu.showPopupMenu(event);
			}
		}

		private class DropTargetListenerImpl implements DropTargetListener {
			private UploadDao uploadDAO = new UploadDao();

			public void dragEnter(DropTargetDragEvent event) {
			}

			public void dragExit(DropTargetEvent event) {
			}

			public void dragOver(DropTargetDragEvent event) {
			}

			public void dropActionChanged(DropTargetDragEvent event) {
			}

			@SuppressWarnings("unchecked")
			public void drop(DropTargetDropEvent event) {
				Transferable transferable = event.getTransferable();
				java.util.List<DataFlavor> flavors = event.getCurrentDataFlavorsAsList();
				for (DataFlavor flavor : flavors) {
					if (flavor.isFlavorJavaFileListType()) {
						event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

						try {
							// Acquire a list of all the dropped files
							List<File> files = (List<File>) transferable.getTransferData(flavor);

							if (files.size() == 1) {
								// If there is a singular file, upload it
								uploadDAO.uploadFile(files.get(0));
							}
							//else if (files.size() > 1) uploadDAO.uploadFiles(files);
							// TODO: Create method to allow a list of files to be passed in and uploaded
						} catch (Exception e) {
							e.printStackTrace();
							MessageHandler.postMessage("Drop Error",
									"The dropped file(s) could not be processed.", LoggingDao.Status.Error);
							// Reject an invalid drop operation
							event.rejectDrop();
							return;
						}
					}
				}
			}
		}
	}
}
