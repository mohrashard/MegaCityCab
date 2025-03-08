package com.megacitycab.servlet;

import com.megacitycab.dao.DriverCardDAO;
import com.megacitycab.dao.DriverCardDAOImpl;
import com.megacitycab.dao.DriverTransactionDAO;
import com.megacitycab.dao.DriverTransactionDAOImpl;
import com.megacitycab.dao.DriverWalletDAO;
import com.megacitycab.dao.DriverWalletDAOImpl;
import com.megacitycab.model.DriverCard;
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

@WebServlet("/driverTopup")
public class DriverTopUpServlet extends HttpServlet {

    private final DriverWalletDAO walletDAO = new DriverWalletDAOImpl();
    private final DriverCardDAO cardDAO = new DriverCardDAOImpl();
    private final DriverTransactionDAO transactionDAO = new DriverTransactionDAOImpl();
    
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
            // Get and validate the top-up amount
            String amountStr = request.getParameter("amount");
            if (amountStr == null || amountStr.isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Amount is required.\"}");
                return;
            }
            
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    out.print("{\"success\": false, \"message\": \"Amount must be greater than zero.\"}");
                    return;
                }
            } catch (NumberFormatException e) {
                out.print("{\"success\": false, \"message\": \"Invalid amount format.\"}");
                return;
            }
            
            // Get card details
            String cardIdStr = request.getParameter("cardId");
            if (cardIdStr == null || cardIdStr.isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Card selection is required.\"}");
                return;
            }
            
            int cardId;
            try {
                cardId = Integer.parseInt(cardIdStr);
            } catch (NumberFormatException e) {
                out.print("{\"success\": false, \"message\": \"Invalid card selection.\"}");
                return;
            }
            
            // Check if the card belongs to the driver
// In DriverTopUpServlet's card check
Optional<DriverCard> cardOpt = cardDAO.findById(cardId);
if (!cardOpt.isPresent()) {
    out.print("{\"success\": false, \"message\": \"Invalid payment method.\"}");
    return;
}
if (cardOpt.get().getDriverId() != driverId) {
    out.print("{\"success\": false, \"message\": \"Unauthorized card usage.\"}");
    return;
}
            
            // Process top-up
            // First check if wallet exists, if not create it
            Optional<DriverWallet> walletOpt = walletDAO.findByDriverId(driverId);
            DriverWallet wallet;
            
            if (!walletOpt.isPresent()) {
                wallet = new DriverWallet();
                wallet.setDriverId(driverId);
                wallet.setWalletBalance(BigDecimal.ZERO);
                wallet.setTotalEarnings(BigDecimal.ZERO);
                wallet.setTotalExpenses(BigDecimal.ZERO);
                
                
                    if (!walletDAO.save(wallet)) {
        out.print("{\"success\": false, \"message\": \"Failed to initialize wallet. Contact support.\"}");
        return;
    }
                walletDAO.save(wallet);
            }
            
            // Update wallet balance
            boolean updateSuccess = walletDAO.updateBalance(driverId, amount, true);
            
            if (updateSuccess) {
                // Record the transaction
                DriverTransaction transaction = new DriverTransaction();
                transaction.setDriverId(driverId);
                transaction.setTransactionType("Top Up");
                transaction.setAmount(amount);
                transaction.setDescription("Top up using card ending with " + 
                                          cardOpt.get().getCardNumber().substring(cardOpt.get().getCardNumber().length() - 4));
                transaction.setDateTime(LocalDateTime.now());
                
                boolean txnSuccess = transactionDAO.save(transaction);
                
                if (txnSuccess) {
                    out.print("{\"success\": true, \"message\": \"Top up successful. Amount added to your wallet.\"}");
                } else {
                    // This is a critical situation where wallet is updated but transaction record failed
                    out.print("{\"success\": true, \"message\": \"Top up processed but transaction recording failed. Please contact support.\"}");
                }
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to process top up. Please try again later.\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }
}