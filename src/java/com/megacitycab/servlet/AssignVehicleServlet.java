package com.megacitycab.servlet;

import com.megacitycab.service.VehicleService;
import com.megacitycab.service.VehicleServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;

@WebServlet("/admin/vehicle/assign")
public class AssignVehicleServlet extends HttpServlet {
    
    private VehicleService vehicleService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        vehicleService = new VehicleServiceImpl();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {

            String vehicleIdStr = request.getParameter("vehicleId");
            String driverIdStr = request.getParameter("driverId");
      
            if (vehicleIdStr == null || driverIdStr == null) {
                try {
   
                    StringBuilder sb = new StringBuilder();
                    BufferedReader reader = request.getReader();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    
        
                    if (sb.length() > 0) {
                        JSONObject jsonObject = new JSONObject(sb.toString());
                        
                        vehicleIdStr = String.valueOf(jsonObject.optInt("vehicleId", 0));
                        driverIdStr = String.valueOf(jsonObject.optInt("driverId", 0));
                    }
                } catch (Exception e) {
               
                }
            }
            

            if (vehicleIdStr == null || driverIdStr == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Missing required parameters\"}");
                return;
            }
            
            int vehicleId = Integer.parseInt(vehicleIdStr);
            int driverId = Integer.parseInt(driverIdStr);
            
            boolean success = vehicleService.assignDriverToVehicle(vehicleId, driverId);
            
            if (success) {
                out.print("{\"success\": true, \"message\": \"Driver assigned to vehicle successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\": false, \"message\": \"Failed to assign driver to vehicle\"}");
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Invalid vehicle or driver ID\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }
}