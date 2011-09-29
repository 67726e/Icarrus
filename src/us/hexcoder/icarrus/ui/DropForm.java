package us.hexcoder.icarrus.ui;

import us.hexcoder.icarrus.dao.LoggingDAO;
import us.hexcoder.icarrus.dto.MessageHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;

/**
 * User: 67726e
 * Date: 8/13/11
 * Time: 7:26 PM
 */
public class DropForm extends JDialog {
    private ExtendedTrayIcon trayIcon;

    /**
     * Creates a new undecorated, non-focusing, ever-present form for use as a drop target
     *
     * @param size is the size of the TrayIcon image as per the host OS
     * @param trayIcon is the TrayIcon used by the application
     */
    public DropForm(Dimension size, ExtendedTrayIcon trayIcon) {
        this.trayIcon = trayIcon;

        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.setFocusable(false);
        this.setFocusableWindowState(false);
        this.setSize(size);
        this.getContentPane().add(new DropPanel(size));

        try {
            Class<?> awtUtilities = Class.forName("com.sun.awt.AWTUtilities");
            Method setWindowOpacity = awtUtilities.getMethod("setWindowOpacity", Window.class, float.class);
            setWindowOpacity.invoke(null, this, 0.01f);
        } catch (Exception e) {
            MessageHandler.postMessage("Translucency Error", "Required translucency methods could not be accessed.", LoggingDAO.Status.FATAL_ERROR);
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

            DropTarget dropTarget = new DropTarget(dropTargetComponent, new DropTargetListenerImpl());                  // Implement the drop target listener on the drop component for this form
        }

        private class DropMouseListener implements MouseListener {
            public void mousePressed(MouseEvent event) {}
            public void mouseReleased(MouseEvent event) {}
            public void mouseExited(MouseEvent event) {}
            public void mouseEntered(MouseEvent event) {}
            public void mouseClicked(MouseEvent event) {
                trayIcon.showPopupMenu(event);                                                                          // Pass on the event object to determine if the popup menu should be shown
            }
        }

        private class DropTargetListenerImpl implements DropTargetListener {
            public void dragEnter(DropTargetDragEvent event) {}
            public void dragExit(DropTargetEvent event) {}
            public void dragOver(DropTargetDragEvent event) {}
            public void dropActionChanged(DropTargetDragEvent event) {}
            public void drop(DropTargetDropEvent event) {
                Transferable transferable = event.getTransferable();
                java.util.List<DataFlavor> flavors = event.getCurrentDataFlavorsAsList();
                for (DataFlavor flavor : flavors) {
                    if (flavor.isFlavorJavaFileListType()) {
                        event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                        java.util.List<?> data = null;

                        try { data = (java.util.List)transferable.getTransferData(flavor); }                            // Create a list of all the files dropped
                        catch (Exception e) {
                            MessageHandler.postMessage("Drop Error", "The dropped file(s) could not be processed.", LoggingDAO.Status.ERROR);
                            event.rejectDrop();                                                                         // Reject an invalid drop operation
                            return;
                        }

                        // TODO: Develop specification for uploading multiple files
                    }
                }
            }
        }
    }
}
