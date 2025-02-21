package com.megacitycab.dao;

import com.megacitycab.config.DBConnection;
import com.megacitycab.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

<<<<<<< HEAD
public class AdminDAO implements AdminDAOInterface {
    
    @Override
    public void saveAdmin(Admin admin) {
        String sql = "INSERT INTO Admins (username, admin_name, password) VALUES (?, ?, ?)";
=======
public class AdminDAO {

    public void saveAdmin(Admin admin) {
        String sql = "INSERT INTO Admins (adminID, admin_name, password) VALUES (?, ?, ?)";
>>>>>>> 72ae542dfd6364ed1a2fd6a2eb44d3e556980607

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

<<<<<<< HEAD
            pstmt.setString(1, admin.getUsername());
            pstmt.setString(2, admin.getAdminName());
            pstmt.setString(3, admin.getPassword());  // Store hashed password (recommended)
            
            pstmt.executeUpdate();
=======
            pstmt.setString(1, admin.getAdminID());
            pstmt.setString(2, admin.getAdminName());
            pstmt.setString(3, admin.getPassword());
            pstmt.executeUpdate();

>>>>>>> 72ae542dfd6364ed1a2fd6a2eb44d3e556980607
            System.out.println("Admin saved successfully!");
        } catch (SQLException e) {
            System.out.println("Error saving admin: " + e.getMessage());
        }
    }

<<<<<<< HEAD
    @Override
    public Admin getAdminByUsername(String username) {
        String sql = "SELECT adminId, username, admin_name, password FROM Admins WHERE username = ?";
=======
    public Admin getAdminByID(String adminID) { // Change to String
        String sql = "SELECT * FROM Admins WHERE adminID = ?";
>>>>>>> 72ae542dfd6364ed1a2fd6a2eb44d3e556980607
        Admin admin = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

<<<<<<< HEAD
            pstmt.setString(1, username);
=======
            pstmt.setString(1, adminID); // Set adminID as String
>>>>>>> 72ae542dfd6364ed1a2fd6a2eb44d3e556980607
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                admin = new Admin(
<<<<<<< HEAD
                    rs.getInt("adminId"),
                    rs.getString("username"),
=======
                    rs.getString("adminID"), // Change to getString
>>>>>>> 72ae542dfd6364ed1a2fd6a2eb44d3e556980607
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
