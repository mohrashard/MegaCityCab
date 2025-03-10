package com.megacitycab.servlet;

import com.megacitycab.config.DBConnection;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/DriverPaymentServlet")
public class DriverPaymentServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();


        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        
  
        JSONObject requestData = new JSONObject(sb.toString());
        

        int driverId;
        int rideId;
        String paymentMethod;
        
        try {
            driverId = requestData.getInt("driverId");
            rideId = requestData.getInt("rideId");
            paymentMethod = requestData.getString("paymentMethod");
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid request parameters: " + e.getMessage());
            out.print(jsonResponse.toString());
            return;
        }

 
        HttpSession session = request.getSession();
        Integer sessionDriverId = (Integer) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");

        
        if (sessionDriverId != null && "driver".equals(userType) && !sessionDriverId.equals(driverId)) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Unauthorized access: Session driver ID (" + sessionDriverId + 
                             ") does not match request driver ID (" + driverId + ")");
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
            

            String bookingQuery = "SELECT hire_fee FROM Bookings WHERE booking_id = ? AND driver_id = ? AND status = 'Ended'";
            PreparedStatement bookingStmt = conn.prepareStatement(bookingQuery);
            bookingStmt.setInt(1, rideId);
            bookingStmt.setInt(2, driverId);
            ResultSet bookingRs = bookingStmt.executeQuery();
            
            if (!bookingRs.next()) {
                conn.rollback();
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Booking not found or driver does not match");
                out.print(jsonResponse.toString());
                return;
            }
            
            double hireFee = bookingRs.getDouble("hire_fee");
            double adminFee = hireFee * 0.3; 

            String checkPaymentQuery = "SELECT 1 FROM driver_transaction WHERE driver_id = ? AND transaction_type = 'Expense' " +
                    "AND description = ?";
            PreparedStatement checkPaymentStmt = conn.prepareStatement(checkPaymentQuery);
            checkPaymentStmt.setInt(1, driverId);
            checkPaymentStmt.setString(2, "Admin fee for booking #" + rideId);
            ResultSet checkPaymentRs = checkPaymentStmt.executeQuery();
            
            if (checkPaymentRs.next()) {
                conn.rollback();
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Admin fee already paid for this booking");
                out.print(jsonResponse.toString());
                return;
            }
      
            if ("wallet".equals(paymentMethod)) {
                String walletQuery = "SELECT wallet_balance FROM driver_wallet WHERE driver_id = ?";
                PreparedStatement walletStmt = conn.prepareStatement(walletQuery);
                walletStmt.setInt(1, driverId);
                ResultSet walletRs = walletStmt.executeQuery();
                
                if (!walletRs.next()) {
                    conn.rollback();
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Wallet not found for driver");
                    out.print(jsonResponse.toString());
                    return;
                }
                
                double walletBalance = walletRs.getDouble("wallet_balance");
                
                if (walletBalance < adminFee) {
                    conn.rollback();
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Insufficient wallet balance");
                    out.print(jsonResponse.toString());
                    return;
                }
                

                String updateWalletQuery = "UPDATE driver_wallet SET wallet_balance = wallet_balance - ?, " +
                        "total_expenses = total_expenses + ? WHERE driver_id = ?";
                PreparedStatement updateWalletStmt = conn.prepareStatement(updateWalletQuery);
                updateWalletStmt.setDouble(1, adminFee);
                updateWalletStmt.setDouble(2, adminFee);
                updateWalletStmt.setInt(3, driverId);
                updateWalletStmt.executeUpdate();

                String driverTransactionQuery = "INSERT INTO driver_transaction (driver_id, transaction_type, amount, description) " +
                        "VALUES (?, 'Expense', ?, ?)";
                PreparedStatement driverTransactionStmt = conn.prepareStatement(driverTransactionQuery);
                driverTransactionStmt.setInt(1, driverId);
                driverTransactionStmt.setDouble(2, adminFee);
                driverTransactionStmt.setString(3, "Admin fee for booking #" + rideId);
                driverTransactionStmt.executeUpdate();
            } else if ("card".equals(paymentMethod) || "paypal".equals(paymentMethod)) {


                String driverTransactionQuery = "INSERT INTO driver_transaction (driver_id, transaction_type, amount, description) " +
                        "VALUES (?, 'Expense', ?, ?)";
                PreparedStatement driverTransactionStmt = conn.prepareStatement(driverTransactionQuery);
                driverTransactionStmt.setInt(1, driverId);
                driverTransactionStmt.setDouble(2, adminFee);
                driverTransactionStmt.setString(3, "Admin fee for booking #" + rideId + " via " + paymentMethod);
                driverTransactionStmt.executeUpdate();
            } else {
                conn.rollback();
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid payment method");
                out.print(jsonResponse.toString());
                return;
            }
            

            List<Integer> adminIds = new ArrayList<>();
            String getAllAdminsQuery = "SELECT adminId FROM Admins";
            PreparedStatement getAllAdminsStmt = conn.prepareStatement(getAllAdminsQuery);
            ResultSet adminRs = getAllAdminsStmt.executeQuery();
            
            while (adminRs.next()) {
                adminIds.add(adminRs.getInt("adminId"));
            }
            
            if (adminIds.isEmpty()) {
                adminIds.add(1);
            }
     
            for (Integer adminId : adminIds) {
                String checkAdminWalletQuery = "SELECT wallet_id, balance FROM AdminWallet WHERE admin_id = ?";
                PreparedStatement checkAdminWalletStmt = conn.prepareStatement(checkAdminWalletQuery);
                checkAdminWalletStmt.setInt(1, adminId);
                ResultSet checkAdminWalletRs = checkAdminWalletStmt.executeQuery();
                
                if (checkAdminWalletRs.next()) {
                    String updateAdminWalletQuery = "UPDATE AdminWallet SET balance = balance + ?, updated_at = GETDATE() " +
                            "WHERE admin_id = ?";
                    PreparedStatement updateAdminWalletStmt = conn.prepareStatement(updateAdminWalletQuery);
                    updateAdminWalletStmt.setDouble(1, adminFee);
                    updateAdminWalletStmt.setInt(2, adminId);
                    updateAdminWalletStmt.executeUpdate();
                } else {
                    String createAdminWalletQuery = "INSERT INTO AdminWallet (admin_id, balance) VALUES (?, ?)";
                    PreparedStatement createAdminWalletStmt = conn.prepareStatement(createAdminWalletQuery);
                    createAdminWalletStmt.setInt(1, adminId);
                    createAdminWalletStmt.setDouble(2, adminFee);
                    createAdminWalletStmt.executeUpdate();
                }
                
                String adminTransactionQuery = "INSERT INTO AdminTransactions (admin_id, amount, transaction_type, description) " +
                        "VALUES (?, ?, 'DRIVER_PAYMENT', ?)";
                PreparedStatement adminTransactionStmt = conn.prepareStatement(adminTransactionQuery);
                adminTransactionStmt.setInt(1, adminId);
                adminTransactionStmt.setDouble(2, adminFee);
                adminTransactionStmt.setString(3, "Admin fee from driver #" + driverId + " for booking #" + rideId);
                adminTransactionStmt.executeUpdate();
            }
            

            conn.commit();
            
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Payment successful");
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