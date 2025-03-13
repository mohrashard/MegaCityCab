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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet("/driver/transactions")
public class DriverTransactionServlet extends HttpServlet {

    private final DriverTransactionDAO transactionDAO = new DriverTransactionDAOImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !session.getAttribute("userType").equals("driver")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        int driverId = (int) session.getAttribute("userId");
        String transactionType = request.getParameter("type");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        
        Date startDate = null;
        Date endDate = null;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        try {
            if (startDateStr != null && !startDateStr.isEmpty()) {
                LocalDate localStartDate = LocalDate.parse(startDateStr, formatter);
                startDate = Date.valueOf(localStartDate);
            }
            
            if (endDateStr != null && !endDateStr.isEmpty()) {
                LocalDate localEndDate = LocalDate.parse(endDateStr, formatter);
                endDate = Date.valueOf(localEndDate);
            }
        } catch (DateTimeParseException e) {
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Invalid date format. Please use yyyy-MM-dd format.\"}");
            return;
        }
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
            List<DriverTransaction> transactions = transactionDAO.findByDriverIdAndFilters(driverId, transactionType, startDate, endDate);
            
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");
            jsonBuilder.append("\"success\": true,");
            jsonBuilder.append("\"transactions\": [");
            
            for (int i = 0; i < transactions.size(); i++) {
                DriverTransaction transaction = transactions.get(i);
                jsonBuilder.append("{");
                jsonBuilder.append("\"transactionId\": ").append(transaction.getTransactionId()).append(",");
                jsonBuilder.append("\"driverId\": ").append(transaction.getDriverId()).append(",");
                jsonBuilder.append("\"transactionType\": \"").append(transaction.getTransactionType()).append("\",");
                jsonBuilder.append("\"amount\": ").append(transaction.getAmount()).append(",");
                jsonBuilder.append("\"description\": \"").append(transaction.getDescription().replace("\"", "\\\"")).append("\",");
                jsonBuilder.append("\"dateTime\": \"").append(transaction.getDateTime().toString()).append("\"");
                jsonBuilder.append("}");
                
                if (i < transactions.size() - 1) {
                    jsonBuilder.append(",");
                }
            }
            
            jsonBuilder.append("]");
            jsonBuilder.append("}");
            
            out.print(jsonBuilder.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // This method would be used for CSV download functionality
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !session.getAttribute("userType").equals("driver")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        int driverId = (int) session.getAttribute("userId");
        String transactionType = request.getParameter("type");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        
        Date startDate = null;
        Date endDate = null;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        try {
            if (startDateStr != null && !startDateStr.isEmpty()) {
                LocalDate localStartDate = LocalDate.parse(startDateStr, formatter);
                startDate = Date.valueOf(localStartDate);
            }
            
            if (endDateStr != null && !endDateStr.isEmpty()) {
                LocalDate localEndDate = LocalDate.parse(endDateStr, formatter);
                endDate = Date.valueOf(localEndDate);
            }
        } catch (DateTimeParseException e) {
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Invalid date format. Please use yyyy-MM-dd format.\"}");
            return;
        }
        
        List<DriverTransaction> transactions = transactionDAO.findByDriverIdAndFilters(driverId, transactionType, startDate, endDate);
        
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=transaction_statement.csv");
        PrintWriter out = response.getWriter();
        
        // CSV Header
        out.println("Transaction ID,Transaction Type,Amount,Description,Date Time");
        
        // CSV Data
        for (DriverTransaction transaction : transactions) {
            out.print(transaction.getTransactionId());
            out.print(",");
            out.print(transaction.getTransactionType());
            out.print(",");
            out.print(transaction.getAmount());
            out.print(",\"");
            // Escape double quotes in CSV by doubling them
            out.print(transaction.getDescription().replace("\"", "\"\""));
            out.print("\",");
            out.println(transaction.getDateTime().toString());
        }
    }
}