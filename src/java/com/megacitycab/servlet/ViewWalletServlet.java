package com.megacitycab.servlet;

import com.megacitycab.service.WalletService;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/viewWallet")
public class ViewWalletServlet extends HttpServlet {
    private final WalletService walletService = new WalletService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Integer passengerId = (Integer) request.getSession().getAttribute("userId");

        if (passengerId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Not authenticated\"}");
            return;
        }

        try {
            BigDecimal balance = walletService.getWalletBalance(passengerId);
            JSONObject json = new JSONObject();
            json.put("balance", balance);
            response.getWriter().write(json.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Error retrieving wallet balance: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}