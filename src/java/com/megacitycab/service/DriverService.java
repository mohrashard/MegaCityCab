package com.megacitycab.service;

import com.megacitycab.dao.DriverDAO;
import com.megacitycab.model.Driver;

public class DriverService {
    private DriverDAO driverDAO;

    public DriverService() {
        driverDAO = new DriverDAO();
    }

 public boolean registerDriver(Driver driver) {
<<<<<<< HEAD
   
    try {
      
        driverDAO.saveDriver(driver);
        return true; 
    } catch (Exception e) {
        
        System.out.println("Error registering driver: " + e.getMessage());
        return false; 
=======
    // You can add validation logic here if needed
    try {
        // Attempt to save the driver using the DAO
        driverDAO.saveDriver(driver);
        return true; // Return true if save is successful
    } catch (Exception e) {
        // Handle exceptions (e.g., log the error, throw a custom exception, etc.)
        System.out.println("Error registering driver: " + e.getMessage());
        return false; // Return false if there was an error
>>>>>>> 72ae542dfd6364ed1a2fd6a2eb44d3e556980607
    }
}


    public Driver getDriverByEmail(String email) {
        return driverDAO.getDriverByEmail(email);
    }
}
