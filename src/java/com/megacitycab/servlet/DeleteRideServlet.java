package com.megacitycab.servlet;

import com.megacitycab.config.DBConnection;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/DeleteRideServlet")
public class DeleteRideServlet extends HttpServlet {

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        String rideIdParam = request.getParameter("rideId");
        if (rideIdParam == null || rideIdParam.isEmpty()) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Ride ID is required");
            out.print(jsonResponse.toString());
            return;
        }

        int rideId = Integer.parseInt(rideIdParam);


        HttpSession session = request.getSession();
        Integer driverId = (Integer) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");


        if (driverId == null || !"driver".equals(userType)) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Unauthorized access");
            out.print(jsonResponse.toString());
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Database connection failed");
                out.print(jsonResponse.toString());
                return;
            }


            conn.setAutoCommit(false);

            String checkBookingQuery = "SELECT booking_id FROM Bookings WHERE booking_id = ? AND driver_id = ? AND status = 'ended'";
            PreparedStatement checkBookingStmt = conn.prepareStatement(checkBookingQuery);
            checkBookingStmt.setInt(1, rideId);
            checkBookingStmt.setInt(2, driverId);
            ResultSet checkBookingRs = checkBookingStmt.executeQuery();

            if (!checkBookingRs.next()) {
                conn.rollback();
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Booking not found or not eligible for deletion");
                out.print(jsonResponse.toString());
                return;
            }

        
            String checkPaymentQuery = "SELECT 1 FROM driver_transaction WHERE driver_id = ? AND transaction_type = 'Expense' " +
                    "AND description LIKE ?";
            PreparedStatement checkPaymentStmt = conn.prepareStatement(checkPaymentQuery);
            checkPaymentStmt.setInt(1, driverId);
            checkPaymentStmt.setString(2, "Admin fee for booking #" + rideId + "%");
            ResultSet checkPaymentRs = checkPaymentStmt.executeQuery();

            if (!checkPaymentRs.next()) {
                conn.rollback();
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Admin fee must be paid before deleting this ride");
                out.print(jsonResponse.toString());
                return;
            }

            String deleteBookingQuery = "DELETE FROM Bookings WHERE booking_id = ?";
            PreparedStatement deleteBookingStmt = conn.prepareStatement(deleteBookingQuery);
            deleteBookingStmt.setInt(1, rideId);
            int rowsAffected = deleteBookingStmt.executeUpdate();

            if (rowsAffected == 0) {
                conn.rollback();
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to delete ride");
                out.print(jsonResponse.toString());
                return;
            }

            conn.commit();

            jsonResponse.put("success", true);
            jsonResponse.put("message", "Ride deleted successfully");
            out.print(jsonResponse.toString());

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                // Ignore
            }
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Database error: " + e.getMessage());
            out.print(jsonResponse.toString());
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                // Ignore
            }
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Server error: " + e.getMessage());
            out.print(jsonResponse.toString());
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                // Ignore
            }
        }
    }
}