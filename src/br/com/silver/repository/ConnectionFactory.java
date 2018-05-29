package br.com.silver.repository;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

public class ConnectionFactory {
	
	private static ConnectionFactory instance;
	private Connection connection;
	
	/**
	 * Get connection
	 * @return Connection
	 */
	public ConnectionFactory() {
        try {
        	Ini ini = getConfig();
        	
        	String url = String.format("jdbc:mysql://%s:%s/%s", 
        			ini.get("server", "url"),
        			ini.get("server", "port"),
        			ini.get("server", "database"));
        	
        	Properties properties = new Properties();
        	properties.setProperty("user", ini.get("server", "user"));
        	properties.setProperty("password", ini.get("server", "pass"));
        	properties.setProperty("useSSL", "false");
        	properties.setProperty("autoReconnect", "true");
        	
        	this.connection = DriverManager.getConnection(url, properties);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
	
	public Connection getConnection() {
		return this.connection;
	}
	
	public static ConnectionFactory getInstance() throws SQLException {
		if (instance == null) {
            instance = new ConnectionFactory();
        } else if (instance.getConnection().isClosed()) {
            instance = new ConnectionFactory();
        }

		return instance;
	}
	
	/**
	 * Close connection with database
	 * @throws SQLException
	 */
	public static void close() throws SQLException {
		instance.getConnection().close();
	}
	
	/**
	 * Get if instance is connected
	 * @return
	 * @throws SQLException
	 */
	public boolean isConnected() throws SQLException {
		return !instance.getConnection().isClosed();
	}
	/**
	 * Get config
	 * @return
	 */
	private Ini getConfig() {
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