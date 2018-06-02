package br.com.silver.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReaderCSV {
	
	/**
	 * Read csv file
	 * @param file
	 */
	public static void reader(File file, IReaderCSV rc) {
		String line;
		BufferedReader buffer = null;
		
		try {
			buffer = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		try {
			buffer.readLine();
			
			while((line = buffer.readLine()) != null) {
				ArrayList<String> data = parseCSV(line);
				new Thread() {
					public void run() {
						rc.lineReady(data);
					}
				}.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parse csv file
	 * @param csv
	 * @return
	 */
	private static ArrayList<String> parseCSV(String csv) {
		ArrayList<String> result = new ArrayList<String>();
		
		if (csv != null) {
			String[] splitData = csv.split("\\s*;\\s*");
			for (int i = 0; i < splitData.length; i++) {
				if (!(splitData[i] == null) || !(splitData[i].length() == 0)) {
					result.add(splitData[i].trim());
				}
			}
		}
		
		return result;
	}
}
