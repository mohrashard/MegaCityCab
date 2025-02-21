package com.megacitycab.service;

import com.megacitycab.dao.PassengerDAO;
import com.megacitycab.model.Passenger;

public class PassengerService {
    private PassengerDAO passengerDAO;

    public PassengerService() {
        passengerDAO = new PassengerDAO();
    }

public boolean registerPassenger(Passenger passenger) {
<<<<<<< HEAD
 
    try {
      
        passengerDAO.savePassenger(passenger);
        return true; 
    } catch (Exception e) {
        
        System.out.println("Error registering passenger: " + e.getMessage());
        return false; 
=======
    // You can add validation logic here if needed
    try {
        // Attempt to save the passenger using the DAO
        passengerDAO.savePassenger(passenger);
        return true; // Return true if save is successful
    } catch (Exception e) {
        // Handle exceptions (e.g., log the error, throw a custom exception, etc.)
        System.out.println("Error registering passenger: " + e.getMessage());
        return false; // Return false if there was an error
>>>>>>> 72ae542dfd6364ed1a2fd6a2eb44d3e556980607
    }
}


    public Passenger getPassengerByEmail(String email) {
        return passengerDAO.getPassengerByEmail(email);
    }
}
