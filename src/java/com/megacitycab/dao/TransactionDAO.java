package com.megacitycab.dao;

import com.megacitycab.config.DBConnection;
import com.megacitycab.model.Transaction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public void addTransaction(Transaction transaction) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(true);
            String sql = "INSERT INTO Transactions (passenger_id, amount, transaction_type, description, date_time) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, transaction.getPassengerId());
            stmt.setBigDecimal(2, transaction.getAmount());
            stmt.setString(3, transaction.getTransactionType());
            stmt.setString(4, transaction.getDescription());
            stmt.setTimestamp(5, Timestamp.valueOf(transaction.getDateTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
            throw e;
        }
    }

    public List<Transaction> getTransactionsByPassengerId(int passengerId) {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Transactions WHERE passenger_id = ? ORDER BY date_time DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, passengerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getInt("transaction_id"));
                transaction.setPassengerId(rs.getInt("passenger_id"));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setTransactionType(rs.getString("transaction_type"));
                transaction.setDescription(rs.getString("description"));
                transaction.setDateTime(rs.getTimestamp("date_time").toLocalDateTime());
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error fetching transactions: " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }
    
    public void addTransaction(Connection conn, Transaction transaction) throws SQLException {
        String sql = "INSERT INTO Transactions (passenger_id, amount, transaction_type, description, date_time) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transaction.getPassengerId());
            stmt.setBigDecimal(2, transaction.getAmount());
            stmt.setString(3, transaction.getTransactionType());
            stmt.setString(4, transaction.getDescription());
            stmt.setTimestamp(5, Timestamp.valueOf(transaction.getDateTime()));
            stmt.executeUpdate();
        }
    }
}