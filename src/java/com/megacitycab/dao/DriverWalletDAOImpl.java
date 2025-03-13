package com.megacitycab.dao;

import com.megacitycab.dao.DriverWalletDAO;
import com.megacitycab.model.DriverWallet;
import com.megacitycab.config.DBConnection;

import java.sql.*;
import java.math.BigDecimal;
import java.util.Optional;

public class DriverWalletDAOImpl implements DriverWalletDAO {
    
    @Override
    public Optional<DriverWallet> findByDriverId(int driverId) {
        String sql = "SELECT * FROM driver_wallet WHERE driver_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, driverId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                DriverWallet wallet = new DriverWallet();
                wallet.setDriverId(rs.getInt("driver_id"));
                wallet.setWalletBalance(rs.getBigDecimal("wallet_balance"));
                wallet.setTotalEarnings(rs.getBigDecimal("total_earnings"));
                wallet.setTotalExpenses(rs.getBigDecimal("total_expenses"));
                return Optional.of(wallet);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public boolean save(DriverWallet wallet) {
        String sql = "INSERT INTO driver_wallet (driver_id, wallet_balance, total_earnings, total_expenses) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, wallet.getDriverId());
            stmt.setBigDecimal(2, wallet.getWalletBalance());
            stmt.setBigDecimal(3, wallet.getTotalEarnings());
            stmt.setBigDecimal(4, wallet.getTotalExpenses());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean update(DriverWallet wallet) {
        String sql = "UPDATE driver_wallet SET wallet_balance = ?, total_earnings = ?, total_expenses = ? WHERE driver_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, wallet.getWalletBalance());
            stmt.setBigDecimal(2, wallet.getTotalEarnings());
            stmt.setBigDecimal(3, wallet.getTotalExpenses());
            stmt.setInt(4, wallet.getDriverId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
   @Override
public boolean updateBalance(int driverId, BigDecimal amount, boolean isTopUp) {
    String sql = "UPDATE driver_wallet SET wallet_balance = wallet_balance + ? WHERE driver_id = ?";
    if (!isTopUp) {
        sql = "UPDATE driver_wallet SET wallet_balance = wallet_balance - ? WHERE driver_id = ?";
    }
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setBigDecimal(1, amount);
        stmt.setInt(2, driverId);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    
    
}
