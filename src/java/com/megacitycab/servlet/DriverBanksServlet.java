package com.megacitycab.servlet;

import com.megacitycab.dao.DriverBankDAO;
import com.megacitycab.dao.DriverBankDAOImpl;
import com.megacitycab.model.DriverBank;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

@WebServlet("/api/drivers/banks/*")  
public class DriverBanksServlet extends HttpServlet {

    private final DriverBankDAO bankDAO = new DriverBankDAOImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !session.getAttribute("userType").equals("driver")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        int driverId = (int) session.getAttribute("userId");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
      
            List<DriverBank> banks = bankDAO.findByDriverId(driverId);
     
        
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");
            jsonBuilder.append("\"success\": true,");
            jsonBuilder.append("\"banks\": [");
            
            for (int i = 0; i < banks.size(); i++) {
                DriverBank bank = banks.get(i);
                jsonBuilder.append("{");
                jsonBuilder.append("\"bankId\": ").append(bank.getBankId()).append(",");
                jsonBuilder.append("\"driverId\": ").append(bank.getDriverId()).append(",");
                jsonBuilder.append("\"bankName\": \"").append(bank.getBankName()).append("\",");
                
                // Mask account number for security
                String maskedAccount = maskAccountNumber(bank.getAccountNumber());
                jsonBuilder.append("\"accountNumber\": \"").append(maskedAccount).append("\",");
                
                jsonBuilder.append("\"accountHolderName\": \"").append(bank.getAccountHolderName()).append("\",");
                jsonBuilder.append("\"ifscCode\": \"").append(bank.getIfscCode()).append("\",");
                jsonBuilder.append("\"isDefault\": ").append(bank.isDefault());
                jsonBuilder.append("}");
                
            
                
                if (i < banks.size() - 1) {
                    jsonBuilder.append(",");
                }
            }
            
            jsonBuilder.append("]");
            jsonBuilder.append("}");
            
