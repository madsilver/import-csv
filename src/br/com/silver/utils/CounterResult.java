package br.com.silver.utils;

public class CounterResult {
	
	private int total;
	private int client;
	private int notFound;
	private int notUpdated;
	
	public CounterResult() {
		this.total = 0;
		this.client = 0;
		this.notFound = 0;
		this.notUpdated = 0;
	}
	
	public void setTotal() {
		this.total++;
	}
	public void setClient() {
		this.client++;
	}
	public void setNotFound() {
		this.notFound++;
	}
	public void setNotUpdated() {
		this.notUpdated++;
	}
	
	public String toString() {
		String result = "\nTotal\t\tSuccess\t\tNot Found\t\tPaid\n";
		result += new String(new char[155]).replace("\0", "-");
		result += String.format("\n%s\t\t%s\t\t%s\t\t%s", 
				this.total,
				this.client,
				this.notFound,
				this.notUpdated);
		return result;
	}

}
