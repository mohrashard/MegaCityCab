package com.megacitycab.servlet;

import com.megacitycab.model.Admin;
import com.megacitycab.service.AdminService;
import org.json.JSONObject; // Ensure you have the JSON library

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/adminSignup")
public class AdminSignupServlet extends HttpServlet {

    private AdminService adminService = new AdminService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String adminID = request.getParameter("adminId");
        String adminName = request.getParameter("adminName");
        String password = request.getParameter("password");

        Admin admin = new Admin(adminID, adminName, password);

        
        boolean isRegistered = adminService.registerAdmin(admin);
     
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
       
        JSONObject jsonResponse = new JSONObject();
        if (isRegistered) {
            jsonResponse.put("message", "Admin Registration Successful!");
        } else {
            jsonResponse.put("message", "Error registering admin.");
        }

        
        out.print(jsonResponse.toString());
        out.flush(); 
    }
}
