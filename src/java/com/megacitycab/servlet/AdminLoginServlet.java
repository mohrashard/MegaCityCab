package com.megacitycab.servlet;

import com.megacitycab.controller.LoginController;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;

@WebServlet("/adminLogin")
public class AdminLoginServlet extends HttpServlet {
    private LoginController loginController;
    
    @Override
    public void init() throws ServletException {
        System.out.println("AdminLoginServlet initialized");
        loginController = new LoginController();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // Debug logs
        System.out.println("Login attempt - Username: " + username);
        
        JSONObject jsonResponse = new JSONObject();
        try {
            // Add debug logs around the login attempt
            System.out.println("Attempting login validation...");
            boolean loginSuccess = loginController.login(username, password);
            System.out.println("Login validation result: " + loginSuccess);
            
            if (loginSuccess) {
                jsonResponse.put("success", true);
                jsonResponse.put("redirectUrl", "adminDashboard.html");
                System.out.println("Login successful - redirecting to dashboard");
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("errorMessage", "Invalid username or password.");
                System.out.println("Login failed - invalid credentials");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("success", false);
            jsonResponse.put("errorMessage", "An internal error occurred: " + e.getMessage());
            System.out.println("Login error: " + e.getMessage());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }
}