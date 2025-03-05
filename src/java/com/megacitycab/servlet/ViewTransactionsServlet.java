package com.megacitycab.servlet;

import com.megacitycab.model.Transaction;
import com.megacitycab.service.TransactionService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/viewTransactions")
public class ViewTransactionsServlet extends HttpServlet {
    private final TransactionService transactionService = new TransactionService();

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
            List<Transaction> transactions = transactionService.getTransactionsByPassengerId(passengerId);
            JSONArray jsonArray = new JSONArray();
            
            for (Transaction t : transactions) {
                JSONObject json = new JSONObject();
                json.put("transactionType", t.getTransactionType());
                json.put("amount", t.getAmount());
                json.put("description", t.getDescription());
                json.put("dateTime", t.getDateTime().toString());
                jsonArray.put(json);
            }
            
            response.getWriter().write(jsonArray.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Error retrieving transactions: " + e.getMessage() + "\"}");
            System.err.println("Transaction error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}