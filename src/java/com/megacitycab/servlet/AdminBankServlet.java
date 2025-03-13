package com.megacitycab.servlet;

import com.megacitycab.model.AdminBank;
import com.megacitycab.service.AdminBankService;
import com.megacitycab.service.AdminBankServiceImpl;

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
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/banks")
public class AdminBankServlet extends HttpServlet {
    private final AdminBankService adminBankService = new AdminBankServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    Integer adminId = (Integer) session.getAttribute("adminId");
    
    if (adminId == null) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print("{\"success\": false, \"message\": \"Unauthorized\"}");
        return;
    }
        String pathInfo = request.getPathInfo();
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
   
                List<AdminBank> banks = adminBankService.getAllBanksByAdminId(adminId);
                
                StringBuilder jsonResponse = new StringBuilder();
                jsonResponse.append("{\"success\": true, \"banks\": [");
                
                for (int i = 0; i < banks.size(); i++) {
                    AdminBank bank = banks.get(i);
                    jsonResponse.append("{")
                        .append("\"bankId\": ").append(bank.getBankId()).append(",")
                        .append("\"adminId\": ").append(bank.getAdminId()).append(",")
                        .append("\"bankName\": \"").append(bank.getBankName()).append("\",")
                        .append("\"accountNumber\": \"").append(bank.getAccountNumber()).append("\",")
                        .append("\"accountHolder\": \"").append(bank.getAccountHolder()).append("\",")
                        .append("\"branch\": \"").append(bank.getBranch()).append("\",")
                        .append("\"createdAt\": \"").append(bank.getCreatedAt()).append("\",")
                        .append("\"updatedAt\": \"").append(bank.getUpdatedAt()).append("\"")
                        .append("}");
                    
                    if (i < banks.size() - 1) {
                        jsonResponse.append(",");
                    }
                }
                
                jsonResponse.append("]}");
                out.print(jsonResponse.toString());
            } else {
  
                try {
                    int bankId = Integer.parseInt(pathInfo.substring(1));
                    AdminBank bank = adminBankService.getBankById(bankId);
                    
                    if (bank != null && bank.getAdminId() == adminId) {
                        String jsonResponse = "{" +
                            "\"success\": true," +
                            "\"bankId\": " + bank.getBankId() + "," +
                            "\"adminId\": " + bank.getAdminId() + "," +
                            "\"bankName\": \"" + bank.getBankName() + "\"," +
                            "\"accountNumber\": \"" + bank.getAccountNumber() + "\"," +
                            "\"accountHolder\": \"" + bank.getAccountHolder() + "\"," +
                            "\"branch\": \"" + bank.getBranch() + "\"," +
                            "\"createdAt\": \"" + bank.getCreatedAt() + "\"," +
                            "\"updatedAt\": \"" + bank.getUpdatedAt() + "\"" +
                        "}";
                        
                        out.print(jsonResponse);
                    } else {
                        out.print("{\"success\": false, \"message\": \"Bank not found.\"}");
                    }
                } catch (NumberFormatException e) {
                    out.print("{\"success\": false, \"message\": \"Invalid bank ID.\"}");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }

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
            String bankName = extractValueFromJson(body, "bankName");
            String accountNumber = extractValueFromJson(body, "accountNumber");
            String accountHolder = extractValueFromJson(body, "accountHolder");
            String branch = extractValueFromJson(body, "branch");
            
            // Create and add bank
            AdminBank bank = new AdminBank();
            bank.setAdminId(adminId);
            bank.setBankName(bankName);
            bank.setAccountNumber(accountNumber);
            bank.setAccountHolder(accountHolder);
            bank.setBranch(branch);
            
            if (adminBankService.canAddBank(adminId)) {
                boolean success = adminBankService.addBank(bank);
                
                if (success) {
                    out.print("{\"success\": true, \"message\": \"Bank added successfully.\"}");
                } else {
                    out.print("{\"success\": false, \"message\": \"Failed to add bank.\"}");
                }
            } else {
                out.print("{\"success\": false, \"message\": \"You cannot add more than 3 banks.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    Integer adminId = (Integer) session.getAttribute("adminId");
    
    if (adminId == null) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print("{\"success\": false, \"message\": \"Unauthorized\"}");
        return;
    }
        String pathInfo = request.getPathInfo();
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            out.print("{\"success\": false, \"message\": \"Bank ID is required.\"}");
            return;
        }
        
        try {
            int bankId = Integer.parseInt(pathInfo.substring(1));
            
            // Verify the bank belongs to the admin
            AdminBank existingBank = adminBankService.getBankById(bankId);
            if (existingBank == null || existingBank.getAdminId() != adminId) {
                out.print("{\"success\": false, \"message\": \"Bank not found or access denied.\"}");
                return;
            }
            
            // Read request body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            // Parse request body manually
            String body = sb.toString();
            String bankName = extractValueFromJson(body, "bankName");
            String accountNumber = extractValueFromJson(body, "accountNumber");
            String accountHolder = extractValueFromJson(body, "accountHolder");
            String branch = extractValueFromJson(body, "branch");
            
            // Update bank
            AdminBank bank = new AdminBank();
            bank.setBankId(bankId);
            bank.setAdminId(adminId);
            bank.setBankName(bankName);
            bank.setAccountNumber(accountNumber);
            bank.setAccountHolder(accountHolder);
            bank.setBranch(branch);
            
            boolean success = adminBankService.updateBank(bank);
            
            if (success) {
                out.print("{\"success\": true, \"message\": \"Bank updated successfully.\"}");
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to update bank.\"}");
            }
        } catch (NumberFormatException e) {
            out.print("{\"success\": false, \"message\": \"Invalid bank ID.\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }

@Override
protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    Integer adminId = (Integer) session.getAttribute("adminId");
    
    if (adminId == null) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print("{\"success\": false, \"message\": \"Unauthorized\"}");
        return;
    }
    
    String pathInfo = request.getPathInfo();
    
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();
    
    if (pathInfo == null || pathInfo.equals("/")) {
        out.print("{\"success\": false, \"message\": \"Bank ID is required.\"}");
        return;
    }
    
    try {
        int bankId = Integer.parseInt(pathInfo.substring(1));
        
        // Verify the bank belongs to the admin
        AdminBank existingBank = adminBankService.getBankById(bankId);
        if (existingBank == null || existingBank.getAdminId() != adminId) {
            out.print("{\"success\": false, \"message\": \"Bank not found or access denied.\"}");
            return;
        }
        
        boolean success = adminBankService.deleteBank(bankId);
        
        if (success) {
            out.print("{\"success\": true, \"message\": \"Bank deleted successfully.\"}");
        } else {
            out.print("{\"success\": false, \"message\": \"Failed to delete bank.\"}");
        }
    } catch (NumberFormatException e) {
        out.print("{\"success\": false, \"message\": \"Invalid bank ID.\"}");
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