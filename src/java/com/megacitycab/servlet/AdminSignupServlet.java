package com.megacitycab.servlet;

import com.megacitycab.model.Admin;
import com.megacitycab.service.AdminService;

import org.json.JSONObject;

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
        String username = request.getParameter("username");
        String adminName = request.getParameter("adminName");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        JSONObject jsonResponse = new JSONObject();
        if (!password.equals(confirmPassword)) {
            jsonResponse.put("message", "Passwords do not match.");
        } else if (adminService.getAdminByUsername(username) != null) {
            jsonResponse.put("message", "Username already exists.");
        } else {
            Admin admin = new Admin(username, adminName, password);
            boolean isRegistered = adminService.registerAdmin(admin);
            jsonResponse.put("message", isRegistered ? "Admin Registration Successful!" : "Error registering admin.");
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }
}