            out.print(jsonBuilder.toString());
            
        }else {

            String[] parts = pathInfo.split("/");
            if (parts.length < 2) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Invalid bank ID format\"}");
                return;
            }
            
            
              int bankId = Integer.parseInt(parts[1]);
            Optional<DriverBank> bankOpt = bankDAO.findById(bankId);
            
            if (!bankOpt.isPresent() || bankOpt.get().getDriverId() != driverId) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Bank account not found\"}");
                return;
            }
            
            DriverBank bank = bankOpt.get();
            out.print("{\"success\": true, \"bank\": " + convertBankToJson(bank) + "}");
        }
        }
        catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !session.getAttribute("userType").equals("driver")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        int driverId = (int) session.getAttribute("userId");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        // Check if driver already has 3 bank accounts
        int bankCount = bankDAO.countByDriverId(driverId);
        if (bankCount >= 3) {
            out.print("{\"success\": false, \"message\": \"Maximum bank account limit reached (3 accounts).\"}");
            return;
        }
        
        try {
            String bankName = request.getParameter("bankName");
            String accountNumber = request.getParameter("accountNumber");
            String accountHolderName = request.getParameter("accountHolderName");
            String ifscCode = request.getParameter("ifscCode");
            boolean isDefault = Boolean.parseBoolean(request.getParameter("isDefault"));
            
            // Validate inputs
            if (bankName == null || bankName.isEmpty() || 
                accountNumber == null || accountNumber.isEmpty() ||
                accountHolderName == null || accountHolderName.isEmpty() ||
                ifscCode == null || ifscCode.isEmpty()) {
                
                out.print("{\"success\": false, \"message\": \"All fields are required.\"}");
                return;
            }
            if (!accountNumber.matches("\\d{9,18}")) {
    out.print("{\"success\": false, \"message\": \"Invalid account number format\"}");
    return;
}
            
            // Create and save bank account
            DriverBank bank = new DriverBank();
            bank.setDriverId(driverId);
            bank.setBankName(bankName);
            bank.setAccountNumber(accountNumber);
            bank.setAccountHolderName(accountHolderName);
            bank.setIfscCode(ifscCode);
            bank.setDefault(isDefault);
            
            boolean success = bankDAO.save(bank);
            
            if (success) {
                out.print("{\"success\": " + success + ", \"message\": \"" + (success ? "Bank added" : "Error") + "\"}");
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to add bank account.\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }
    
@Override
protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("userId") == null || !session.getAttribute("userType").equals("driver")) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
    }
    
    int driverId = (int) session.getAttribute("userId");
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    
    try {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Bank account ID is required.\"}");
            return;
        }
        
        int bankId = Integer.parseInt(pathInfo.substring(1));
        
        // Check if bank account belongs to the driver
        Optional<DriverBank> bankOpt = bankDAO.findById(bankId);
        if (!bankOpt.isPresent() || bankOpt.get().getDriverId() != driverId) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"success\": false, \"message\": \"You don't have permission to update this bank account.\"}");
            return;
        }
        
        DriverBank bank = bankOpt.get();
        
        // Debug incoming parameters
        Enumeration<String> paramNames = request.getParameterNames();
        System.out.println("--- Incoming Parameters ---");
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            System.out.println(paramName + ": " + request.getParameter(paramName));
        }
        System.out.println("-------------------------");
        
        // Get updated values
        String bankName = request.getParameter("bankName");
        String accountNumber = request.getParameter("accountNumber");
        String accountHolderName = request.getParameter("accountHolderName");
        String ifscCode = request.getParameter("ifscCode");
        
        // Improved boolean parsing with detailed logging
        boolean isDefault = false;
        String isDefaultParam = request.getParameter("isDefault");
        System.out.println("Raw isDefault parameter: " + isDefaultParam);
        
        if (isDefaultParam != null) {
            // Handle various true values
            if (isDefaultParam.equalsIgnoreCase("true") || 
                isDefaultParam.equals("1") || 
                isDefaultParam.equalsIgnoreCase("yes") || 
                isDefaultParam.equalsIgnoreCase("on")) {
                isDefault = true;
            }
        }
        
        System.out.println("Parsed isDefault value: " + isDefault);
        
        // Update fields with validation
        if (bankName != null && !bankName.trim().isEmpty()) {
            bank.setBankName(bankName.trim());
        }
        
        if (accountNumber != null && !accountNumber.trim().isEmpty()) {
            bank.setAccountNumber(accountNumber.trim());
        }
        
        if (accountHolderName != null && !accountHolderName.trim().isEmpty()) {
            bank.setAccountHolderName(accountHolderName.trim());
        }
        
        if (ifscCode != null && !ifscCode.trim().isEmpty()) {
            bank.setIfscCode(ifscCode.trim());
        }
        
        // Set default status
        bank.setDefault(isDefault);
        
        // Add detailed debugging
        System.out.println("Updating bank with ID: " + bankId);
        System.out.println("driverId: " + bank.getDriverId());
        System.out.println("bankName: " + bank.getBankName());
        System.out.println("accountNumber: " + bank.getAccountNumber());
        System.out.println("accountHolderName: " + bank.getAccountHolderName());
        System.out.println("ifscCode: " + bank.getIfscCode());
        System.out.println("isDefault (final): " + bank.isDefault());
        
        boolean success = bankDAO.update(bank);
        System.out.println("Update operation result: " + success);
        
        if (success) {
            out.print("{\"success\": true, \"message\": \"Bank account updated successfully.\"}");
        } else {
            out.print("{\"success\": false, \"message\": \"Failed to update bank account.\"}");
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Exception in doPut: " + e.getMessage());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
    }
}
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !session.getAttribute("userType").equals("driver")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        int driverId = (int) session.getAttribute("userId");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Bank account ID is required.\"}");
                return;
            }
            
            int bankId = Integer.parseInt(pathInfo.substring(1));
            
            // Check if bank account belongs to the driver
            Optional<DriverBank> bankOpt = bankDAO.findById(bankId);
            if (!bankOpt.isPresent() || bankOpt.get().getDriverId() != driverId) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"message\": \"You don't have permission to delete this bank account.\"}");
                return;
            }
            
            boolean success = bankDAO.delete(bankId);
            
            if (success) {
                out.print("{\"success\": true, \"message\": \"Bank account deleted successfully.\"}");
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to delete bank account.\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getMethod();
        if (method.equals("PATCH")) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }
    
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !session.getAttribute("userType").equals("driver")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        int driverId = (int) session.getAttribute("userId");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Bank account ID is required.\"}");
                return;
            }
            
            int bankId = Integer.parseInt(pathInfo.substring(1));
            
            // Check if bank account belongs to the driver
            Optional<DriverBank> bankOpt = bankDAO.findById(bankId);
            if (!bankOpt.isPresent() || bankOpt.get().getDriverId() != driverId) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"message\": \"You don't have permission to modify this bank account.\"}");
                return;
            }
            
            // Set as default bank
            boolean success = bankDAO.setDefaultBank(driverId, bankId);
            
            if (success) {
                out.print("{\"success\": true, \"message\": \"Default bank account updated successfully.\"}");
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to update default bank account.\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }
    
    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return accountNumber;
        }
        
        // Show only the last 4 digits
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < accountNumber.length() - 4; i++) {
            masked.append('X');
        }
        masked.append(accountNumber.substring(accountNumber.length() - 4));
        return masked.toString();
    }
    
    
    private String convertBankToJson(DriverBank bank) {
    return String.format(
        "{\"bankId\": %d, \"bankName\": \"%s\", \"accountNumber\": \"%s\", " +
        "\"accountHolderName\": \"%s\", \"ifscCode\": \"%s\", \"isDefault\": %s}",
        bank.getBankId(),
        bank.getBankName(),
        bank.getAccountNumber(),
        bank.getAccountHolderName(),
        bank.getIfscCode(),
        bank.isDefault()
    );
}
    
}