
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

@WebServlet("/admin/vehicle/delete")
public class DeleteVehicleServlet extends HttpServlet {
    
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
            
    
            boolean success = vehicleService.deleteVehicle(vehicleId);
            
            if (success) {
                out.print("{\"success\": true, \"message\": \"Vehicle deleted successfully\"}");
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to delete vehicle\"}");
            }
            
        } catch (NumberFormatException e) {
            out.print("{\"success\": false, \"message\": \"Invalid vehicle ID\"}");
        } catch (Exception e) {
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }
}
