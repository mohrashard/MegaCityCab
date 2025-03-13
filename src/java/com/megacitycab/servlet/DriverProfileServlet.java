package com.megacitycab.servlet;

import com.megacitycab.config.DBConnection;
import com.megacitycab.util.PasswordUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;
import java.util.regex.Pattern;

@WebServlet("/driverProfile")
public class DriverProfileServlet extends HttpServlet {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[\\d\\s\\-\\(\\)]{7,15}$");
    
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$");
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        
        HttpSession session = request.getSession();
        if (session.getAttribute("userId") == null || 
            !"driver".equals(session.getAttribute("userType"))) {
            
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Access denied. Please login as a driver.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print(jsonResponse.toString());
            return;
        }
        
        int driverId = (int) session.getAttribute("userId");
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Database connection failed.");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(jsonResponse.toString());
                return;
            }
            
            String query = "SELECT driver_id, full_name, email, phone, license_no, vehicle_type, vehicle_number, status " +
                           "FROM Drivers WHERE driver_id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, driverId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        JSONObject driverData = new JSONObject();
                        driverData.put("driver_id", rs.getString("driver_id"));
                        driverData.put("full_name", rs.getString("full_name"));
                        driverData.put("email", rs.getString("email"));
                        driverData.put("phone", rs.getString("phone"));
                        driverData.put("license_no", rs.getString("license_no"));
                        driverData.put("vehicle_type", rs.getString("vehicle_type"));
                        driverData.put("vehicle_number", rs.getString("vehicle_number"));
                        driverData.put("status", rs.getString("status"));
                        
                        jsonResponse.put("success", true);
                        jsonResponse.put("data", driverData);
                    } else {
                        jsonResponse.put("success", false);
                        jsonResponse.put("message", "Driver profile not found.");
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred while retrieving profile: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        out.print(jsonResponse.toString());
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        
        HttpSession session = request.getSession();
        if (session.getAttribute("userId") == null || 
            !"driver".equals(session.getAttribute("userType"))) {
            
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Access denied. Please login as a driver.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print(jsonResponse.toString());
            return;
        }
        
        int driverId = (int) session.getAttribute("userId");
        
        String fullName = request.getParameter("full_name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String licenseNo = request.getParameter("license_no");
        String currentPassword = request.getParameter("current_password");
        String newPassword = request.getParameter("new_password");
        
        JSONObject validationErrors = new JSONObject();
        
        if (fullName == null || fullName.trim().isEmpty()) {
            validationErrors.put("full_name", "Full name is required.");
        }
        
        if (email != null && !email.trim().isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            validationErrors.put("email", "Please provide a valid email address.");
        }
        
        if (phone != null && !phone.trim().isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            validationErrors.put("phone", "Please provide a valid phone number.");
        }
        
        if (licenseNo == null || licenseNo.trim().isEmpty()) {
            validationErrors.put("license_no", "License number is required.");
        }
        
        if (newPassword != null && !newPassword.isEmpty()) {
            if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
                validationErrors.put("new_password", "Password must be at least 8 characters with one uppercase letter, one number, and one special character");
            }
            
            if (currentPassword == null || currentPassword.isEmpty()) {
                validationErrors.put("current_password", "Current password is required.");
            }
        }
        
        if (validationErrors.length() > 0) {
            jsonResponse.put("success", false);
            jsonResponse.put("errors", validationErrors);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(jsonResponse.toString());
            return;
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Database connection failed.");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(jsonResponse.toString());
                return;
            }
            
            conn.setAutoCommit(false);
            
            try {
                String updateProfileQuery = "UPDATE Drivers SET full_name = ?, email = ?, phone = ?, license_no = ? WHERE driver_id = ?";
                
                try (PreparedStatement stmt = conn.prepareStatement(updateProfileQuery)) {
                    stmt.setString(1, fullName);
                    stmt.setString(2, email);
                    stmt.setString(3, phone);
                    stmt.setString(4, licenseNo);
                    stmt.setInt(5, driverId);
                    
                    int rowsUpdated = stmt.executeUpdate();
                    
                    if (rowsUpdated == 0) {
                        throw new SQLException("Failed to update profile.");
                    }
                }
                
                if (newPassword != null && !newPassword.isEmpty()) {
                    String checkPasswordQuery = "SELECT password, salt FROM Drivers WHERE driver_id = ?";
                    
                    String storedPassword = null;
                    String storedSalt = null;
                    
                    try (PreparedStatement stmt = conn.prepareStatement(checkPasswordQuery)) {
                        stmt.setInt(1, driverId);
                        
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                storedPassword = rs.getString("password");
                                storedSalt = rs.getString("salt");
                            } else {
                                throw new SQLException("Driver not found when verifying password.");
                            }
                        }
                    }
                    
                    if (!PasswordUtil.verifyPassword(currentPassword, storedPassword, storedSalt)) {
                        throw new ServletException("Current password is incorrect.");
                    }
                    
                    String newSalt = PasswordUtil.generateSalt();
                    String newHashedPassword = PasswordUtil.hashPassword(newPassword, newSalt);
                    
                    String updatePasswordQuery = "UPDATE Drivers SET password = ?, salt = ? WHERE driver_id = ?";
                    
                    try (PreparedStatement stmt = conn.prepareStatement(updatePasswordQuery)) {
                        stmt.setString(1, newHashedPassword);
                        stmt.setString(2, newSalt);
                        stmt.setInt(3, driverId);
                        
                        int rowsUpdated = stmt.executeUpdate();
                        
                        if (rowsUpdated == 0) {
                            throw new SQLException("Failed to update password.");
                        }
                    }
                }
                
                conn.commit();
                
                session.setAttribute("fullName", fullName);
                
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Profile updated successfully.");
                
            } catch (ServletException | SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                
                jsonResponse.put("success", false);
                jsonResponse.put("message", e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred while updating profile: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        out.print(jsonResponse.toString());
    }
}