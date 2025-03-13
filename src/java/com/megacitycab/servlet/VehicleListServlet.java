
package com.megacitycab.servlet;

import com.megacitycab.model.Vehicle;
import com.megacitycab.service.VehicleService;
import com.megacitycab.service.VehicleServiceImpl;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/vehicles/*")
public class VehicleListServlet extends HttpServlet {
    private VehicleService vehicleService = new VehicleServiceImpl();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
        
                List<Vehicle> vehicles = vehicleService.getAllVehicles();
                out.print(convertVehiclesToJson(vehicles));
            } else {
          
                String[] splits = pathInfo.split("/");
                if (splits.length != 2) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format");
                    return;
                }
                
                int vehicleId = Integer.parseInt(splits[1]);
                Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
                
                if (vehicle == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Vehicle not found");
                    return;
                }
                
                out.print(convertVehicleToJson(vehicle));
            }
            
            response.setStatus(HttpServletResponse.SC_OK);
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid vehicle ID format\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error processing request: " + e.getMessage() + "\"}");
        }
    }

    private String convertVehiclesToJson(List<Vehicle> vehicles) {
        StringBuilder json = new StringBuilder("[");
        for (Vehicle v : vehicles) {
            json.append(convertVehicleToJson(v)).append(",");
        }
        if (!vehicles.isEmpty()) json.setLength(json.length() - 1);
        return json.append("]").toString();
    }

    private String convertVehicleToJson(Vehicle v) {
        return String.format(
            "{\"id\":%d,\"plateNumber\":\"%s\",\"vehicleType\":\"%s\",\"model\":\"%s\"," +
            "\"brand\":\"%s\",\"passengerCapacity\":%d,\"status\":\"%s\"}",
            v.getId(), v.getPlateNumber(), v.getVehicleType(), v.getModel(),
            v.getBrand(), v.getPassengerCapacity(), v.getStatus()
        );
    }
}