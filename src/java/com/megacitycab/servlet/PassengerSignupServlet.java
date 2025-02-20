package com.megacitycab.servlet;

import com.megacitycab.model.Passenger;
import com.megacitycab.service.PassengerService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;

@WebServlet("/passengerSignup")
public class PassengerSignupServlet extends HttpServlet {

    private PassengerService passengerService = new PassengerService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String nic = request.getParameter("nic");
        String address = request.getParameter("address");

        
        Passenger passenger = new Passenger(fullName, email, phone, password, nic, address);

        
        boolean isRegistered = passengerService.registerPassenger(passenger);
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

       JSONObject jsonResponse = new JSONObject();
        if (isRegistered) {
            jsonResponse.put("message", "Passenger Registration Successful!");
        } else {
            jsonResponse.put("message", "Error registering admin.");
        }

      
        out.print(jsonResponse.toString());
        out.flush(); 
    }
}
