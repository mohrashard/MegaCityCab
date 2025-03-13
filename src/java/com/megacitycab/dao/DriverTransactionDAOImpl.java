
package com.megacitycab.dao;
import com.megacitycab.model.DriverTransaction;
import com.megacitycab.config.DBConnection;
import com.megacitycab.dao.DriverTransactionDAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DriverTransactionDAOImpl implements DriverTransactionDAO {
    
    @Override
    public boolean save(DriverTransaction transaction) {
        String sql = "INSERT INTO driver_transaction (driver_id, transaction_type, amount, description, date_time) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, transaction.getDriverId());
            stmt.setString(2, transaction.getTransactionType());
            stmt.setBigDecimal(3, transaction.getAmount());
            stmt.setString(4, transaction.getDescription());
            stmt.setTimestamp(5, Timestamp.valueOf(transaction.getDateTime()));
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Optional<DriverTransaction> findById(int transactionId) {
        String sql = "SELECT * FROM driver_transaction WHERE transaction_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToTransaction(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<DriverTransaction> findByDriverId(int driverId) {
        String sql = "SELECT * FROM driver_transaction WHERE driver_id = ? ORDER BY date_time DESC";
        List<DriverTransaction> transactions = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, driverId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    @Override
    public List<DriverTransaction> findByDriverIdAndFilters(int driverId, String type, Date startDate, Date endDate) {
        StringBuilder sql = new StringBuilder("SELECT * FROM driver_transaction WHERE driver_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(driverId);
        
        if (type != null && !type.isEmpty()) {
            sql.append(" AND transaction_type = ?");
            params.add(type);
        }
        
        if (startDate != null) {
            sql.append(" AND date_time >= ?");
            params.add(startDate);
        }
        
        if (endDate != null) {
            sql.append(" AND date_time <= ?");
            params.add(endDate);
        }
        
        sql.append(" ORDER BY date_time DESC");
        
        List<DriverTransaction> transactions = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else if (param instanceof Date) {
                    stmt.setDate(i + 1, (Date) param);
                }
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    private DriverTransaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        DriverTransaction transaction = new DriverTransaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setDriverId(rs.getInt("driver_id"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setDescription(rs.getString("description"));
        transaction.setDateTime(rs.getTimestamp("date_time").toLocalDateTime());
        return transaction;
    }
}
