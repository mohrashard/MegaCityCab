package com.megacitycab.servlet;

import com.megacitycab.model.Admin;
import com.megacitycab.service.AdminService;
import org.json.JSONObject;

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

        Admin admin = new Admin(username, adminName, password);
        boolean isRegistered = adminService.registerAdmin(admin);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("message", isRegistered ? "Admin Registration Successful!" : "Error registering admin.");

        out.print(jsonResponse.toString());
        out.flush();
    }
}
