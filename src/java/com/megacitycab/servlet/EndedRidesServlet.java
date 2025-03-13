
package com.megacitycab.servlet;

import com.megacitycab.dto.RideDTO;
import com.megacitycab.service.RideService;
import com.megacitycab.service.RideServiceImpl;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/driver/ended-rides")
public class EndedRidesServlet extends HttpServlet {
    
    private final RideService rideService = new RideServiceImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");
        
    
        if (userId == null || !userType.equals("driver")) {
            response.sendRedirect(request.getContextPath() + "login.html");
            return;
        }
        
        List<RideDTO> rides = rideService.getEndedRides(userId);
        
      
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            JSONArray jsonRides = new JSONArray();
            for (RideDTO ride : rides) {
                JSONObject jsonRide = new JSONObject();
                jsonRide.put("bookingId", ride.getBookingId());
                jsonRide.put("passengerName", ride.getPassengerName());
                jsonRide.put("passengerPhone", ride.getPassengerPhone());
                jsonRide.put("pickupLocation", ride.getPickupLocation());
                jsonRide.put("dropoffLocation", ride.getDropoffLocation());
                jsonRide.put("bookingDatetime", ride.getBookingDatetime());
                jsonRide.put("hireFee", ride.getHireFee());
                jsonRide.put("adminCharge", ride.getAdminCharge());
                jsonRide.put("driverEarnings", ride.getDriverEarnings());
                jsonRide.put("status", ride.getStatus());
                jsonRides.put(jsonRide);
            }
            
            response.getWriter().write(jsonRides.toString());
        } else {

            request.setAttribute("endedRides", rides);
            request.getRequestDispatcher("/driver/dashboard.jsp").forward(request, response);
        }
    }
}
