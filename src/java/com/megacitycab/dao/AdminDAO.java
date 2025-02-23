package com.megacitycab.dao;

import com.megacitycab.config.DBConnection;
import com.megacitycab.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO implements AdminDAOInterface {

    @Override
    public void saveAdmin(Admin admin) {
        String sql = "INSERT INTO Admins (username, admin_name, password) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, admin.getUsername());
            pstmt.setString(2, admin.getAdminName());
            pstmt.setString(3, admin.getPassword());  // Hash the password here
            
            pstmt.executeUpdate();
            System.out.println("Admin saved successfully!");
        } catch (SQLException e) {
            System.out.println("Error saving admin: " + e.getMessage());
        }
    }

    public Admin getAdminID(int adminId) {
        String sql = "SELECT * FROM Admins WHERE adminId = ?";
        Admin admin = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, adminId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                admin = new Admin(
                    rs.getInt("adminId"),
                    rs.getString("username"),
                    rs.getString("admin_name"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving admin: " + e.getMessage());
        }

        return admin;
    }

    @Override
    public Admin getAdminByUsername(String username) {
        String sql = "SELECT * FROM Admins WHERE username = ?";
        Admin admin = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                admin = new Admin(
                    rs.getInt("adminId"),
                    rs.getString("username"),
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
