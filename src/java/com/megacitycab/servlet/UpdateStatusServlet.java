package com.megacitycab.servlet;

import com.megacitycab.config.DBConnection;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import javax.servlet.annotation.WebServlet;
import org.json.JSONObject;

@WebServlet("/updateStatus")
public class UpdateStatusServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        
        try (BufferedReader reader = request.getReader()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            
            JSONObject reqData = new JSONObject(sb.toString());
            int driverId = reqData.getInt("driverId");
            String status = reqData.getString("status");

            try (Connection conn = DBConnection.getConnection()) {
                String query = "UPDATE drivers SET status = ? WHERE driver_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, status);
                pstmt.setInt(2, driverId);
                
                int updated = pstmt.executeUpdate();
                if (updated > 0) {
                    json.put("success", true);
                    json.put("message", "Status updated to " + status);
                } else {
                    json.put("success", false);
                    json.put("message", "No driver found with ID: " + driverId);
                }
            }
        } catch (Exception e) {
            json.put("success", false);
            json.put("message", "Error: " + e.getMessage());
        }
        
        response.getWriter().write(json.toString());
    }
}