package com.megacitycab.dao;

import com.megacitycab.config.DBConnection;
import com.megacitycab.dao.WalletDAO;
import com.megacitycab.model.AdminWallet;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

public class AdminWalletDAOImpl implements AdminWalletDAO {

    @Override
    public AdminWallet getWalletByAdminId(int adminId) throws SQLException {
        AdminWallet wallet = null;
        String query = "SELECT * FROM AdminWallet WHERE admin_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, adminId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    wallet = new AdminWallet();
                    wallet.setWalletId(rs.getInt("wallet_id"));
                    wallet.setAdminId(rs.getInt("admin_id"));
                    wallet.setBalance(rs.getBigDecimal("balance"));
                    wallet.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    wallet.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
            }
        }
        
        return wallet;
    }

    @Override
    public boolean createWallet(AdminWallet wallet) throws SQLException {
        String query = "INSERT INTO AdminWallet (admin_id, balance) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, wallet.getAdminId());
            stmt.setBigDecimal(2, wallet.getBalance());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean updateWalletBalance(int adminId, BigDecimal newBalance) throws SQLException {
        String query = "UPDATE AdminWallet SET balance = ?, updated_at = ? WHERE admin_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, adminId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
