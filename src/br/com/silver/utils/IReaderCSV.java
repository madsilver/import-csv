package br.com.silver.utils;

import java.util.ArrayList;

public interface IReaderCSV {
	/**
	 * Function called after read file
	 * @param data
	 */
	public void afterRead(ArrayList<String> data);
}
