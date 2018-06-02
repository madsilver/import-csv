package br.com.silver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.silver.model.Client;
import br.com.silver.repository.ConnectionFactory;

public class FinanceDao {
	
	public static String TABLE = "financeiro";
	private ConnectionFactory repository;
	
	public FinanceDao(ConnectionFactory repository) {
		this.repository = repository;
	}
	
	/**
	 * Update finance
	 * @param finance
	 * @return
	 */
	public boolean isPaid(Client client) {
		try {
			Connection conn = this.repository.getConnection();

	        String sql = "SELECT situacao FROM" + TABLE;
	        sql += " WHERE cliente = ? AND situacao = 'P'";
	        PreparedStatement stmt = conn.prepareStatement(sql);
	        stmt.setInt(1, client.getId()); 
	        
	        ResultSet rs = stmt.executeQuery();
	        String situacao = null;
			while (rs.next()) {
				System.out.println(rs);
				situacao = rs.getString("situacao");
				
				if(situacao == "P") {
					return true;
				}
			}
	        
	        return false;
		} catch (SQLException e) {
			return false;
		}
	}

}
