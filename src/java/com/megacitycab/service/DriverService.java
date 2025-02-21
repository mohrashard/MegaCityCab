package com.megacitycab.service;

import com.megacitycab.dao.DriverDAO;
import com.megacitycab.model.Driver;

public class DriverService {
    private DriverDAO driverDAO;

    public DriverService() {
        driverDAO = new DriverDAO();
    }

 public boolean registerDriver(Driver driver) {
   
    try {
      
        driverDAO.saveDriver(driver);
        return true; 
    } catch (Exception e) {
        
        System.out.println("Error registering driver: " + e.getMessage());
        return false; 
    }
}


    public Driver getDriverByEmail(String email) {
        return driverDAO.getDriverByEmail(email);
    }
}
