package com.hexhaus.icarrus.dao;

import com.hexhaus.icarrus.handler.MessageHandler;

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
public class IdatDao {
	private String blockStart;
	private File idatFile;

	public IdatDao(String blockName, String idatFileName) {
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
		if (!idatFile.exists()) return null;
		List<Map<String, String>> blocks = new LinkedList<Map<String, String>>();

		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(idatFile));
			blocks = readIdatFromBuffer(in, blockStart);
		} finally {
			try {
				if (in != null) in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return blocks;
	}

	/**
	 * Method used to read in an IDAT file from a reader and parses it into blocks
	 * in a standardized format.
	 *
	 * @param in         The reader from which the parser gets its content
	 * @param blockStart The name for the start of an IDAT block
	 * @return A List/Map object containing the parsed data, or null if it could not be read
	 * @throws IOException Thrown if there is a problem reading the IDAT data
	 */
	public static List<Map<String, String>> readIdatFromBuffer(BufferedReader in, String blockStart) throws IOException {
		List<Map<String, String>> blocks = new LinkedList<Map<String, String>>();
		Map<String, String> block = null;
		boolean inBlock = false;
		String line = "";

		while ((line = in.readLine()) != null) {
			if (inBlock) {
				// Check if the current block has ended
				if (line.equals("}")) {
					inBlock = false;
					// Add the prior block to the list of blocks for storage
					if (block != null) blocks.add(block);
				} else {
					int index = line.indexOf("=");
					// Skip this line if it doesn't contain a valid key/value pair
					if (index == -1 || index == 0 || line.length() < 3 ||
							(line.length() - index) == 1) continue;

					String key = line.substring(0, index).trim();
					String value = line.substring(index + 1, line.length()).trim();
					block.put(key, value);
				}
			} else {
				// Check if the parser has entered a new block
				if (line.equals(blockStart)) {
					inBlock = true;
					// Create a new Map to hold the new block
					block = new HashMap<String, String>();
				}
			}
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
		// Create the IDAT file if it does not already exist
		if (!idatFile.exists()) idatFile.createNewFile();
		BufferedWriter out = null;

		try {
			out = new BufferedWriter(new FileWriter(idatFile));

			// Start the IDAT block
			out.write("\n" + blockStart);
			for (Map.Entry<String, String> entry : block.entrySet()) {
				// Write out the key/value pairs
				out.write("\n" + entry.getKey() + "=" + entry.getValue());
			}

			// Close the IDAT block
			out.write("}");

		} finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (Exception e) {
				MessageHandler.postMessage("IO Error",
						"Unable to close output stream for " + idatFile.getName(), LoggingDao.Status.Error);
			}
		}
	}
}
