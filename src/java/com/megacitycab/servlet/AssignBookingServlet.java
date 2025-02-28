package com.megacitycab.servlet;

import com.megacitycab.config.DBConnection;
import com.megacitycab.service.BookingService;
import com.megacitycab.service.BookingServiceImpl;
import com.megacitycab.repository.BookingRepository;
import com.megacitycab.repository.BookingRepositoryImpl;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/bookings/assign")
public class AssignBookingServlet extends HttpServlet {
    private BookingRepository bookingRepository;
    
    @Override
    public void init() {
        bookingRepository = new BookingRepositoryImpl(); 
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            int driverId = Integer.parseInt(request.getParameter("driverId"));
            

            boolean success = bookingRepository.assignDriver(bookingId, driverId);
            
            if (success) {
                out.print("{\"success\":true}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Could not assign driver to booking\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid booking or driver ID\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}