package com.megacitycab.dao;

import com.megacitycab.model.DriverBank;
import com.megacitycab.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DriverBankDAOImpl implements DriverBankDAO {
    
    @Override
    public boolean save(DriverBank bank) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            

            if (countByDriverId(bank.getDriverId()) >= 3) {
                conn.rollback();
                return false;
            }
            

            if (bank.isDefault()) {
                String resetSql = "UPDATE driver_banks SET is_default = 0 WHERE driver_id = ?";
                PreparedStatement resetStmt = conn.prepareStatement(resetSql);
                resetStmt.setInt(1, bank.getDriverId());
                resetStmt.executeUpdate();
            }
            
            String sql = "INSERT INTO driver_banks (driver_id, bank_name, account_number, account_holder_name, ifsc_code, is_default) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bank.getDriverId());
            stmt.setString(2, bank.getBankName());
            stmt.setString(3, bank.getAccountNumber());
            stmt.setString(4, bank.getAccountHolderName());
            stmt.setString(5, bank.getIfscCode());
            stmt.setBoolean(6, bank.isDefault());
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
@Override
public boolean update(DriverBank bank) {
    Connection conn = null;
    PreparedStatement resetStmt = null;
    PreparedStatement stmt = null;
    
    try {
        conn = DBConnection.getConnection();
        conn.setAutoCommit(false);
        
        System.out.println("Updating bank in DAO - Bank ID: " + bank.getBankId());
        System.out.println("Is Default: " + bank.isDefault());
        
        // If this bank is being set as default, reset all other banks' default status
        if (bank.isDefault()) {
            String resetSql = "UPDATE driver_banks SET is_default = 0 WHERE driver_id = ? AND bank_id != ?";
            resetStmt = conn.prepareStatement(resetSql);
            resetStmt.setInt(1, bank.getDriverId());
            resetStmt.setInt(2, bank.getBankId());
            resetStmt.executeUpdate();
            System.out.println("Reset other banks default status for driver ID: " + bank.getDriverId());
        }
        
        // Modified SQL query to add debugging and ensure proper column names
        String sql = "UPDATE driver_banks SET bank_name = ?, account_number = ?, account_holder_name = ?, " + 
                     "ifsc_code = ?, is_default = ? WHERE bank_id = ?";
        stmt = conn.prepareStatement(sql);
        stmt.setString(1, bank.getBankName());
        stmt.setString(2, bank.getAccountNumber());
        stmt.setString(3, bank.getAccountHolderName());
        stmt.setString(4, bank.getIfscCode());
        
        // Explicitly convert boolean to integer (1 or 0) for SQL compatibility
        int isDefaultInt = bank.isDefault() ? 1 : 0;
        stmt.setInt(5, isDefaultInt);
        stmt.setInt(6, bank.getBankId());
        
        System.out.println("Executing SQL: " + sql);
        System.out.println("With values: " + bank.getBankName() + ", " + bank.getAccountNumber() + ", " + 
                           bank.getAccountHolderName() + ", " + bank.getIfscCode() + ", " + 
                           isDefaultInt + ", " + bank.getBankId());
        
        int result = stmt.executeUpdate();
        
        // Critical debugging
        System.out.println("Update bank result: " + result + " for bank ID: " + bank.getBankId());
        
        if (result > 0) {
            conn.commit();
            System.out.println("Database committed successfully!");
            return true;
        } else {
            conn.rollback();
            System.out.println("No rows affected, rolling back transaction");
            return false;
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("SQL Exception: " + e.getMessage());
        try {
            if (conn != null) {
                conn.rollback();
                System.out.println("Transaction rolled back due to exception");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    } finally {
        try {
            if (resetStmt != null) {
                resetStmt.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
                System.out.println("Connection closed and auto-commit reset");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
    
    @Override
    public boolean delete(int bankId) {
        String sql = "DELETE FROM driver_banks WHERE bank_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bankId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Optional<DriverBank> findById(int bankId) {
        String sql = "SELECT * FROM driver_banks WHERE bank_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bankId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToBank(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<DriverBank> findByDriverId(int driverId) {
        String sql = "SELECT * FROM driver_banks WHERE driver_id = ? ORDER BY is_default DESC";
        List<DriverBank> banks = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, driverId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                banks.add(mapResultSetToBank(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return banks;
    }
    
    @Override
    public int countByDriverId(int driverId) {
        String sql = "SELECT COUNT(*) FROM driver_banks WHERE driver_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, driverId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public boolean setDefaultBank(int driverId, int bankId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            String resetSql = "UPDATE driver_banks SET is_default = 0 WHERE driver_id = ?";
            PreparedStatement resetStmt = conn.prepareStatement(resetSql);
            resetStmt.setInt(1, driverId);
            resetStmt.executeUpdate();
            

            String sql = "UPDATE driver_banks SET is_default = 1 WHERE bank_id = ? AND driver_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bankId);
            stmt.setInt(2, driverId);
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private DriverBank mapResultSetToBank(ResultSet rs) throws SQLException {
        DriverBank bank = new DriverBank();
        bank.setBankId(rs.getInt("bank_id"));
        bank.setDriverId(rs.getInt("driver_id"));
        bank.setBankName(rs.getString("bank_name"));
        bank.setAccountNumber(rs.getString("account_number"));
        bank.setAccountHolderName(rs.getString("account_holder_name"));
        bank.setIfscCode(rs.getString("ifsc_code"));
        bank.setDefault(rs.getBoolean("is_default"));
        return bank;
    }
}