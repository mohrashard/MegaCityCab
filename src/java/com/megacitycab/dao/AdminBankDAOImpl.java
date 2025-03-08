package com.megacitycab.dao;

import com.megacitycab.config.DBConnection;
import com.megacitycab.model.AdminBank;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminBankDAOImpl implements AdminBankDAO {

    @Override
    public List<AdminBank> getAllBanksByAdminId(int adminId) throws SQLException {
        List<AdminBank> banks = new ArrayList<>();
        String query = "SELECT * FROM AdminBanks WHERE admin_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, adminId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AdminBank bank = new AdminBank();
                    bank.setBankId(rs.getInt("bank_id"));
                    bank.setAdminId(rs.getInt("admin_id"));
                    bank.setBankName(rs.getString("bank_name"));
                    bank.setAccountNumber(rs.getString("account_number"));
                    bank.setAccountHolder(rs.getString("account_holder"));
                    bank.setBranch(rs.getString("branch"));
                    bank.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    bank.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    banks.add(bank);
                }
            }
        }
        
        return banks;
    }

    @Override
    public AdminBank getBankById(int bankId) throws SQLException {
        AdminBank bank = null;
        String query = "SELECT * FROM AdminBanks WHERE bank_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bankId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    bank = new AdminBank();
                    bank.setBankId(rs.getInt("bank_id"));
                    bank.setAdminId(rs.getInt("admin_id"));
                    bank.setBankName(rs.getString("bank_name"));
                    bank.setAccountNumber(rs.getString("account_number"));
                    bank.setAccountHolder(rs.getString("account_holder"));
                    bank.setBranch(rs.getString("branch"));
                    bank.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    bank.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
            }
        }
        
        return bank;
    }

    @Override
    public boolean addBank(AdminBank bank) throws SQLException {
        String query = "INSERT INTO AdminBanks (admin_id, bank_name, account_number, account_holder, branch) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bank.getAdminId());
            stmt.setString(2, bank.getBankName());
            stmt.setString(3, bank.getAccountNumber());
            stmt.setString(4, bank.getAccountHolder());
            stmt.setString(5, bank.getBranch());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean updateBank(AdminBank bank) throws SQLException {
        String query = "UPDATE AdminBanks SET bank_name = ?, account_number = ?, account_holder = ?, branch = ?, updated_at = ? WHERE bank_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bank.getBankName());
            stmt.setString(2, bank.getAccountNumber());
            stmt.setString(3, bank.getAccountHolder());
            stmt.setString(4, bank.getBranch());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(6, bank.getBankId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean deleteBank(int bankId) throws SQLException {
        String query = "DELETE FROM AdminBanks WHERE bank_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bankId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public int getBankCountByAdminId(int adminId) throws SQLException {
        String query = "SELECT COUNT(*) AS bank_count FROM AdminBanks WHERE admin_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, adminId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("bank_count");
                }
            }
        }
        
        return 0;
    }
}
