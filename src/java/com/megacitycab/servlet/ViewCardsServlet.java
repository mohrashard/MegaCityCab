package com.megacitycab.servlet;

import com.megacitycab.model.Card;
import com.megacitycab.service.CardService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/viewCards")
public class ViewCardsServlet extends HttpServlet {

    private CardService cardService = new CardService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        Object passengerIdObj = request.getSession().getAttribute("userId"); 

        if (passengerIdObj == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\":\"User not logged in.\"}");
            return;
        }


        int passengerId = (int) passengerIdObj;
        

        List<Card> cards = cardService.getCardsByPassengerId(passengerId);

        JSONArray jsonArray = new JSONArray();
        for (Card card : cards) {
            JSONObject json = new JSONObject();
            json.put("id", card.getCardId());
            json.put("number", card.getCardNumber());
            json.put("holder", card.getCardholderName());
            json.put("expiry", card.getExpiryDate());
            json.put("cvv", card.getCvc());
            jsonArray.put(json);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonArray.toString());
    }
}
