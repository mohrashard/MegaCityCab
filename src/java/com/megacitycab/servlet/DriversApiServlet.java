package com.megacitycab.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.megacitycab.config.DBConnection;

@WebServlet(urlPatterns = {"/api/drivers"}, loadOnStartup = 1)
public class DriversApiServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        String vehicleType = request.getParameter("type");
        
        try {
        
String sql;

if (vehicleType != null && !vehicleType.isEmpty()) {
    sql = "SELECT * FROM [megacitycab].[dbo].[Drivers] WHERE [vehicle_type] = ? AND [status] = 'offline'";
} else {
    sql = "SELECT * FROM [megacitycab].[dbo].[Drivers] WHERE [status] = 'offline'";
}
            
            StringBuilder jsonResponse = new StringBuilder("[");
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                if (vehicleType != null && !vehicleType.isEmpty()) {
                    stmt.setString(1, vehicleType);
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    boolean first = true;
                    
                    while (rs.next()) {
                        if (!first) {
                            jsonResponse.append(",");
                        }
                        

jsonResponse.append("{")
    .append("\"driverId\":").append(rs.getInt("driver_id")).append(",")
    .append("\"fullName\":\"").append(escapeJson(rs.getString("full_name"))).append("\",")
    .append("\"vehicleType\":\"").append(escapeJson(rs.getString("vehicle_type"))).append("\",")
    .append("\"vehicleNumber\":\"").append(escapeJson(rs.getString("vehicle_number"))).append("\",")
    .append("\"status\":\"").append(escapeJson(rs.getString("status"))).append("\"")
    .append("}");
                        
                        first = false;
                    }
                }
            }
            
            jsonResponse.append("]");
            response.getWriter().write(jsonResponse.toString());
            
        } catch (SQLException e) {
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            e.printStackTrace();
        }
    }
    
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}