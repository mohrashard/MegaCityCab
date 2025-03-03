package com.megacitycab.servlet;

import com.megacitycab.model.Vehicle;
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

@WebServlet("/admin/vehicle/add")
public class AddVehicleServlet extends HttpServlet {
    
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
      
            String plateNumber = request.getParameter("plateNumber");
            String vehicleType = request.getParameter("vehicleType");
            String capacityStr = request.getParameter("passengerCapacity");
            String brand = request.getParameter("brand");
            String model = request.getParameter("model");
            
              if (plateNumber != null) {
            plateNumber = plateNumber.trim().toUpperCase();
        }
            
  
            if (plateNumber == null || vehicleType == null || capacityStr == null || brand == null || model == null) {
                try {
           
                    StringBuilder sb = new StringBuilder();
                    BufferedReader reader = request.getReader();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    
          
                    if (sb.length() > 0) {
                        JSONObject jsonObject = new JSONObject(sb.toString());
                        
                        plateNumber = jsonObject.optString("plateNumber", plateNumber);
                        vehicleType = jsonObject.optString("vehicleType", vehicleType);
                        capacityStr = String.valueOf(jsonObject.optInt("passengerCapacity", 0));
                        brand = jsonObject.optString("brand", brand);
                        model = jsonObject.optString("model", model);
                    }
                } catch (Exception e) {
                    
                }
            }
            
      
            if (plateNumber == null || vehicleType == null || capacityStr == null || brand == null || model == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Missing required parameters\"}");
                return;
            }
            
Vehicle existingVehicle = vehicleService.getVehicleByPlate(plateNumber);
if (existingVehicle != null) {
    response.setStatus(HttpServletResponse.SC_CONFLICT);
    out.print("{\"success\": false, \"message\": \"Plate number already exists\"}");
    return;
}

            
            int passengerCapacity = Integer.parseInt(capacityStr);
            
 
            Vehicle vehicle = new Vehicle(plateNumber, vehicleType, passengerCapacity, brand, model);
            boolean success = vehicleService.addVehicle(vehicle);
            
            if (success) {
                out.print("{\"success\": true, \"message\": \"Vehicle added successfully\", \"vehicleId\": " + vehicle.getId() + "}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\": false, \"message\": \"Failed to add vehicle\"}");
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Invalid passenger capacity\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }
}