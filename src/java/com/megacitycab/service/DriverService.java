package com.megacitycab.service;

import com.megacitycab.dao.DriverDAO;
import com.megacitycab.model.Driver;
import com.megacitycab.util.PasswordUtil;

public class DriverService {
    private DriverDAO driverDAO;

    public DriverService() {
        driverDAO = new DriverDAO();
    }

public boolean registerDriver(Driver driver, String password) {
    try {
        String salt = PasswordUtil.generateSalt();
        String hashedPassword = PasswordUtil.hashPassword(password, salt);
        driver.setPassword(hashedPassword);
        driver.setSalt(salt); 
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
