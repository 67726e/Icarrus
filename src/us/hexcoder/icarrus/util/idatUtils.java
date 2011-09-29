package us.hexcoder.icarrus.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * User: 67726e
 * Date: 07.09.11
 * Time: 10:31
 */
public class idatUtils {
	private idatUtils() {}

	/**
	 * Method reads an input stream and parses the incoming data using the IDAT file format specification
	 * and creates a HashMap containing each key/value pair.
	 *
	 * @param inputStream an input stream from which the data is read.
	 * @return a HashMap containing all valid key/value pairs from the input stream
	 * @throws Exception if a major error occurs while attempting to parse the incoming data
	 */
	public static HashMap<String, String> parseIDATResponse(InputStream inputStream) throws Exception {
		HashMap<String, String> map = new HashMap<String, String>();
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		String line;

		while ((line = in.readLine()) != null) {
			if (line.charAt(0) != '~') continue;
			String[] arguments = line.split(":");
			if (arguments.length != 2) continue;

			map.put(arguments[0], arguments[1]);
		}

		in.close();
		return map;
	}
}
