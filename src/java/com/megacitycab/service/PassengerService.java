package com.megacitycab.service;

import com.megacitycab.dao.PassengerDAO;
import com.megacitycab.model.Passenger;

public class PassengerService {
    private PassengerDAO passengerDAO;

    public PassengerService() {
        passengerDAO = new PassengerDAO();
    }

    public boolean registerPassenger(Passenger passenger) {
        try {
            // Attempt to save the passenger using the DAO
            passengerDAO.savePassenger(passenger);
            return true; // Return true if save is successful
        } catch (Exception e) {
            // Handle exceptions (e.g., log the error, throw a custom exception, etc.)
            System.out.println("Error registering passenger: " + e.getMessage());
            return false; // Return false if there was an error
        }
    }

    public Passenger getPassengerByEmail(String email) {
        return passengerDAO.getPassengerByEmail(email);
    }
}
