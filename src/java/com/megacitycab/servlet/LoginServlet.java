package com.megacitycab.servlet;

import com.megacitycab.config.DBConnection;
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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String userType = request.getParameter("user-type");

        String tableName = userType.equals("driver") ? "Drivers" : "Passengers"; 

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Database connection failed.");
                out.print(jsonResponse.toString());
                return;
            }

            // Use parameterized queries to avoid SQL injection
            String query = "SELECT * FROM " + tableName + " WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Assuming you have a hashed password stored in the database
                String storedPassword = rs.getString("password");
                
                // Compare the provided password with the stored hashed password
                if (password.equals(storedPassword)) { // Replace with hash comparison if using hashed passwords
                    HttpSession session = request.getSession();
                    session.setAttribute("user", email);
                    session.setAttribute("userType", userType);

                    jsonResponse.put("success", true);
                    jsonResponse.put("userType", userType);
                    jsonResponse.put("message", "Login successful!");
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Invalid email or password.");
                }
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid email or password.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred: " + e.getMessage());
        }

        out.print(jsonResponse.toString());
        out.flush();
    }
}
