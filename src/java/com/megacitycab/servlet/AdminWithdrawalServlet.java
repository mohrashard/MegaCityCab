package com.megacitycab.servlet;

import com.megacitycab.service.AdminTransactionService;
import com.megacitycab.service.AdminTransactionService;
import com.megacitycab.service.AdminTransactionServiceImpl;
import com.megacitycab.service.AdminTransactionServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/admin/withdrawal")
public class AdminWithdrawalServlet extends HttpServlet {
    private final AdminTransactionService adminTransactionService = new AdminTransactionServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    Integer adminId = (Integer) session.getAttribute("adminId");
    
    if (adminId == null) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print("{\"success\": false, \"message\": \"Unauthorized\"}");
        return;
    }
        

        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            // Read request body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            // Parse request body manually
            String body = sb.toString();
            String amountStr = extractValueFromJson(body, "amount");
            String description = extractValueFromJson(body, "description");
            
            if (amountStr == null || amountStr.isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Amount is required.\"}");
                return;
            }
            
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
            } catch (NumberFormatException e) {
                out.print("{\"success\": false, \"message\": \"Invalid amount format.\"}");
                return;
            }
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                out.print("{\"success\": false, \"message\": \"Amount must be greater than zero.\"}");
                return;
            }
            
            // Check if withdrawal is allowed
            if (!adminTransactionService.canWithdraw(adminId, amount)) {
                out.print("{\"success\": false, \"message\": \"Withdrawal limit of LKR 100,000 exceeded for today.\"}");
                return;
            }
            
            // Process withdrawal
            boolean success = adminTransactionService.processWithdrawal(adminId, amount, description);
            
            if (success) {
                out.print("{\"success\": true, \"message\": \"Withdrawal processed successfully.\"}");
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to process withdrawal.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }
    
private int getAdminIdFromSession(HttpSession session) {
        Object adminIdObj = session.getAttribute("adminId");
        if (adminIdObj instanceof Integer) {
            return (Integer) adminIdObj;
        } else {
            throw new RuntimeException("Admin not logged in");
        }
    }
    
    // Helper method to extract values from JSON string
    private String extractValueFromJson(String json, String key) {
        String keyWithQuotes = "\"" + key + "\"";
        int keyIndex = json.indexOf(keyWithQuotes);
        if (keyIndex == -1) {
            return null;
        }
        
        int valueStartIndex = json.indexOf(":", keyIndex) + 1;
        while (valueStartIndex < json.length() && Character.isWhitespace(json.charAt(valueStartIndex))) {
            valueStartIndex++;
        }
        
        boolean isStringValue = json.charAt(valueStartIndex) == '"';
        if (isStringValue) {
            valueStartIndex++; // Skip opening quote
            int valueEndIndex = json.indexOf("\"", valueStartIndex);
            return json.substring(valueStartIndex, valueEndIndex);
        } else {
            int valueEndIndex = json.indexOf(",", valueStartIndex);
            if (valueEndIndex == -1) {
                valueEndIndex = json.indexOf("}", valueStartIndex);
            }
            return json.substring(valueStartIndex, valueEndIndex).trim();
        }
    }
}