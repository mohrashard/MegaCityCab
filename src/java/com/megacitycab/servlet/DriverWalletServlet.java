package com.megacitycab.servlet;

import com.megacitycab.dao.DriverWalletDAO;
import com.megacitycab.dao.DriverWalletDAOImpl;
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
import java.util.Optional;

@WebServlet("/driver/wallet")
public class DriverWalletServlet extends HttpServlet {
    
    private final DriverWalletDAO driverWalletDAO = new DriverWalletDAOImpl();
    
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
            Optional<DriverWallet> walletOpt = driverWalletDAO.findByDriverId(driverId);
            
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");
            
            if (walletOpt.isPresent()) {
                DriverWallet wallet = walletOpt.get();
                jsonBuilder.append("\"success\": true,");
                jsonBuilder.append("\"wallet\": {");
                jsonBuilder.append("\"driverId\": ").append(wallet.getDriverId()).append(",");
                jsonBuilder.append("\"walletBalance\": ").append(wallet.getWalletBalance()).append(",");
                jsonBuilder.append("\"totalEarnings\": ").append(wallet.getTotalEarnings()).append(",");
                jsonBuilder.append("\"totalExpenses\": ").append(wallet.getTotalExpenses());
                jsonBuilder.append("}");
            } else {
                // Create new wallet for driver if not exists
                DriverWallet newWallet = new DriverWallet();
                newWallet.setDriverId(driverId);
                newWallet.setWalletBalance(BigDecimal.ZERO);
                newWallet.setTotalEarnings(BigDecimal.ZERO);
                newWallet.setTotalExpenses(BigDecimal.ZERO);
                
                if (driverWalletDAO.save(newWallet)) {
                    jsonBuilder.append("\"success\": true,");
                    jsonBuilder.append("\"wallet\": {");
                    jsonBuilder.append("\"driverId\": ").append(newWallet.getDriverId()).append(",");
                    jsonBuilder.append("\"walletBalance\": ").append(newWallet.getWalletBalance()).append(",");
                    jsonBuilder.append("\"totalEarnings\": ").append(newWallet.getTotalEarnings()).append(",");
                    jsonBuilder.append("\"totalExpenses\": ").append(newWallet.getTotalExpenses());
                    jsonBuilder.append("}");
                } else {
                    jsonBuilder.append("\"success\": false,");
                    jsonBuilder.append("\"message\": \"Failed to initialize wallet\"");
                }
            }
            
            jsonBuilder.append("}");
            out.print(jsonBuilder.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }
}