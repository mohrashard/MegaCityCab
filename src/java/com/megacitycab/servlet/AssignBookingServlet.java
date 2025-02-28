package com.megacitycab.servlet;

import com.megacitycab.config.DBConnection;
import com.megacitycab.service.BookingService;
import com.megacitycab.service.BookingServiceImpl;
import com.megacitycab.repository.BookingRepository;
import com.megacitycab.repository.BookingRepositoryImpl;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/bookings/assign"})
public class AssignBookingServlet extends HttpServlet {
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

        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            int driverId = Integer.parseInt(request.getParameter("driverId"));

            // Verify if driver exists
            if (!checkDriverExists(driverId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Driver does not exist\"}");
                return;
            }

            boolean success = bookingService.assignDriver(bookingId, driverId);

            if (success) {
          response.getWriter().write("{\"success\":true}");
            }  else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Assignment failed\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid input data\"}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    private boolean checkDriverExists(int driverId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM [megacitycab].[dbo].[Drivers] WHERE driver_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

  private void updateBookingStatus(int bookingId, String status) throws SQLException {
        String sql = "UPDATE [megacitycab].[dbo].[Bookings] SET status = ? WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            stmt.executeUpdate();
        }
    }
}
