package com.megacitycab.servlet;

import com.megacitycab.service.VehicleService;
import com.megacitycab.service.VehicleServiceImpl;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/admin/vehicle/unassign")
public class UnassignVehicleServlet extends HttpServlet {
    
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

            int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
            

            boolean success = vehicleService.unassignDriver(vehicleId);
            
            if (success) {
                out.print("{\"success\": true}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"error\": \"Unassignment failed. Vehicle may not exist or already unassigned.\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"error\": \"Invalid vehicle ID format\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"error\": \"Server error: " + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
    }
}