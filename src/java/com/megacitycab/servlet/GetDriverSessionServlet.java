package com.megacitycab.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import org.json.JSONObject;

@WebServlet("/getDriverSession")
public class GetDriverSessionServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                json.put("success", false);
                json.put("message", "No active session");
                response.getWriter().write(json.toString());
                return;
            }

            String userType = (String) session.getAttribute("userType");
            if (!"driver".equals(userType)) {
                json.put("success", false);
                json.put("message", "User  is not a driver");
                response.getWriter().write(json.toString());
                return;
            }

            Integer userId = (Integer) session.getAttribute("userId");
            String fullName = (String) session.getAttribute("fullName"); 
            
            // Debugging: Log the session attributes
            System.out.println("User  ID: " + userId);
            System.out.println("Full Name: " + fullName);
            
            if (userId == null) {
                json.put("success", false);
                json.put("message", "Driver not logged in");
            } else {
                json.put("success", true);
                json.put("driverId", userId);
                json.put("fullName", fullName); 
            }
        } catch (Exception e) {
            json.put("success", false);
            json.put("message", "Server error: " + e.getMessage());
        }
        
        response.getWriter().write(json.toString());
    }
}