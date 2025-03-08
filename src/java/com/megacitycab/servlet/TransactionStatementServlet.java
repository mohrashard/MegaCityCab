package com.megacitycab.servlet;

import com.megacitycab.dao.DriverTransactionDAO;
import com.megacitycab.dao.DriverTransactionDAOImpl;
import com.megacitycab.model.DriverTransaction;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/driver/transaction/statement")
public class TransactionStatementServlet extends HttpServlet {

    private final DriverTransactionDAO transactionDAO = new DriverTransactionDAOImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !session.getAttribute("userType").equals("driver")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        int driverId = (int) session.getAttribute("userId");
        
        try {
            // Get filter parameters
            String type = request.getParameter("type");
            
            Date startDate = null;
            if (request.getParameter("startDate") != null && !request.getParameter("startDate").isEmpty()) {
                startDate = Date.valueOf(request.getParameter("startDate"));
            }
            
            Date endDate = null;
            if (request.getParameter("endDate") != null && !request.getParameter("endDate").isEmpty()) {
                endDate = Date.valueOf(request.getParameter("endDate"));
            }
            
            // Get filtered transactions
            List<DriverTransaction> transactions = transactionDAO.findByDriverIdAndFilters(driverId, type, startDate, endDate);
            
            // Set response headers for CSV download
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"transaction_statement.csv\"");
            
            // Create CSV content
            PrintWriter writer = response.getWriter();
            
            // Write CSV header
            writer.println("Transaction ID,Date,Type,Amount (LKR),Description");
            
            // Format for date and time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            // Write transaction data
            for (DriverTransaction transaction : transactions) {
                writer.print(transaction.getTransactionId());
                writer.print(",");
                writer.print(transaction.getDateTime().format(formatter));
                writer.print(",");
                writer.print(transaction.getTransactionType());
                writer.print(",");
                
                // Format amount based on transaction type
                if (transaction.getTransactionType().equals("Top Up") || 
                    transaction.getTransactionType().equals("Ride Earnings")) {
                    writer.print("+");
                } else {
                    writer.print("-");
                }
                writer.print(transaction.getAmount());
                
                writer.print(",");
                // Ensure description doesn't break CSV format (escape commas)
                String description = transaction.getDescription().replace("\"", "\"\"");
                writer.print("\"" + description + "\"");
                writer.println();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Failed to generate statement: " + e.getMessage() + "\"}");
        }
    }
}