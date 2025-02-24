package com.megacitycab.servlet;

import com.megacitycab.config.DBConnection;
import com.megacitycab.util.PasswordUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.JSONObject;

@WebServlet("/adminLogin")
public class AdminLoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Database connection failed.");
                out.print(jsonResponse.toString());
                return;
            }

  
            String query = "SELECT password, salt FROM Admins WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                String storedSalt = rs.getString("salt");

        
                if (PasswordUtil.verifyPassword(password, storedHashedPassword, storedSalt)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("admin", username);
                    
                    jsonResponse.put("success", true);
                    jsonResponse.put("message", "Login successful!");
                    jsonResponse.put("redirectUrl", "adminDashboard.html");
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Invalid username or password.");
                }
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid username or password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred during login.");
        }

        out.print(jsonResponse.toString());
        out.flush();
    }
}
