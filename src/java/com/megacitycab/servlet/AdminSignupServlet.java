package com.megacitycab.servlet;

import com.megacitycab.model.Admin;
import com.megacitycab.service.AdminService;
import org.json.JSONObject;
import com.megacitycab.util.PasswordUtil;

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
       

        JSONObject jsonResponse = new JSONObject();

       

        Admin admin = new Admin(username, adminName);

        boolean isRegistered = adminService.registerAdmin(admin, password);


        if (isRegistered) {
            jsonResponse.put("message", "Admin Registration Successful!");
        } else {
            jsonResponse.put("message", "Error registering admin.");
        }

        sendResponse(response, jsonResponse);
    }

    private void sendResponse(HttpServletResponse response, JSONObject jsonResponse) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }
}
