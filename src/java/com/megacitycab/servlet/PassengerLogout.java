package com.megacitycab.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import org.json.JSONObject;
import java.io.PrintWriter;

@WebServlet("/PassengerLogout")
public class PassengerLogout extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            String userType = (String) session.getAttribute("userType");
            

            session.invalidate();

            response.sendRedirect("login.html");
        } else {
            response.sendRedirect("login.html");
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        
        if (session != null) {
            session.invalidate();
            
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Logout successful");
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "No active session found");
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
}