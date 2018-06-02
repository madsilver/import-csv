package br.com.silver.utils;

import java.io.File;
import java.io.IOException;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

public class Config {

	/**
	 * Get config
	 * @return
	 */
	public Ini getConfig() {
		try {
			String path = System.getProperty("user.dir") + "/config/config.ini";
			Ini ini = new Ini();
			ini.load(new File(path));
			
			return ini;

		} catch (InvalidFileFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
