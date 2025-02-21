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
      
        passengerDAO.savePassenger(passenger);
        return true; 
    } catch (Exception e) {
        
        System.out.println("Error registering passenger: " + e.getMessage());
        return false; 
    }
}


    public Passenger getPassengerByEmail(String email) {
        return passengerDAO.getPassengerByEmail(email);
    }
}
