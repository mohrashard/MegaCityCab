package com.megacitycab.dao;

import com.megacitycab.config.DBConnection;
import com.megacitycab.model.Card;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CardDAO {

    public void addCard(Card card) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO Cards (passenger_id, card_number, cardholder_name, expiry_date, cvc) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, card.getPassengerId());
            stmt.setString(2, card.getCardNumber());
            stmt.setString(3, card.getCardholderName());
            stmt.setString(4, card.getExpiryDate());
            stmt.setString(5, card.getCvc());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Card> getCardsByPassengerId(int passengerId) {
        List<Card> cards = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Cards WHERE passenger_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, passengerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Card card = new Card();
                card.setCardId(rs.getInt("card_id"));
                card.setPassengerId(rs.getInt("passenger_id"));
                card.setCardNumber(rs.getString("card_number"));
                card.setCardholderName(rs.getString("cardholder_name"));
                card.setExpiryDate(rs.getString("expiry_date"));
                card.setCvc(rs.getString("cvc"));
                cards.add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }

public void updateCard(Card card) {
    try (Connection conn = DBConnection.getConnection()) {
        String sql = "UPDATE Cards SET cardholder_name = ?, expiry_date = ? WHERE card_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, card.getCardholderName());
            stmt.setString(2, card.getExpiryDate());
            stmt.setInt(3, card.getCardId());
            stmt.executeUpdate();
        }catch (SQLException e) {
        throw new RuntimeException("Database error updating card", e);
    }
    }

    public void deleteCard(int cardId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM Cards WHERE card_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cardId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    public boolean checkCardOwner(int cardId, int passengerId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT passenger_id FROM Cards WHERE card_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cardId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int ownerId = rs.getInt("passenger_id");
                return ownerId == passengerId;  
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  
    }

}