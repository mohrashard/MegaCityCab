package com.megacitycab.dao;

import com.megacitycab.config.DBConnection;
import com.megacitycab.model.Wallet;
import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WalletDAO {

    public Wallet getWalletByPassengerId(int passengerId) {
        Wallet wallet = null;
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Wallet WHERE passenger_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, passengerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                wallet = new Wallet();
                wallet.setPassengerId(rs.getInt("passenger_id"));
                wallet.setBalance(rs.getBigDecimal("balance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wallet;
    }

    public void updateWalletBalance(int passengerId, BigDecimal amount) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE Wallet SET balance = balance + ? WHERE passenger_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBigDecimal(1, amount);
            stmt.setInt(2, passengerId);
            stmt.executeUpdate();
        }
    }
    
    public boolean createWallet(int passengerId, BigDecimal initialBalance) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO Wallet (passenger_id, balance) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, passengerId);
            stmt.setBigDecimal(2, initialBalance);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public BigDecimal getWalletBalance(int passengerId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT balance FROM Wallet WHERE passenger_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, passengerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("balance");
            } else {
                createWallet(passengerId, BigDecimal.ZERO);
                return BigDecimal.ZERO;
            }
        }
    }

    public void updateWalletBalance(Connection conn, int passengerId, BigDecimal amount) throws SQLException {
        String sql = "UPDATE Wallet SET balance = balance + ? WHERE passenger_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, amount);
            stmt.setInt(2, passengerId);
            stmt.executeUpdate();
        }
    }

    public boolean createWallet(Connection conn, int passengerId, BigDecimal initialBalance) throws SQLException {
        String sql = "INSERT INTO Wallet (passenger_id, balance) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, passengerId);
            stmt.setBigDecimal(2, initialBalance);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }


    public BigDecimal getWalletBalance(Connection conn, int passengerId) throws SQLException {
        String sql = "SELECT balance FROM Wallet WHERE passenger_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, passengerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("balance");
            } else {
                createWallet(conn, passengerId, BigDecimal.ZERO);
                return BigDecimal.ZERO;
            }
        }
    }
}