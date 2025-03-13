package com.megacitycab.servlet;

import com.megacitycab.dao.DriverCardDAO;
import com.megacitycab.dao.DriverCardDAOImpl;
import com.megacitycab.model.DriverCard;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet("/driverCards")
public class DriversCardServlet extends HttpServlet {

    private final DriverCardDAO cardDAO = new DriverCardDAOImpl();
    
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
        
        // Handle single card request
        if (pathInfo != null && !pathInfo.equals("/")) {
            String[] parts = pathInfo.split("/");
            if (parts.length != 2) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Invalid URL format.\"}");
                return;
            }
            
            try {
                int cardId = Integer.parseInt(parts[1]);
                Optional<DriverCard> cardOpt = cardDAO.findById(cardId);
                
                if (!cardOpt.isPresent() || cardOpt.get().getDriverId() != driverId) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"success\": false, \"message\": \"Card not found.\"}");
                    return;
                }
                
                DriverCard card = cardOpt.get();
                String maskedNumber = maskCardNumber(card.getCardNumber());
                
                out.print("{\"success\": true, \"card\": {" +
                        "\"cardId\": " + card.getCardId() + "," +
                        "\"driverId\": " + card.getDriverId() + "," +
                        "\"cardNumber\": \"" + maskedNumber + "\"," +
                        "\"expiryDate\": \"" + card.getExpiryDate() + "\"," +
                        "\"cardholderName\": \"" + card.getCardholderName() + "\"," +
                        "\"isDefault\": " + card.isDefault() + "}}");
                return;
                
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Invalid card ID.\"}");
                return;
            }
        }
        
        // Existing code to return all cards
        List<DriverCard> cards = cardDAO.findByDriverId(driverId);
        
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"success\": true,");
        jsonBuilder.append("\"cards\": [");
        
        for (int i = 0; i < cards.size(); i++) {
            DriverCard card = cards.get(i);
            jsonBuilder.append("{");
            jsonBuilder.append("\"cardId\": ").append(card.getCardId()).append(",");
            jsonBuilder.append("\"driverId\": ").append(card.getDriverId()).append(",");
            String maskedNumber = maskCardNumber(card.getCardNumber());
            jsonBuilder.append("\"cardNumber\": \"").append(maskedNumber).append("\",");
            jsonBuilder.append("\"expiryDate\": \"").append(card.getExpiryDate()).append("\",");
            jsonBuilder.append("\"cardholderName\": \"").append(card.getCardholderName()).append("\",");
            jsonBuilder.append("\"isDefault\": ").append(card.isDefault());
            jsonBuilder.append("}");
            if (i < cards.size() - 1) jsonBuilder.append(",");
        }
        
        jsonBuilder.append("]}");
        out.print(jsonBuilder.toString());
        
    } catch (Exception e) {
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
        
        // Check if driver already has 3 cards
        int cardCount = cardDAO.countByDriverId(driverId);
        if (cardCount >= 3) {
            out.print("{\"success\": false, \"message\": \"Maximum card limit reached (3 cards).\"}");
            return;
        }
        
        try {
            String cardNumber = request.getParameter("cardNumber");
            String expiryDate = request.getParameter("expiryDate");
            String cvv = request.getParameter("cvv");
            String cardholderName = request.getParameter("cardholderName");
            boolean isDefault = Boolean.parseBoolean(request.getParameter("isDefault"));
            
            // Validate inputs
            if (cardNumber == null || cardNumber.isEmpty() || 
                expiryDate == null || expiryDate.isEmpty() ||
                cvv == null || cvv.isEmpty() ||
                cardholderName == null || cardholderName.isEmpty()) {
                
                out.print("{\"success\": false, \"message\": \"All fields are required.\"}");
                return;
            }
            
            
            
            // Create and save card
            DriverCard card = new DriverCard();
            card.setDriverId(driverId);
            card.setCardNumber(cardNumber);
            card.setExpiryDate(expiryDate);
            card.setCvv(cvv);
            card.setCardholderName(cardholderName);
            card.setDefault(isDefault);
            
boolean success = cardDAO.save(card);
            
if (success) {
    out.print("{\"success\": true, \"message\": \"Card added successfully\", \"cardId\": " + card.getCardId() + "}");
} else {
    out.print("{\"success\": false, \"message\": \"Failed to add card. Please try again.\"}");
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
                out.print("{\"success\": false, \"message\": \"Card ID is required.\"}");
                return;
            }
            
            int cardId = Integer.parseInt(pathInfo.substring(1));
            
            // Check if card belongs to the driver
            Optional<DriverCard> cardOpt = cardDAO.findById(cardId);
            if (!cardOpt.isPresent() || cardOpt.get().getDriverId() != driverId) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"message\": \"You don't have permission to update this card.\"}");
                return;
            }
            
            DriverCard card = cardOpt.get();
            
            // Update only the allowed fields
            String expiryDate = request.getParameter("expiryDate");
            String cvv = request.getParameter("cvv");
            String cardholderName = request.getParameter("cardholderName");
            boolean isDefault = Boolean.parseBoolean(request.getParameter("isDefault"));
            
            if (expiryDate != null && !expiryDate.isEmpty()) {
                card.setExpiryDate(expiryDate);
            }
            
            if (cvv != null && !cvv.isEmpty()) {
                card.setCvv(cvv);
            }
            
            if (cardholderName != null && !cardholderName.isEmpty()) {
                card.setCardholderName(cardholderName);
            }
            
            card.setDefault(isDefault);
            
            boolean success = cardDAO.update(card);
            
            if (success) {
                out.print("{\"success\": true, \"message\": \"Card updated successfully.\"}");
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to update card.\"}");
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Invalid card ID.\"}");
        } catch (Exception e) {
            e.printStackTrace();
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
                out.print("{\"success\": false, \"message\": \"Card ID is required.\"}");
                return;
            }
            
            int cardId = Integer.parseInt(pathInfo.substring(1));
            
            // Check if card belongs to the driver
            Optional<DriverCard> cardOpt = cardDAO.findById(cardId);
            if (!cardOpt.isPresent() || cardOpt.get().getDriverId() != driverId) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"message\": \"You don't have permission to delete this card.\"}");
                return;
            }
            
            boolean success = cardDAO.delete(cardId);
            
            if (success) {
                out.print("{\"success\": true, \"message\": \"Card deleted successfully.\"}");
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to delete card.\"}");
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Invalid card ID.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }
    
    // Helper method to mask card number
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }
        
        int length = cardNumber.length();
        StringBuilder masked = new StringBuilder();
        
        // Add asterisks for all digits except last 4
        for (int i = 0; i < length - 4; i++) {
            masked.append("*");
        }
        
        // Add last 4 digits
        masked.append(cardNumber.substring(length - 4));
        
        return masked.toString();
    }
}