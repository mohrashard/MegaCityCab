package com.megacitycab.servlet;

import com.megacitycab.model.Booking;
import com.megacitycab.repository.BookingRepository;
import com.megacitycab.repository.BookingRepositoryImpl;
import com.megacitycab.service.BookingService;
import com.megacitycab.service.BookingServiceImpl;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/book")
public class BookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private BookingService bookingService;
    
    @Override
    public void init() throws ServletException {

        BookingRepository bookingRepository = new BookingRepositoryImpl();
        bookingService = new BookingServiceImpl(bookingRepository);
    }
    
  @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    try {
        HttpSession session = request.getSession();
        Integer passengerId = (Integer) session.getAttribute("userId");

        if (passengerId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("User not logged in");
            return;
        }
        
        String vehicleType = request.getParameter("vehicleType");
        String pickupLocation = request.getParameter("pickupLocation");
        String dropoffLocation = request.getParameter("dropoffLocation");
        String bookingDateTime = request.getParameter("bookingDateTime");
        String paymentMethod = request.getParameter("paymentMethod");


        StringBuilder missingFields = new StringBuilder();
        if (vehicleType == null || vehicleType.isEmpty()) missingFields.append("Vehicle Type is required. ");
        if (pickupLocation == null || pickupLocation.isEmpty()) missingFields.append("Pickup Location is required. ");
        if (dropoffLocation == null || dropoffLocation.isEmpty()) missingFields.append("Drop-off Location is required. ");
        if (bookingDateTime == null || bookingDateTime.isEmpty()) missingFields.append("Date & Time is required. ");
        if (paymentMethod == null || paymentMethod.isEmpty()) missingFields.append("Payment Method is required. ");

        if (missingFields.length() > 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(missingFields.toString());
            return;
        }
        
        Booking booking = new Booking();
        booking.setPassengerId(passengerId);
        booking.setVehicleType(vehicleType);
        booking.setPickupLocation(pickupLocation);
        booking.setDropoffLocation(dropoffLocation);
        booking.setBookingDateTime(bookingDateTime);
        booking.setPaymentMethod(paymentMethod);
        
        boolean success = bookingService.createBooking(booking);
        
        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Booking successfully created");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Failed to create booking. Please check input data and try again.");
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("Error: " + e.getMessage());
    }
}
}