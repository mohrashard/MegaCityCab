package com.megacitycab.servlet;

import com.megacitycab.config.DBConnection;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import javax.servlet.annotation.WebServlet;
import org.json.JSONObject;

@WebServlet("/getDriverStatus")
public class GetDriverStatusServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        HttpSession session = request.getSession(false);
        
        // Check if session exists and user is a driver
        if (session == null || !"driver".equals(session.getAttribute("userType"))) {
            json.put("success", false);
            json.put("message", "User not logged in or not a driver");
            response.getWriter().write(json.toString());
            return;
        }
        
        // Retrieve driver details from session
        Integer driverId = (Integer) session.getAttribute("userId");
        String fullName = (String) session.getAttribute("fullName");
        
        if (driverId == null) {
            json.put("success", false);
            json.put("message", "Driver ID not found in session");
            response.getWriter().write(json.toString());
            return;
        }
        
        // Fetch driver status from database
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT status FROM drivers WHERE driver_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, driverId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                json.put("success", true);
                json.put("status", rs.getString("status"));
                json.put("driverId", driverId);
                json.put("fullName", fullName);
            } else {
                json.put("success", false);
                json.put("message", "Driver not found");
            }
        } catch (Exception e) {
            json.put("success", false);
            json.put("message", "Error retrieving driver status: " + e.getMessage());
        }
        
        response.getWriter().write(json.toString());
    }
}