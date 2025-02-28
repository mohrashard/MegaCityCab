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

@WebServlet(urlPatterns = {"/api/bookings/update-fee"}, loadOnStartup = 1)
public class UpdateBookingFeeServlet extends HttpServlet {
    private BookingService bookingService;
    
    @Override
    public void init() {
        BookingRepository bookingRepository = new BookingRepositoryImpl();
        bookingService = new BookingServiceImpl(bookingRepository);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            String bookingIdStr = request.getParameter("bookingId");
            String hireFeeStr = request.getParameter("hireFee");
            
            if (bookingIdStr == null || hireFeeStr == null) {
                response.setStatus(400);
                response.getWriter().write("{\"error\":\"Missing required parameters: bookingId and hireFee\"}");
                return;
            }
            
            int bookingId;
            double hireFee;
            
            try {
                bookingId = Integer.parseInt(bookingIdStr);
                hireFee = Double.parseDouble(hireFeeStr);
            } catch (NumberFormatException e) {
                response.setStatus(400);
                response.getWriter().write("{\"error\":\"Invalid bookingId or hireFee format\"}");
                return;
            }
            
            Booking booking = bookingService.getBooking(bookingId);
            if (booking == null) {
                response.setStatus(404);
                response.getWriter().write("{\"error\":\"Booking not found\"}");
                return;
            }
            
            booking.setHireFee(hireFee);
              boolean success = bookingService.updateBookingFee(bookingId, hireFee);
            
            if (success) {
                response.getWriter().write("{\"success\":true,\"message\":\"Hire fee updated successfully\"}");
            } else {
                response.setStatus(500);
                response.getWriter().write("{\"error\":\"Failed to update hire fee\"}");
            }
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write(String.format("{\"error\":\"%s\"}", e.getMessage().replace("\"", "\\\"")));
            e.printStackTrace();
        }
    }
}