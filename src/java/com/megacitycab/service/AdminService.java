package com.megacitycab.service;

import com.megacitycab.dao.AdminDAO;
import com.megacitycab.model.Admin;

public class AdminService {
    private AdminDAO adminDAO;

    public AdminService() {
        adminDAO = new AdminDAO();
    }

   public boolean registerAdmin(Admin admin) {
    // You can add validation logic here if needed
    try {
        // Attempt to save the admin using the DAO
        adminDAO.saveAdmin(admin);
        return true; // Return true if save is successful
    } catch (Exception e) {
        // Handle exceptions (e.g., log the error, throw a custom exception, etc.)
        System.out.println("Error registering admin: " + e.getMessage());
        return false; // Return false if there was an error
    }
}


    public Admin getAdminByID(String adminID) {
        return adminDAO.getAdminByID(adminID);
    }
}
