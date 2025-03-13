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
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(urlPatterns = {"/api/bookings"}, loadOnStartup = 1)
public class BookingsApiServlet extends HttpServlet {
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
            String filter = request.getParameter("filter");
            List<Booking> bookings;
            
            if (filter == null || filter.equalsIgnoreCase("all")) {
                bookings = bookingService.getAllBookings();
            } else {
                bookings = bookingService.getBookingsByStatus(filter.toUpperCase());
            }
            

            System.out.println("Retrieved " + bookings.size() + " bookings with filter: " + filter);
            
            if (bookings.isEmpty()) {
                response.getWriter().write("[]");
                return;
            }
            
            String json = convertBookingsToJson(bookings);

            System.out.println("JSON response: " + json);
            
            response.getWriter().write(json);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(String.format("{\"error\":\"%s\"}", escapeJson(e.getMessage())));
        }
    }
    
    private String convertBookingsToJson(List<Booking> bookings) {
        StringBuilder json = new StringBuilder("[");
        
        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            json.append("{")
                .append("\"bookingId\":").append(b.getBookingId()).append(",")
                .append("\"passengerName\":\"").append(escapeJson(b.getPassengerName())).append("\",")
                .append("\"pickupLocation\":\"").append(escapeJson(b.getPickupLocation())).append("\",")
                .append("\"dropoffLocation\":\"").append(escapeJson(b.getDropoffLocation())).append("\",")
                .append("\"bookingDateTime\":\"").append(escapeJson(b.getBookingDateTime())).append("\",")
                .append("\"vehicleType\":\"").append(escapeJson(b.getVehicleType())).append("\",")
                .append("\"status\":\"").append(b.getStatus() != null ? escapeJson(b.getStatus()) : "PENDING").append("\"");
            

            if (b.getHireFee() != null) {
                json.append(",\"hireFee\":").append(b.getHireFee());
            } else {
                json.append(",\"hireFee\":null");
            }
            

            if (b.getDriverName() != null && !b.getDriverName().isEmpty()) {
                json.append(",\"driverName\":\"").append(escapeJson(b.getDriverName())).append("\"");
            }
            
            json.append("}");
            if (i < bookings.size() - 1) json.append(",");
        }
        return json.append("]").toString();
    }
    
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}