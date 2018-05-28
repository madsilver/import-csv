package br.com.silver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.com.silver.model.Finance;
import br.com.silver.repository.ConnectionFactory;

public class FinanceDao {
	
	public static String TABLE = "financeiro";
	
	/**
	 * Update finance
	 * @param finance
	 * @return
	 */
	public String update(Finance finance) {
		try {
			ConnectionFactory cf = ConnectionFactory.getInstance();
			Connection conn = cf.getConnection();

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
