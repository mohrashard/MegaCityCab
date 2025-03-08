package com.megacitycab.servlet;

import com.megacitycab.model.AdminTransaction;
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
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/admin/transactions")
public class AdminTransactionServlet extends HttpServlet {
    private final AdminTransactionService adminTransactionService = new AdminTransactionServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    HttpSession session = request.getSession();
    Integer adminId = (Integer) session.getAttribute("adminId");
    
    if (adminId == null) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print("{\"success\": false, \"message\": \"Unauthorized\"}");
        return;
    }

        
    
        String limitParam = request.getParameter("limit");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            List<AdminTransaction> transactions;
            
            if (limitParam != null) {
                int limit = Integer.parseInt(limitParam);
                transactions = adminTransactionService.getRecentTransactionsByAdminId(adminId, limit);
            } else {
                transactions = adminTransactionService.getAllTransactionsByAdminId(adminId);
            }
            
            StringBuilder jsonResponse = new StringBuilder();
            jsonResponse.append("{\"success\": true, \"transactions\": [");
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            for (int i = 0; i < transactions.size(); i++) {
                AdminTransaction transaction = transactions.get(i);
                LocalDateTime createdAt = transaction.getCreatedAt();
                String formattedDate = createdAt != null ? createdAt.format(formatter) : "";
                
                jsonResponse.append("{")
                    .append("\"transactionId\": ").append(transaction.getTransactionId()).append(",")
                    .append("\"adminId\": ").append(transaction.getAdminId()).append(",")
                    .append("\"amount\": \"").append(transaction.getAmount()).append("\",")
                    .append("\"transactionType\": \"").append(transaction.getTransactionType()).append("\",")
                    .append("\"description\": \"").append(transaction.getDescription()).append("\",")
                    .append("\"status\": \"").append(transaction.getStatus()).append("\",")
                    .append("\"createdAt\": \"").append(formattedDate).append("\"")
                    .append("}");
                
                if (i < transactions.size() - 1) {
                    jsonResponse.append(",");
                }
            }
            
            jsonResponse.append("]}");
            out.print(jsonResponse.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        } catch (NumberFormatException e) {
            out.print("{\"success\": false, \"message\": \"Invalid limit parameter.\"}");
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
}