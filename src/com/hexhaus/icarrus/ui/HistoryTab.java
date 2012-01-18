package com.hexhaus.icarrus.ui;

import com.hexhaus.icarrus.dao.IdatDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: 67726e
 * Date: 10/29/11
 * Time: 3:02 PM
 */
public class HistoryTab extends JPanel {
	private static JTable historyTable;
    private static DefaultTableModel tableModel;
    private final int SCROLL_WIDTH = 440, SCROLL_HEIGHT = 150;
	private static IdatDao historyDao;

    public HistoryTab() {
    	this.setLayout(null);

		historyDao = new IdatDao("File", "data/history.idat");				// Create DAO for reading/writing to the history file
		List<Map<String, String>> history = null;
		try { history = historyDao.readIdatFile(); }						// Convert the IDAT data into a format readable by the JTable
		catch (IOException ignore) {ignore.printStackTrace();}
		// TODO: Log error and announce to user

		Object[][] data = toTableData(history);
        String[] columns = {"File Name", "URL", "Size", "Date"};

        tableModel = new DefaultTableModel(data, columns);
        historyTable = new JTable(tableModel);
        historyTable.setPreferredScrollableViewportSize(new Dimension(SCROLL_WIDTH, SCROLL_HEIGHT));
        historyTable.setFillsViewportHeight(true);
        historyTable.addMouseListener(new HistoryMouseListener());

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBounds(20, 20, SCROLL_WIDTH, SCROLL_HEIGHT);
        this.add(scrollPane);
    }

    private static void insertRow(Object[] data) {
		tableModel.insertRow(0, data);

		Map<String, String> block = new HashMap<String, String>();		// Convert the Object into a Map to be written to the IDAT file
		block.put("name", (String)data[0]);
		block.put("url", (String)data[1]);
		block.put("size", (String)data[2]);
		block.put("date", (String)data[3]);

		try { historyDao.appendBlock(block); }							// Write out the Map data to the IDAT history file
		catch (IOException e) {e.printStackTrace();}
		// TODO: Log this exception
	}

	public void clearTable() {tableModel.getDataVector().removeAllElements();}


	/**
	 * Method converts the List/Map object used to transport parsed IDAT data into an Object[][] form
	 * used by the JTable to load display data.
	 *
	 * @param list Object containing the parsed history IDAT file
	 * @return An Object[][] if data is available, null otherwise
	 */
     private Object[][] toTableData(List<Map<String, String>> list) {
     	if (list == null) { return null; }
		Object[][] data = new Object[list.size()][0];

		for (int i = 0; i < data.length; i++) {
			Map<String, String> block = list.get(i);
			Object[] blockData = new Object[4];

			blockData[0] = block.get("name");
			blockData[1] = block.get("url");
			blockData[2] = block.get("size");
			blockData[3] = block.get("date");

			data[i] = blockData;
		}

        return data;
     }

     private class HistoryMouseListener implements MouseListener {
     	public void mouseClicked(MouseEvent event) {}
        public void mouseEntered(MouseEvent event) {}
        public void mouseExited(MouseEvent event) {}
        public void mousePressed(MouseEvent event) {}
        public void mouseReleased(MouseEvent event) {}
     }
}
