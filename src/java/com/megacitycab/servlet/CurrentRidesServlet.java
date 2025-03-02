package com.megacitycab.servlet;

import com.megacitycab.dto.RideDTO;
import com.megacitycab.service.RideService;
import com.megacitycab.service.RideServiceImpl;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(urlPatterns = {"/currentRides"}) 

public class CurrentRidesServlet extends HttpServlet {
    private final RideService rideService = new RideServiceImpl();
    private static final Logger logger = Logger.getLogger(CurrentRidesServlet.class.getName());
    
    @Override
public void init() throws ServletException {
    super.init();
    System.out.println("CurrentRidesServlet initialized with path: " + getServletContext().getContextPath());
}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject responseJson = new JSONObject();
        HttpSession session = request.getSession(false);

        try {
            // Validate session
            if (session == null || session.getAttribute("userId") == null) {
                responseJson.put("success", false)
                           .put("message", "Session expired. Please login again.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(responseJson.toString());
                return;
            }

            // Verify driver authentication
            Object userIdObj = session.getAttribute("userId");
            String userType = (String) session.getAttribute("userType");
            
            if (!(userIdObj instanceof Integer)) {
                responseJson.put("success", false)
                           .put("message", "Invalid session data.");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(responseJson.toString());
                return;
            }

            Integer driverId = (Integer) userIdObj;

            if (driverId == null || !"driver".equals(userType)) {
                responseJson.put("success", false)
                           .put("message", "Driver authentication failed");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(responseJson.toString());
                return;
            }

            // Get rides from service
            List<RideDTO> rides = rideService.getCurrentRides(driverId);
            JSONArray jsonRides = new JSONArray();

            if (rides != null && !rides.isEmpty()) {
                for (RideDTO ride : rides) {
                    JSONObject jsonRide = new JSONObject();
                    jsonRide.put("bookingId", ride.getBookingId());
                    jsonRide.put("passengerName", sanitize(ride.getPassengerName()));
                    jsonRide.put("passengerPhone", sanitize(ride.getPassengerPhone()));
                    jsonRide.put("pickupLocation", sanitize(ride.getPickupLocation()));
                    jsonRide.put("dropoffLocation", sanitize(ride.getDropoffLocation()));
                    jsonRide.put("bookingDatetime", ride.getBookingDatetime());
                    jsonRide.put("hireFee", ride.getHireFee());
                    jsonRide.put("adminCharge", ride.getAdminCharge());
                    jsonRide.put("driverEarnings", ride.getDriverEarnings());
                    jsonRide.put("status", sanitize(ride.getStatus()));
                    jsonRide.put("driverId", ride.getDriverId());
                    jsonRides.put(jsonRide);
                }
            }

            responseJson.put("success", true)
                       .put("rides", jsonRides);
            response.getWriter().write(responseJson.toString());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in CurrentRidesServlet: ", e);
            responseJson.put("success", false)
                       .put("message", "Server error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(responseJson.toString());
        } finally {
            response.getWriter().flush();
            response.getWriter().close();
        }
    }

    private String sanitize(String input) {
        return input != null ? input.replace("\"", "'") : ""; // Simple sanitization
    }
}
