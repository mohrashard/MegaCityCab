package com.megacitycab.servlet;

import com.megacitycab.model.Booking;
import com.megacitycab.repository.BookingRepositoryImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/bookhistory")
public class BookingHistoryServlet extends HttpServlet {
    private BookingRepositoryImpl bookingDAO;

    @Override
    public void init() {
        bookingDAO = new BookingRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer passengerId = (Integer) session.getAttribute("userId");

        if (passengerId == null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": false, \"message\": \"User not authenticated\"}");
            return;
        }

        List<Booking> bookings = bookingDAO.getBookingsByPassengerId(passengerId);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            json.append("{")
                    .append("\"bookingId\":").append(booking.getBookingId()).append(",")
                    .append("\"passengerId\":").append(booking.getPassengerId()).append(",")
                    .append("\"pickupLocation\":\"").append(escapeJson(booking.getPickupLocation())).append("\",")
                    .append("\"dropoffLocation\":\"").append(escapeJson(booking.getDropoffLocation())).append("\",")
                    .append("\"vehicleType\":\"").append(escapeJson(booking.getVehicleType())).append("\",")
                    .append("\"hireFee\":").append(booking.getHireFee()).append(",")
                    .append("\"status\":\"").append(escapeJson(booking.getStatus())).append("\",")
                    .append("\"paymentMethod\":\"").append(escapeJson(booking.getPaymentMethod())).append("\",")
                    .append("\"driverName\":\"").append(escapeJson(booking.getDriverName() != null ? booking.getDriverName() : "Pending")).append("\",")
                    .append("\"driverPhone\":\"").append(escapeJson(booking.getDriverPhone() != null ? booking.getDriverPhone() : "Pending")).append("\"")
                    .append("}");
            
            if (i < bookings.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        response.getWriter().write(json.toString());
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}