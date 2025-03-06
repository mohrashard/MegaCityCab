package com.megacitycab.servlet;

import com.megacitycab.config.DBConnection;
import com.megacitycab.dao.CardDAO;
import com.megacitycab.dao.TransactionDAO;
import com.megacitycab.dao.WalletDAO;
import com.megacitycab.model.Transaction;
import com.megacitycab.repository.BookingRepositoryImpl;
import com.megacitycab.service.PaymentException;
import com.megacitycab.service.CardPayment;
import com.megacitycab.service.PaymentProcessor;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/process-payment")
public class PaymentServlet extends HttpServlet {
    private BookingRepositoryImpl bookingDAO = new BookingRepositoryImpl();
    private TransactionDAO transactionDAO = new TransactionDAO();
    private WalletDAO walletDAO = new WalletDAO();
    private PaymentProcessor cardPaymentProcessor = new CardPayment(new CardDAO(), transactionDAO);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String[] data = sb.toString().split("&");
            int bookingId = 0;
            String paymentMethod = "";
            BigDecimal amount = BigDecimal.ZERO;
            
            for (String param : data) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = URLDecoder.decode(keyValue[1], "UTF-8");
                    
                    switch (key) {
                        case "bookingId":
                            bookingId = Integer.parseInt(value);
                            break;
                        case "paymentMethod":
                            paymentMethod = value;
                            break;
                        case "amount":
                            amount = new BigDecimal(value);
                            break;
                    }
                }
            }
            
            HttpSession session = request.getSession();
            int passengerId = (Integer) session.getAttribute("userId");
            
            boolean success = processPayment(passengerId, bookingId, paymentMethod, amount);
            
            String jsonResponse = "{\"success\":" + success + "}";
            response.getWriter().write(jsonResponse);
            
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Payment processing failed");
            e.printStackTrace();
        }
    }

   private boolean processPayment(int passengerId, int bookingId, String paymentMethod, BigDecimal amount) {
    Connection conn = null;
    try {
        conn = DBConnection.getConnection();
        conn.setAutoCommit(false);
        

        if (paymentMethod.equals("Wallet")) {
            BigDecimal currentBalance = walletDAO.getWalletBalance(conn, passengerId);
            if (currentBalance.compareTo(amount) < 0) {
                throw new PaymentException("Insufficient wallet balance");
            }
        }
        

        if (!bookingDAO.updateBookingPayment(bookingId, paymentMethod, "ended")) {
            throw new PaymentException("Failed to update booking payment details");
        }
        

        Transaction transaction = new Transaction();
        transaction.setPassengerId(passengerId);
        transaction.setAmount(amount);
        transaction.setTransactionType("Expense");  
        transaction.setDescription(getPaymentDescription(paymentMethod));
        transaction.setDateTime(LocalDateTime.now());
        

        transactionDAO.addTransaction(conn, transaction);
 
        if (paymentMethod.equals("Wallet")) {
  
            walletDAO.updateWalletBalance(conn, passengerId, amount.negate());
        } else if (paymentMethod.equals("Card")) {
            cardPaymentProcessor.processPayment(passengerId, bookingId, amount, transaction.getDescription());
        }
        
        conn.commit();
        return true;
        
    } catch (SQLException | PaymentException e) {
        if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        e.printStackTrace();
        return false;
    } finally {
        if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}

    private String getPaymentDescription(String method) {
        switch (method) {
            case "Cash": return "Paid in Cash";
            case "Wallet": return "Wallet Payment";
            default: return "Card Payment";
        }
    }
}