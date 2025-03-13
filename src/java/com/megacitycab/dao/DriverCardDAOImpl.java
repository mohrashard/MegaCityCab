package com.megacitycab.dao;

import com.megacitycab.model.DriverCard;
import com.megacitycab.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DriverCardDAOImpl implements DriverCardDAO {
    
    @Override
    public boolean save(DriverCard card) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check card count first
            if (countByDriverId(card.getDriverId()) >= 3) {
                conn.rollback();
                return false;
            }
            
            // If this is set as default, reset other defaults
            if (card.isDefault()) {
                String resetSql = "UPDATE driver_cards SET is_default = 0 WHERE driver_id = ?";
                PreparedStatement resetStmt = conn.prepareStatement(resetSql);
                resetStmt.setInt(1, card.getDriverId());
                resetStmt.executeUpdate();
            }
            
            // Insert the new card
String sql = "INSERT INTO driver_cards (driver_id, card_number, expiry_date, cvv, cardholder_name, is_default) VALUES (?, ?, ?, ?, ?, ?)";
PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
stmt.setInt(1, card.getDriverId());
stmt.setString(2, card.getCardNumber());
stmt.setString(3, card.getExpiryDate());
stmt.setString(4, card.getCvv());
stmt.setString(5, card.getCardholderName());
stmt.setBoolean(6, card.isDefault());

int result = stmt.executeUpdate();
if (result > 0) {
    // Get the generated card ID
    ResultSet generatedKeys = stmt.getGeneratedKeys();
    if (generatedKeys.next()) {
        card.setCardId(generatedKeys.getInt(1));
    }
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
    public boolean update(DriverCard card) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // If this is set as default, reset other defaults
            if (card.isDefault()) {
                String resetSql = "UPDATE driver_cards SET is_default = 0 WHERE driver_id = ?";
                PreparedStatement resetStmt = conn.prepareStatement(resetSql);
                resetStmt.setInt(1, card.getDriverId());
                resetStmt.executeUpdate();
            }
            
            // Update the card
            String sql = "UPDATE driver_cards SET expiry_date = ?, cvv = ?, cardholder_name = ?, is_default = ? WHERE card_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, card.getExpiryDate());
            stmt.setString(2, card.getCvv());
            stmt.setString(3, card.getCardholderName());
            stmt.setBoolean(4, card.isDefault());
            stmt.setInt(5, card.getCardId());
            
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
    public boolean delete(int cardId) {
        String sql = "DELETE FROM driver_cards WHERE card_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cardId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Optional<DriverCard> findById(int cardId) {
        String sql = "SELECT * FROM driver_cards WHERE card_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cardId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToCard(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<DriverCard> findByDriverId(int driverId) {
        String sql = "SELECT * FROM driver_cards WHERE driver_id = ? ORDER BY is_default DESC";
        List<DriverCard> cards = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, driverId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                cards.add(mapResultSetToCard(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cards;
    }
    
    @Override
    public int countByDriverId(int driverId) {
        String sql = "SELECT COUNT(*) FROM driver_cards WHERE driver_id = ?";
        
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
    public boolean setDefaultCard(int driverId, int cardId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // First reset all cards to non-default
            String resetSql = "UPDATE driver_cards SET is_default = 0 WHERE driver_id = ?";
            PreparedStatement resetStmt = conn.prepareStatement(resetSql);
            resetStmt.setInt(1, driverId);
            resetStmt.executeUpdate();
            
            // Then set the specified card as default
            String sql = "UPDATE driver_cards SET is_default = 1 WHERE card_id = ? AND driver_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cardId);
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
    
    private DriverCard mapResultSetToCard(ResultSet rs) throws SQLException {
        DriverCard card = new DriverCard();
        card.setCardId(rs.getInt("card_id"));
        card.setDriverId(rs.getInt("driver_id"));
        card.setCardNumber(rs.getString("card_number"));
        card.setExpiryDate(rs.getString("expiry_date"));
        card.setCvv(rs.getString("cvv"));
        card.setCardholderName(rs.getString("cardholder_name"));
        card.setDefault(rs.getBoolean("is_default"));
        return card;
    }
}