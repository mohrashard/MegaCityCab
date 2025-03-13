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
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
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
            String type = request.getParameter("type");
            
            Date startDate = null;
            if (request.getParameter("startDate") != null && !request.getParameter("startDate").isEmpty()) {
                startDate = Date.valueOf(request.getParameter("startDate"));
            }
            
            Date endDate = null;
            if (request.getParameter("endDate") != null && !request.getParameter("endDate").isEmpty()) {
                endDate = Date.valueOf(request.getParameter("endDate"));
            }
            
            List<DriverTransaction> transactions = transactionDAO.findByDriverIdAndFilters(driverId, type, startDate, endDate);
            
            response.setContentType("text/html");
            
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = LocalDateTime.now().format(dtf);
            String fileName = "transaction_statement_" + timestamp + ".html";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            
            PrintWriter writer = response.getWriter();
            
 
            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<head>");
            writer.println("<meta charset=\"UTF-8\">");
            writer.println("<title>Transaction Statement</title>");
            writer.println("<style>");
            writer.println("body { font-family: Arial, sans-serif; margin: 40px; }");
            writer.println("h1 { color: #333366; }");
            writer.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            writer.println("th { background-color: #333366; color: white; padding: 10px; text-align: left; }");
            writer.println("td { padding: 8px; border-bottom: 1px solid #ddd; }");
            writer.println("tr:nth-child(even) { background-color: #f2f2f2; }");
            writer.println(".positive { color: green; }");
            writer.println(".negative { color: red; }");
            writer.println(".print-btn { background-color: #333366; color: white; padding: 10px 20px; ");
            writer.println("  border: none; border-radius: 4px; cursor: pointer; margin-top: 20px; }");
            writer.println("@media print {");
            writer.println("  .print-btn { display: none; }");
            writer.println("  body { margin: 0; }");
            writer.println("}");
            writer.println("</style>");
            writer.println("</head>");
            writer.println("<body>");
            
 
            writer.println("<h1>MegaCityCab - Transaction Statement</h1>");
            writer.println("<p><strong>Driver ID:</strong> " + driverId + "</p>");
            
    
            if (startDate != null && endDate != null) {
                writer.println("<p><strong>Period:</strong> " + startDate + " to " + endDate + "</p>");
            } else if (startDate != null) {
                writer.println("<p><strong>From:</strong> " + startDate + "</p>");
            } else if (endDate != null) {
                writer.println("<p><strong>Until:</strong> " + endDate + "</p>");
            }
            
  
            if (type != null && !type.isEmpty()) {
                writer.println("<p><strong>Transaction Type:</strong> " + type + "</p>");
            }
            

            writer.println("<table>");
            writer.println("<tr>");
            writer.println("<th>Transaction ID</th>");
            writer.println("<th>Date</th>");
            writer.println("<th>Type</th>");
            writer.println("<th>Amount (LKR)</th>");
            writer.println("<th>Description</th>");
            writer.println("</tr>");
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            for (DriverTransaction transaction : transactions) {
                writer.println("<tr>");
                
                writer.println("<td>" + transaction.getTransactionId() + "</td>");
                writer.println("<td>" + transaction.getDateTime().format(formatter) + "</td>");
                writer.println("<td>" + transaction.getTransactionType() + "</td>");
                

                String amountClass = "";
                String amountPrefix = "";
                BigDecimal amount = transaction.getAmount();
                
                if (transaction.getTransactionType().equals("Top Up") || 
                    transaction.getTransactionType().equals("Ride Earnings")) {
                    amountClass = "positive";
                    amountPrefix = "+";
                    totalAmount = totalAmount.add(amount);
                } else {
                    amountClass = "negative";
                    amountPrefix = "-";
                    totalAmount = totalAmount.subtract(amount);
                }
                
                writer.println("<td class=\"" + amountClass + "\">" + amountPrefix + amount + "</td>");
                
   
                String description = transaction.getDescription().replace("<", "&lt;").replace(">", "&gt;");
                writer.println("<td>" + description + "</td>");
                
                writer.println("</tr>");
            }
            

            writer.println("<tr>");
            writer.println("<td colspan=\"3\"><strong>Balance</strong></td>");
            String balanceClass = totalAmount.compareTo(BigDecimal.ZERO) >= 0 ? "positive" : "negative";
            String balancePrefix = totalAmount.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            writer.println("<td class=\"" + balanceClass + "\"><strong>" + balancePrefix + totalAmount + "</strong></td>");
            writer.println("<td></td>");
            writer.println("</tr>");
            
            writer.println("</table>");
            
   
            writer.println("<button class=\"print-btn\" onclick=\"window.print()\">Print Statement</button>");
            
    
            writer.println("<p style=\"margin-top: 40px; font-size: 12px; color: #666;\">Generated on: " 
                + LocalDateTime.now().format(formatter) + " by MegaCityCab System</p>");
            
 
            writer.println("</body>");
            writer.println("<script>");
            writer.println("// Auto download prompt");
            writer.println("if(window.navigator.msSaveOrOpenBlob) {");
            writer.println("  var blob = new Blob([document.documentElement.outerHTML], {type: 'text/html'});");
            writer.println("  window.navigator.msSaveOrOpenBlob(blob, '" + fileName + "');");
            writer.println("} else if(!window.location.href.includes('download=true')) {");
            writer.println("  window.print();");
            writer.println("}");
            writer.println("</script>");
            writer.println("</html>");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Failed to generate statement: " + e.getMessage() + "\"}");
        }
    }
}