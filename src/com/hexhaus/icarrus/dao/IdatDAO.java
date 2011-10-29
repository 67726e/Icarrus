package com.hexhaus.icarrus.dao;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * User: 67726e
 * Date: 10/27/11
 * Time: 1:26 PM
 */
public class IdatDAO {
	private String blockStart;
	private File idatFile;

	public IdatDAO(String blockName, String idatFileName) {
		this.blockStart = blockName + " {";
		this.idatFile = new File(idatFileName);
	}

	/**
	 * Method reads the IDAT file and parses the blocks into key/value pairs.
	 *
	 * @return A list of key/value pairs in a map
	 * @throws IOException Thrown if an error occurs while reading the IDAT file
	 */
	public List<Map<String, String>> readIdatFile() throws IOException {
		List<Map<String, String>> blocks = new LinkedList<Map<String, String>>();
		Map<String, String> block = null;
		boolean inBlock = false;
		String line = "";

		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(idatFile));

			while ((line = in.readLine()) != null) {
				if (inBlock) {
					if (line.equals("}")) {												// Check if the current block has ended
						inBlock = false;
						if (block != null) blocks.add(block);							// Add the prior block to the list of blocks for storage
						continue;														// Continue parsing with the next line
					} else {
						int index = line.indexOf("=");
						if (index == -1 || index == 0 || line.length()  < 3 ||
								(line.length() - index) == 1) continue;					// Skip this line if it doesn't contain a valid key/value pair

						String key = line.substring(0, index).trim();
						String value = line.substring(index + 1, line.length()).trim();
						block.put(key, value);
					}
				} else {
					if (line.equals(blockStart)) {										// Check if the parser has entered a new block
						inBlock = true;
						block = new HashMap<String, String>();							// Create a new Map to hold the new block
					}
				}
			}
		} finally {
			try { if (in != null) in.close(); }
			catch (Exception e) {}
		}

		return blocks;
	}

	/**
	 * Method writes out an IDAT data block to the IDAT file for this object from the provided key/value map
	 * and the existing block data information.
	 *
	 * @param block Map containing the key/value pairs for the internal portion of the data block
	 * @throws IOException Thrown if an exception occurs while writing the data to the IDAT file
	 */
	public void appendBlock(Map<String, String> block) throws IOException {
		if (!idatFile.exists()) idatFile.createNewFile();					// Create the IDAT file if it does not already exist
		BufferedWriter out = null;

		try {
			out = new BufferedWriter(new FileWriter(idatFile));

			out.write("\n" + blockStart);									// Start the IDAT block
			for (Map.Entry<String, String> entry : block.entrySet()) {
				out.write("\n" + entry.getKey() + "=" + entry.getValue());	// Write out the key/value pairs
			}

			out.write("}");													// Close the IDAT block

		} catch (IOException e) {
			// TODO: Log output exception
			throw e;
		} finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (Exception e) {}
		}
	}
}
