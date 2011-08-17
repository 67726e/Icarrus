package com.hexcoder.icarrus.ui;

import com.hexcoder.icarrus.dao.LoggingDAO;
import com.hexcoder.icarrus.dto.MessageHandler;

import javax.swing.*;
import javax.tools.JavaCompiler;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: 67726e
 * Date: 8/13/11
 * Time: 7:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class DropForm extends JDialog {

    /**
     * Creates a new undecorated, non-focusing, ever-present form for use as a drop target
     *
     * @param size
     */
    public DropForm(Dimension size) {
        // TODO: Determine mechanism to allow transparency on JRE 6 & 7

        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.setFocusable(false);
        this.setFocusableWindowState(false);
        this.setSize(size);
        this.getContentPane().add(new DropPanel(size));
    }


    private class DropPanel extends JPanel {

        public DropPanel(Dimension size) {
            this.setLayout(null);

            JTextField dropTargetComponent = new JTextField();
            dropTargetComponent.setBounds(0, 0, size.width, size.height);
            dropTargetComponent.setEnabled(false);
            dropTargetComponent.setDragEnabled(true);
            this.add(dropTargetComponent);

            DropTarget dropTarget = new DropTarget(dropTargetComponent, new DropTargetListenerImpl());                  // Implement the drop target listener on the drop component for this form
        }

        // TODO: Create method to bring this dialog to the front of the screen

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
                            MessageHandler.postMessage("Drop Error", "The dropped file(s) could not be processed.", LoggingDAO.ERROR);
                            event.rejectDrop();                                                                         // Reject an invalid drop operation
                            return;
                        }

                        // TODO: Implement single file upload for the time being
                        // TODO: Develop specification for uploading multiple files
                    }
                }
            }
        }
    }
}
