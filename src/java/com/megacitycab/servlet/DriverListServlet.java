package com.megacitycab.servlet;

import com.megacitycab.model.DriverInfo;
import com.megacitycab.service.DriverServiceImpl;
import com.megacitycab.service.DriverServiceTwo;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/drivers")
public class DriverListServlet extends HttpServlet {
    private DriverServiceTwo driverService = new DriverServiceImpl();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
            String vehicleType = request.getParameter("vehicleType");
            List<DriverInfo> drivers = (vehicleType != null) ? 
                driverService.getDriversByVehicleType(vehicleType) : 
                driverService.getAllDrivers();
            
            StringBuilder json = new StringBuilder("[");
            
            for (DriverInfo d : drivers) {
                json.append("{")
                    .append("\"driverId\":").append(d.getDriverId()).append(",")
                    .append("\"fullName\":\"").append(d.getFullName()).append("\",")
                    .append("\"email\":\"").append(d.getEmail()).append("\",")
                    .append("\"phone\":\"").append(d.getPhone()).append("\",")
                    .append("\"licenseNo\":\"").append(d.getLicenseNo()).append("\",")
                    .append("\"vehicleType\":\"").append(d.getVehicleType()).append("\",")
                    .append("\"status\":\"").append(d.getStatus()).append("\"")
                    .append("},");
            }
            
            if (!drivers.isEmpty()) {
                json.setLength(json.length() - 1); 
            }
            json.append("]");
            
            out.print(json.toString());
            response.setStatus(HttpServletResponse.SC_OK);
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error retrieving drivers: " + e.getMessage() + "\"}");
        } finally {
            out.close(); 
        }
    }
}
