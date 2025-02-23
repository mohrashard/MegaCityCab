package com.megacitycab.service;

import com.megacitycab.dao.AdminDAOInterface;
import com.megacitycab.dao.AdminDAO;
import com.megacitycab.model.Admin;

public class AdminService {

    private AdminDAOInterface adminDAO;

    public AdminService() {
        adminDAO = new AdminDAO();
    }

    public boolean registerAdmin(Admin admin) {
        try {
            adminDAO.saveAdmin(admin);
            return true; // Return true if save is successful
        } catch (Exception e) {
            System.out.println("Error registering admin: " + e.getMessage());
            return false; // Return false if there was an error
        }
    }

 

    public boolean login(String username, String password) {
        Admin admin = adminDAO.getAdminByUsername(username);
        if (admin != null) {
            // Here you can use a method to check hashed passwords.
            return admin.getPassword().equals(password);
        }
        return false; // Login failed
    }

    public Admin getAdminByUsername(String username) {
        return adminDAO.getAdminByUsername(username);
    }
}
