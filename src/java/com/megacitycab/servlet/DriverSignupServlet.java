package com.megacitycab.servlet;

import com.megacitycab.model.Driver;
import com.megacitycab.service.DriverService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;

@WebServlet("/driverSignup")
public class DriverSignupServlet extends HttpServlet {

    private DriverService driverService = new DriverService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String licenseNo = request.getParameter("nic");
        String vehicleType = request.getParameter("vehicle-type");
        String vehicleReg = request.getParameter("vehicle-number");

       
        Driver driver = new Driver(fullName, email, phone, password, licenseNo, vehicleType, vehicleReg);

       
        boolean isRegistered = driverService.registerDriver(driver);
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

         JSONObject jsonResponse = new JSONObject();
        if (isRegistered) {
            jsonResponse.put("message", "Driver Registration Successful!");
        } else {
            jsonResponse.put("message", "Error registering admin.");
        }

      
        out.print(jsonResponse.toString());
        out.flush(); 
    }
}
