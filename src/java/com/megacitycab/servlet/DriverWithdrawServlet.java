package com.megacitycab.servlet;

import com.megacitycab.dao.DriverBankDAO;
import com.megacitycab.dao.DriverBankDAOImpl;
import com.megacitycab.dao.DriverTransactionDAO;
import com.megacitycab.dao.DriverTransactionDAOImpl;
import com.megacitycab.dao.DriverWalletDAO;
import com.megacitycab.dao.DriverWalletDAOImpl;
import com.megacitycab.model.DriverBank;
import com.megacitycab.model.DriverTransaction;
import com.megacitycab.model.DriverWallet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@WebServlet("/driver/withdraw")
public class DriverWithdrawServlet extends HttpServlet {

    private final DriverWalletDAO walletDAO = new DriverWalletDAOImpl();
    private final DriverBankDAO bankDAO = new DriverBankDAOImpl();
    private final DriverTransactionDAO transactionDAO = new DriverTransactionDAOImpl();
    
    // Constants for withdrawal limits
    private static final BigDecimal MIN_WITHDRAWAL = new BigDecimal("100");
    private static final BigDecimal MAX_WITHDRAWAL = new BigDecimal("1000000");
    
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
        
        try {
            // Get and validate the withdrawal amount
            String amountStr = request.getParameter("amount");
            if (amountStr == null || amountStr.isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Amount is required.\"}");
                return;
            }
            
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
                
                // Check minimum and maximum withdrawal limits
                if (amount.compareTo(MIN_WITHDRAWAL) < 0) {
                    out.print("{\"success\": false, \"message\": \"Minimum withdrawal amount is LKR " + MIN_WITHDRAWAL + ".\"}");
                    return;
                }
                
                if (amount.compareTo(MAX_WITHDRAWAL) > 0) {
                    out.print("{\"success\": false, \"message\": \"Maximum withdrawal amount is LKR " + MAX_WITHDRAWAL + ".\"}");
                    return;
                }
            } catch (NumberFormatException e) {
                out.print("{\"success\": false, \"message\": \"Invalid amount format.\"}");
                return;
            }
            
            // Get bank details
            String bankIdStr = request.getParameter("bankId");
            if (bankIdStr == null || bankIdStr.isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Bank account selection is required.\"}");
                return;
            }
            
            int bankId;
            try {
                bankId = Integer.parseInt(bankIdStr);
            } catch (NumberFormatException e) {
                out.print("{\"success\": false, \"message\": \"Invalid bank account selection.\"}");
                return;
            }
            
            // Check if the bank account belongs to the driver
            Optional<DriverBank> bankOpt = bankDAO.findById(bankId);
            if (!bankOpt.isPresent() || bankOpt.get().getDriverId() != driverId) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"message\": \"You don't have permission to use this bank account.\"}");
                return;
            }
            
            // Check if wallet exists and has sufficient balance
            Optional<DriverWallet> walletOpt = walletDAO.findByDriverId(driverId);
            if (!walletOpt.isPresent()) {
                out.print("{\"success\": false, \"message\": \"Wallet not found.\"}");
                return;
            }
            
            DriverWallet wallet = walletOpt.get();
            if (wallet.getWalletBalance().compareTo(amount) < 0) {
                out.print("{\"success\": false, \"message\": \"Insufficient balance.\"}");
                return;
            }
            
            // Process withdrawal
            boolean updateSuccess = walletDAO.updateBalance(driverId, amount, false);
            
            if (updateSuccess) {
                // Record the transaction
                DriverTransaction transaction = new DriverTransaction();
                transaction.setDriverId(driverId);
                transaction.setTransactionType("Withdrawal");
                transaction.setAmount(amount);
                
                // Mask account number for security in the description
                String accountNumber = bankOpt.get().getAccountNumber();
                String maskedAccount = accountNumber.length() > 4 ? 
                    "XXXX" + accountNumber.substring(accountNumber.length() - 4) : accountNumber;
                
                transaction.setDescription("Withdrawal to " + bankOpt.get().getBankName() + 
                                          " account ending with " + maskedAccount);
                transaction.setDateTime(LocalDateTime.now());
                
                boolean txnSuccess = transactionDAO.save(transaction);
                
                if (txnSuccess) {
                    out.print("{\"success\": true, \"message\": \"Withdrawal successful. Money will be transferred to your bank account.\"}");
                } else {
                    // This is a critical situation where wallet is updated but transaction record failed
                    out.print("{\"success\": true, \"message\": \"Withdrawal processed but transaction recording failed. Please contact support.\"}");
                }
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to process withdrawal. Please try again later.\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }
}