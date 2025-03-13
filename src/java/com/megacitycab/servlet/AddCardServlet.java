package com.megacitycab.servlet;

import com.megacitycab.model.Card;
import com.megacitycab.service.CardService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import org.json.JSONObject;

@WebServlet("/addCard")
public class AddCardServlet extends HttpServlet {

    private CardService cardService = new CardService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Integer passengerId = (Integer) request.getSession().getAttribute("userId");
        if (passengerId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": \"User not logged in or invalid session.\"}");
            return;
        }


        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        JSONObject json = new JSONObject(sb.toString());


        String cardNumber = json.getString("number");
        String cardholderName = json.getString("holder");
        String expiryDate = json.getString("expiry");
        String cvc = json.getString("cvv");

    
        Card card = new Card();
        card.setPassengerId(passengerId); 
        card.setCardNumber(cardNumber);
        card.setCardholderName(cardholderName);
        card.setExpiryDate(expiryDate);
        card.setCvc(cvc);

       
        cardService.addCard(card);


        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"Card added successfully\"}");
    }
}
