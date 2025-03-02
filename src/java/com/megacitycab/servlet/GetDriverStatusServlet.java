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
        
        if (session == null || session.getAttribute("fullName") == null) {
            json.put("success", false);
            json.put("message", "User not logged in");
        } else {
            String fullName = (String) session.getAttribute("fullName");
            
            try {
                int driverId = Integer.parseInt(request.getParameter("driverId"));
                
                try (Connection conn = DBConnection.getConnection()) {
                    String query = "SELECT status FROM drivers WHERE driver_id = ?";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, driverId);
                    
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        json.put("success", true);
                        json.put("status", rs.getString("status"));
                        json.put("fullName", fullName);
                    } else {
                        json.put("success", false);
                        json.put("message", "Driver not found");
                    }
                }
            } catch (NumberFormatException e) {
                json.put("success", false);
                json.put("message", "Invalid driver ID format");
            } catch (SQLException e) {
                json.put("success", false);
                json.put("message", "Database error: " + e.getMessage());
            } catch (Exception e) {
                json.put("success", false);
                json.put("message", "Server error: " + e.getMessage());
            }
        }
        
        response.getWriter().write(json.toString());
    }
}
