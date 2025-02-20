package com.megacitycab.dao;

import com.megacitycab.config.DBConnection;
import com.megacitycab.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    public void saveAdmin(Admin admin) {
        String sql = "INSERT INTO Admins (adminID, admin_name, password) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, admin.getAdminID());
            pstmt.setString(2, admin.getAdminName());
            pstmt.setString(3, admin.getPassword());
            pstmt.executeUpdate();

            System.out.println("Admin saved successfully!");
        } catch (SQLException e) {
            System.out.println("Error saving admin: " + e.getMessage());
        }
    }

    public Admin getAdminByID(String adminID) { // Change to String
        String sql = "SELECT * FROM Admins WHERE adminID = ?";
        Admin admin = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, adminID); // Set adminID as String
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                admin = new Admin(
                    rs.getString("adminID"), // Change to getString
                    rs.getString("admin_name"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving admin: " + e.getMessage());
        }

        return admin;
    }
}
