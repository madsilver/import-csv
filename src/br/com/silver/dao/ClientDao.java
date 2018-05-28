package br.com.silver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.silver.model.Client;
import br.com.silver.repository.ConnectionFactory;

public class ClientDao {
	
	public static String TABLE = "clientes";
	
	/**
	 * Get client by id
	 * @param id
	 * @return
	 */
	public Client get(int id) {
		Client client = null;

	    try {
	    	ConnectionFactory cf = ConnectionFactory.getInstance();
			Connection conn = cf.getConnection();
			
			String sql = "SELECT * FROM " + TABLE + " WHERE id = ?";
			
	    	PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
	    	
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				client = new Client();
			    client.setId(rs.getInt("id"));
				client.setVc(rs.getString("vccliente"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	    
	    
	    return client;
	}
	
	/**
	 * Get client by CPF
	 * @param cpf
	 * @return
	 */
	public Client getByCpf(String cpf) {
		Client client = null;

	    try {
	    	ConnectionFactory cf = ConnectionFactory.getInstance();
			Connection conn = cf.getConnection();
			
			String sql = "SELECT * FROM " + TABLE + " WHERE cpf LIKE ?";
			
	    	PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, cpf);
	    	
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				client = new Client();
			    client.setId(rs.getInt("id"));
				client.setVc(rs.getString("vccliente"));
				client.setCpf(rs.getString("cpf"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	    
	    
	    return client;
	}

	/**
	 * Update client
	 * @param client
	 * @return
	 */
	public String update(Client client) {
		try {
			ConnectionFactory cf = ConnectionFactory.getInstance();
			Connection conn = cf.getConnection();

	        String sql = "UPDATE " + TABLE + " SET vccliente = ? WHERE id = ? ";
	        
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, client.getVc());
	        stmt.setLong(2, client.getId());

	        stmt.execute();
	        stmt.close();
	        
	        return null;
		} catch (SQLException e) {
			return e.getMessage();
		}
    }
}
