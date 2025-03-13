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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String licenseNo = request.getParameter("nic");
        String vehicleType = request.getParameter("vehicle-type");

        JSONObject jsonResponse = new JSONObject();

       
        if (driverService.isEmailTaken(email)) {
            jsonResponse.put("message", "Email is already taken");
        } else if (driverService.isPhoneTaken(phone)) {
            jsonResponse.put("message", "Phone number is already taken");
        } else if (driverService.isLicenseTaken(licenseNo)) {
            jsonResponse.put("message", "License number is already taken");
        } else {
            Driver driver = new Driver(fullName, email, phone, licenseNo, vehicleType);
            boolean isRegistered = driverService.registerDriver(driver, password);
            if (isRegistered) {
                jsonResponse.put("message", "Driver Registration Successful!");
            } else {
                jsonResponse.put("message", "Error registering driver.");
            }
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }
}
