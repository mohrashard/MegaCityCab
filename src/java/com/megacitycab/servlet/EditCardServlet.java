package com.megacitycab.servlet;

import com.megacitycab.model.Card;
import com.megacitycab.service.CardService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

@WebServlet("/editCard")
public class EditCardServlet extends HttpServlet {

    private CardService cardService = new CardService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userType") == null || !session.getAttribute("userType").equals("passenger")) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\": \"User is not logged in as a passenger.\"}");
            return; 
        }


        int passengerId = (int) session.getAttribute("userId");


        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        JSONObject json = new JSONObject(sb.toString());

        try {
        int cardId = json.getInt("cardId");
        String cardholderName = json.getString("cardholderName");
        String expiryDate = json.getString("expiryDate");


        if (!cardService.checkCardOwner(cardId, passengerId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"message\": \"Unauthorized card access\"}");
            return;
        }

        Card card = new Card();
        card.setCardId(cardId);
        card.setCardholderName(cardholderName);
        card.setExpiryDate(expiryDate);
        card.setPassengerId(passengerId);

        cardService.updateCard(card);
        response.getWriter().write("{\"message\": \"Card updated successfully\"}");
        
    } catch (JSONException e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"message\": \"Invalid request format\"}");
    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("{\"message\": \"Server error occurred\"}");
    }
}
}
