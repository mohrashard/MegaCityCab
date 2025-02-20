package com.megacitycab.dao;

import com.megacitycab.config.DBConnection;
import com.megacitycab.model.Passenger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PassengerDAO {

    public void savePassenger(Passenger passenger) {
        String sql = "INSERT INTO Passengers (full_name, email, phone, password, nic, address) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, passenger.getFullName());
            pstmt.setString(2, passenger.getEmail());
            pstmt.setString(3, passenger.getPhone());
            pstmt.setString(4, passenger.getPassword());
            pstmt.setString(5, passenger.getNic());
            pstmt.setString(6, passenger.getAddress());
            pstmt.executeUpdate();

            System.out.println("Passenger saved successfully!");
        } catch (SQLException e) {
            System.out.println("Error saving passenger: " + e.getMessage());
        }
    }

    public Passenger getPassengerByEmail(String email) {
        String sql = "SELECT * FROM Passengers WHERE email = ?";
        Passenger passenger = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                passenger = new Passenger(
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("password"),
                    rs.getString("nic"),
                    rs.getString("address")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving passenger: " + e.getMessage());
        }

        return passenger;
    }
}
