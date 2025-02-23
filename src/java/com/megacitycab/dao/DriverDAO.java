package com.megacitycab.dao;

import com.megacitycab.config.DBConnection;
import com.megacitycab.model.Driver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DriverDAO {

    public void saveDriver(Driver driver) {
        String sql = "INSERT INTO Drivers (full_name, email, phone, password, license_no, vehicle_type) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, driver.getFullName());
            pstmt.setString(2, driver.getEmail());
            pstmt.setString(3, driver.getPhone());
            pstmt.setString(4, driver.getPassword());
            pstmt.setString(5, driver.getLicenseNo());
            pstmt.setString(6, driver.getVehicleType());
            
            pstmt.executeUpdate();

            System.out.println("Driver saved successfully!");
        } catch (SQLException e) {
            System.out.println("Error saving driver: " + e.getMessage());
        }
    }

    public Driver getDriverByEmail(String email) {
        String sql = "SELECT * FROM Drivers WHERE email = ?";
        Driver driver = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                driver = new Driver(
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("password"),
                    rs.getString("license_no"),
                    rs.getString("vehicle_type")
                   
                );
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving driver: " + e.getMessage());
        }

        return driver;
    }
}
