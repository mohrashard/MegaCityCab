package com.megacitycab.dao;

import com.megacitycab.config.DBConnection;

import com.megacitycab.model.AdminTransaction;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdminTransactionDAOImpl implements AdminTransactionDAO {

    @Override
    public List<AdminTransaction> getAllTransactionsByAdminId(int adminId) throws SQLException {
        List<AdminTransaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM AdminTransactions WHERE admin_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, adminId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AdminTransaction transaction = new AdminTransaction();
                    transaction.setTransactionId(rs.getInt("transaction_id"));
                    transaction.setAdminId(rs.getInt("admin_id"));
                    transaction.setAmount(rs.getBigDecimal("amount"));
                    transaction.setTransactionType(rs.getString("transaction_type"));
                    transaction.setDescription(rs.getString("description"));
                    transaction.setStatus(rs.getString("status"));
                    transaction.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    transactions.add(transaction);
                }
            }
        }
        
        return transactions;
    }

    @Override
    public List<AdminTransaction> getRecentTransactionsByAdminId(int adminId, int limit) throws SQLException {
        List<AdminTransaction> transactions = new ArrayList<>();
        String query = "SELECT TOP (?) * FROM AdminTransactions WHERE admin_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, adminId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AdminTransaction transaction = new AdminTransaction();
                    transaction.setTransactionId(rs.getInt("transaction_id"));
                    transaction.setAdminId(rs.getInt("admin_id"));
                    transaction.setAmount(rs.getBigDecimal("amount"));
                    transaction.setTransactionType(rs.getString("transaction_type"));
                    transaction.setDescription(rs.getString("description"));
                    transaction.setStatus(rs.getString("status"));
                    transaction.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    transactions.add(transaction);
                }
            }
        }
        
        return transactions;
    }

    @Override
    public boolean addTransaction(AdminTransaction transaction) throws SQLException {
        String query = "INSERT INTO AdminTransactions (admin_id, amount, transaction_type, description, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, transaction.getAdminId());
            stmt.setBigDecimal(2, transaction.getAmount());
            stmt.setString(3, transaction.getTransactionType());
            stmt.setString(4, transaction.getDescription());
            stmt.setString(5, transaction.getStatus());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public BigDecimal getTotalWithdrawalForDay(int adminId, LocalDate date) throws SQLException {
        String query = "SELECT SUM(amount) AS total_withdrawal FROM AdminTransactions WHERE admin_id = ? AND transaction_type = 'WITHDRAWAL' AND CONVERT(DATE, created_at) = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, adminId);
            stmt.setDate(2, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total_withdrawal");
                    return (total != null) ? total : BigDecimal.ZERO;
                }
            }
        }
        
        return BigDecimal.ZERO;
    }
}
