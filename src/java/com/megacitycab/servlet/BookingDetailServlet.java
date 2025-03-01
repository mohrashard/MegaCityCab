package com.megacitycab.servlet;

import com.megacitycab.model.Booking;
import com.megacitycab.service.BookingService;
import com.megacitycab.service.BookingServiceImpl;
import com.megacitycab.repository.BookingRepository;
import com.megacitycab.repository.BookingRepositoryImpl;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/bookings/details/*"}, loadOnStartup = 1)
public class BookingDetailServlet extends HttpServlet {
    private BookingService bookingService;
    
    @Override
    public void init() {
        BookingRepository bookingRepository = new BookingRepositoryImpl();
        bookingService = new BookingServiceImpl(bookingRepository);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(400);
                response.getWriter().write("{\"error\":\"Missing booking ID\"}");
                return;
            }
            
            String[] parts = pathInfo.split("/");
            String bookingIdStr = parts[1];
            
            int bookingId;
            try {
                bookingId = Integer.parseInt(bookingIdStr);
            } catch (NumberFormatException e) {
                response.setStatus(400);
                response.getWriter().write("{\"error\":\"Invalid booking ID\"}");
                return;
            }
            
            Booking booking = bookingService.getBooking(bookingId);
            if (booking == null) {
                response.setStatus(404);
                response.getWriter().write("{\"error\":\"Booking not found\"}");
                return;
            }
            

            StringBuilder json = new StringBuilder("{");
            json.append("\"bookingId\":").append(booking.getBookingId()).append(",")
                .append("\"passengerId\":").append(booking.getPassengerId()).append(",")
                .append("\"passengerName\":\"").append(escapeJson(booking.getPassengerName())).append("\",")
                .append("\"pickupLocation\":\"").append(escapeJson(booking.getPickupLocation())).append("\",")
                .append("\"dropoffLocation\":\"").append(escapeJson(booking.getDropoffLocation())).append("\",")
                .append("\"bookingDateTime\":\"").append(escapeJson(booking.getBookingDateTime())).append("\",")
                .append("\"vehicleType\":\"").append(escapeJson(booking.getVehicleType())).append("\",")
                .append("\"paymentMethod\":\"").append(escapeJson(booking.getPaymentMethod())).append("\",")
                .append("\"status\":\"").append(booking.getStatus() != null ? escapeJson(booking.getStatus()) : "PENDING").append("\"");
                
 
            if (booking.getHireFee() != null) {
                json.append(",\"hireFee\":").append(booking.getHireFee());
            } else {
                json.append(",\"hireFee\":null");
            }
            

            if (booking.getDriverId() != null) {
                json.append(",\"driverId\":").append(booking.getDriverId());
            }
if (booking.getDriverName() != null) {
    json.append(",\"driverName\":\"").append(escapeJson(booking.getDriverName())).append("\"");
}
            
            json.append("}");
            
            response.getWriter().write(json.toString());
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write(String.format("{\"error\":\"%s\"}", escapeJson(e.getMessage())));
            e.printStackTrace();
        }
    }
    
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}