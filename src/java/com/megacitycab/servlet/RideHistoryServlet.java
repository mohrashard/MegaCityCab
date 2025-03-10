package com.megacitycab.servlet;

import com.megacitycab.config.DBConnection;
import org.json.JSONArray;
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

@WebServlet("/RideHistoryServlet")
public class RideHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONArray jsonArray = new JSONArray();


        HttpSession session = request.getSession();
        Integer driverId = (Integer) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");


        if (driverId == null || !"driver".equals(userType)) {
            String driverIdParam = request.getParameter("driverId");
            if (driverIdParam != null && !driverIdParam.isEmpty()) {
                driverId = Integer.parseInt(driverIdParam);
            } else {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("error", "Driver ID not found in session");
                out.print(errorResponse.toString());
                return;
            }
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("error", "Database connection failed");
                out.print(errorResponse.toString());
                return;
            }

            String query = "SELECT b.booking_id, b.passenger_id, b.vehicle_type, " +
                    "b.pickup_location, b.dropoff_location, b.booking_datetime, " +
                    "b.payment_method, b.hire_fee, b.status, " +
                    "p.full_name as passenger_name, " +
                    "CASE WHEN EXISTS (SELECT 1 FROM driver_transaction dt WHERE dt.driver_id = ? " +
                    "AND dt.transaction_type = 'Expense' AND dt.description = CONCAT('Admin fee for booking #', b.booking_id)) " +
                    "THEN 1 ELSE 0 END as admin_fee_paid " +
                    "FROM Bookings b " +
                    "JOIN Passengers p ON b.passenger_id = p.passenger_id " +
                    "WHERE b.driver_id = ? AND b.status = 'Ended' " +
                    "ORDER BY b.booking_datetime DESC";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, driverId);
            stmt.setInt(2, driverId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                JSONObject bookingObject = new JSONObject();
                
                int bookingId = rs.getInt("booking_id");
                double hireFee = rs.getDouble("hire_fee");
                double adminCharge = hireFee * 0.3; // 30% admin fee
                double driverEarnings = hireFee - adminCharge;
                boolean adminFeePaid = rs.getBoolean("admin_fee_paid");

                bookingObject.put("bookingId", bookingId);
                bookingObject.put("passengerName", rs.getString("passenger_name"));
                bookingObject.put("vehicleType", rs.getString("vehicle_type"));
                bookingObject.put("pickupLocation", rs.getString("pickup_location"));
                bookingObject.put("dropoffLocation", rs.getString("dropoff_location"));
                bookingObject.put("bookingDatetime", rs.getTimestamp("booking_datetime").toString());
                bookingObject.put("paymentMethod", rs.getString("payment_method"));
                bookingObject.put("hireFee", hireFee);
                bookingObject.put("adminCharge", adminCharge);
                bookingObject.put("driverEarnings", driverEarnings);
                bookingObject.put("status", rs.getString("status"));
                bookingObject.put("adminFeePaid", adminFeePaid);

                jsonArray.put(bookingObject);
            }

            out.print(jsonArray.toString());

        } catch (SQLException e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Database error: " + e.getMessage());
            out.print(errorResponse.toString());
        } catch (Exception e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Server error: " + e.getMessage());
            out.print(errorResponse.toString());
        }
    }
}