package com.megacitycab.servlet;

import com.megacitycab.service.CardService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import org.json.JSONObject;

@WebServlet("/deleteCard")
public class DeleteCardServlet extends HttpServlet {

    private CardService cardService = new CardService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("userType") != null && session.getAttribute("userType").equals("passenger")) {

            int userId = (int) session.getAttribute("userId");

    
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }


            JSONObject json = new JSONObject(sb.toString());
            int cardId = json.getInt("cardId");

           
            boolean isCardOwner = cardService.checkCardOwner(cardId, userId);
            if (isCardOwner) {
  
                cardService.deleteCard(cardId);


                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"message\": \"Card deleted successfully\"}");
            } else {

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"message\": \"You are not authorized to delete this card\"}");
            }
        } else {

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\": \"Unauthorized access\"}");
        }
    }
}
