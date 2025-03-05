package com.megacitycab.servlet;

import com.megacitycab.config.DBConnection;
import com.megacitycab.dao.TransactionDAO;
import com.megacitycab.dao.WalletDAO;
import com.megacitycab.model.Transaction;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/topUpWallet")
public class TopUpWalletServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(TopUpWalletServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Connection conn = null;


        Integer passengerId = (Integer) request.getSession().getAttribute("userId");
        if (passengerId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"User not authenticated\"}");
            return;
        }


        StringBuilder jsonString = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid request body\"}");
            return;
        }


        JSONObject json;
        try {
            json = new JSONObject(jsonString.toString());
        } catch (JSONException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid JSON format\"}");
            return;
        }


        BigDecimal amount;
        try {
            amount = json.getBigDecimal("amount");
        } catch (JSONException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Amount is required and must be a valid number\"}");
            return;
        }

        String description = json.optString("description", "Top-Up");

   
        if (amount.compareTo(new BigDecimal("100")) < 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Minimum top-up amount is LKR 100\"}");
            return;
        }

        WalletDAO walletDAO = new WalletDAO();
        TransactionDAO transactionDAO = new TransactionDAO();

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); 

            walletDAO.updateWalletBalance(conn, passengerId, amount);

            Transaction transaction = new Transaction();
            transaction.setPassengerId(passengerId);
            transaction.setAmount(amount);
            transaction.setTransactionType("Top-Up");
            transaction.setDescription(description);
            transaction.setDateTime(LocalDateTime.now());

            transactionDAO.addTransaction(conn, transaction);

  
            conn.commit();

      
            BigDecimal newBalance = walletDAO.getWalletBalance(conn, passengerId);


            JSONObject responseJson = new JSONObject();
            responseJson.put("message", "Top-up successful");
            responseJson.put("newBalance", newBalance);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(responseJson.toString());

        } catch (SQLException ex) {
            try {
                if (conn != null) {
                    conn.rollback(); 
                }
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Transaction rollback failed", rollbackEx);
            }
            LOGGER.log(Level.SEVERE, "Database error", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to top up wallet\"}");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    LOGGER.log(Level.SEVERE, "Database connection closing error", closeEx);
                }
            }
        }
    }
}