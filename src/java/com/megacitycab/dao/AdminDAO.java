package com.megacitycab.dao;

import com.megacitycab.config.DBConnection;
import com.megacitycab.model.Admin;
import com.megacitycab.util.PasswordUtil;

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

            String hashedPassword = PasswordUtil.hashPassword(admin.getPassword());
            pstmt.setString(3, hashedPassword);

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

    @Override
public boolean validateAdmin(String username, String password) {
    String sql = "SELECT password FROM Admins WHERE username = ?";
    boolean isValid = false;
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            String storedHashedPassword = rs.getString("password");
            String hashedInputPassword = PasswordUtil.hashPassword(password);
            System.out.println("Stored hashed password: " + storedHashedPassword);
            System.out.println("Input hashed password: " + hashedInputPassword);
            isValid = storedHashedPassword.equals(hashedInputPassword);
        } else {
            System.out.println("No admin found with username: " + username);
        }
    } catch (SQLException e) {
        System.out.println("Error validating admin: " + e.getMessage());
    }
    return isValid;
}


}
