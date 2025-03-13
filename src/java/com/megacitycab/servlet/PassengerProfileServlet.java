package com.megacitycab.servlet;

import com.megacitycab.config.DBConnection;
import org.json.JSONObject;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/passengerProfile")
public class PassengerProfileServlet extends HttpServlet {

    // GET method to retrieve passenger profile
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        // Check if user is logged in
        if (session == null || session.getAttribute("userId") == null || 
            !"passenger".equals(session.getAttribute("userType"))) {
            
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Authentication required.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print(jsonResponse.toString());
            return;
        }

        int passengerId = (int) session.getAttribute("userId");

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Database connection failed.");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(jsonResponse.toString());
                return;
            }

            String query = "SELECT full_name, email, phone, nic, address FROM Passengers WHERE passenger_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, passengerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JSONObject profileData = new JSONObject();
                profileData.put("full_name", rs.getString("full_name"));
                profileData.put("email", rs.getString("email"));
                profileData.put("phone", rs.getString("phone"));
                profileData.put("nic", rs.getString("nic"));
                profileData.put("address", rs.getString("address"));

                jsonResponse.put("success", true);
                jsonResponse.put("profile", profileData);
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Profile not found.");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        out.print(jsonResponse.toString());
        out.flush();
    }

    // POST method to update passenger profile
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        // Check if user is logged in
        if (session == null || session.getAttribute("userId") == null || 
            !"passenger".equals(session.getAttribute("userType"))) {
            
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Authentication required.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print(jsonResponse.toString());
            return;
        }

        int passengerId = (int) session.getAttribute("userId");
        String fullName = request.getParameter("full_name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        
        // Input validation
        boolean hasErrors = false;
        JSONObject errors = new JSONObject();
        
        // Validate full name
        if (fullName == null || fullName.trim().isEmpty()) {
            errors.put("full_name", "Full name is required.");
            hasErrors = true;
        }
        
        // Validate email
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email is required.");
            hasErrors = true;
        } else if (!isValidEmail(email)) {
            errors.put("email", "Invalid email format.");
            hasErrors = true;
        }
        
        // Validate phone (Sri Lankan format)
        if (phone == null || phone.trim().isEmpty()) {
            errors.put("phone", "Phone number is required.");
            hasErrors = true;
        } else if (!isValidSriLankanPhone(phone)) {
            errors.put("phone", "Invalid Sri Lankan phone number format.");
            hasErrors = true;
        }
        
        // Validate address
        if (address == null || address.trim().isEmpty()) {
            errors.put("address", "Address is required.");
            hasErrors = true;
        }
        
        // Validate password if provided
        if (password != null && !password.trim().isEmpty() && !isValidPassword(password)) {
            errors.put("password", "Password must be at least 8 characters and contain 1 special character.");
            hasErrors = true;
        }
        
        if (hasErrors) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Validation failed.");
            jsonResponse.put("errors", errors);
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
            
            // Check if email already exists (for another user)
            if (!isEmailAvailable(conn, email, passengerId)) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Email is already in use by another account.");
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.print(jsonResponse.toString());
                return;
            }
            
            // Handle profile update with or without password change
            boolean updateSuccess;
            if (password != null && !password.trim().isEmpty()) {
                // Update with new password
                updateSuccess = updateProfileWithPassword(conn, passengerId, fullName, email, phone, address, password);
            } else {
                // Update without changing password
                updateSuccess = updateProfileWithoutPassword(conn, passengerId, fullName, email, phone, address);
            }
            
            if (updateSuccess) {
                // Update session attributes if needed
                session.setAttribute("user", email);
                session.setAttribute("fullName", fullName);
                
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Profile updated successfully.");
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to update profile.");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        out.print(jsonResponse.toString());
        out.flush();
    }
    
    // Validation methods
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
    private boolean isValidSriLankanPhone(String phone) {
        // Sri Lankan phone number formats:
        // +94XXXXXXXXX, 0XXXXXXXXX (where X is a digit)
        String phoneRegex = "^(\\+94|0)[0-9]{9}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
    
    private boolean isValidPassword(String password) {
        // At least 8 characters and contains 1 special character
        String passwordRegex = "^(?=.*[!@#$%^&*(),.?\":{}|<>])(.{8,})$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
    
    // Database operations
    private boolean isEmailAvailable(Connection conn, String email, int currentUserId) throws SQLException {
        String query = "SELECT passenger_id FROM Passengers WHERE email = ? AND passenger_id != ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setInt(2, currentUserId);
            ResultSet rs = stmt.executeQuery();
            return !rs.next(); // Email is available if no results are found
        }
    }
    
    private boolean updateProfileWithoutPassword(Connection conn, int passengerId, String fullName, 
                                               String email, String phone, String address) throws SQLException {
        String query = "UPDATE Passengers SET full_name = ?, email = ?, phone = ?, address = ? WHERE passenger_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, fullName);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, address);
            stmt.setInt(5, passengerId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    private boolean updateProfileWithPassword(Connection conn, int passengerId, String fullName, 
                                            String email, String phone, String address, 
                                            String password) throws SQLException {
        // Generate salt and hash password
        String salt = com.megacitycab.util.PasswordUtil.generateSalt();
        String hashedPassword = com.megacitycab.util.PasswordUtil.hashPassword(password, salt);
        
        String query = "UPDATE Passengers SET full_name = ?, email = ?, phone = ?, address = ?, password = ?, salt = ? WHERE passenger_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, fullName);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, address);
            stmt.setString(5, hashedPassword);
            stmt.setString(6, salt);
            stmt.setInt(7, passengerId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}