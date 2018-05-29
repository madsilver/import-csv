package br.com.silver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.com.silver.model.Finance;
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
	public String update(Finance finance) {
		try {
			Connection conn = this.repository.getConnection();

	        String sql = "UPDATE " + TABLE + " SET status = ? WHERE cliente = ?";
	        PreparedStatement stmt;
	        
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, finance.getStatus());
	        stmt.setInt(2, finance.getClient().getId());

	        stmt.execute();
	        stmt.close();
	        
	        return null;
		} catch (SQLException e) {
			return e.getMessage();
		}
	}

}
