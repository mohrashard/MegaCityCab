package com.megacitycab.servlet;

import com.megacitycab.dao.CardDAO;
import com.megacitycab.model.Card;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/viewCards/pay")
public class ViewCards extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer passengerId = (Integer) session.getAttribute("userId");
        
        if (passengerId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
            return;
        }
        
        CardDAO cardDAO = new CardDAO();
        List<Card> cards = cardDAO.getCardsByPassengerId(passengerId);
        
 
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            json.append("{")
                .append("\"cardId\":").append(card.getCardId()).append(",")
                .append("\"cardholderName\":\"").append(card.getCardholderName()).append("\",")
                .append("\"cardNumber\":\"").append(card.getCardNumber()).append("\",")
                .append("\"expiryDate\":\"").append(card.getExpiryDate()).append("\"")
                .append("}");
            if (i < cards.size() - 1) json.append(",");
        }
        json.append("]");
        
        response.setContentType("application/json");
        response.getWriter().write(json.toString());
    }
}