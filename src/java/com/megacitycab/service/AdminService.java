package com.megacitycab.service;

<<<<<<< HEAD
import com.megacitycab.dao.AdminDAOInterface;
=======
>>>>>>> 72ae542dfd6364ed1a2fd6a2eb44d3e556980607
import com.megacitycab.dao.AdminDAO;
import com.megacitycab.model.Admin;

public class AdminService {
<<<<<<< HEAD
    private AdminDAOInterface adminDAO;
=======
    private AdminDAO adminDAO;
>>>>>>> 72ae542dfd6364ed1a2fd6a2eb44d3e556980607

    public AdminService() {
        adminDAO = new AdminDAO();
    }

<<<<<<< HEAD
    public boolean registerAdmin(Admin admin) {
        try {
            adminDAO.saveAdmin(admin);
            return true;
        } catch (Exception e) {
            System.out.println("Error registering admin: " + e.getMessage());
            return false;
        }
    }

    public Admin getAdminByUsername(String username) {
        return adminDAO.getAdminByUsername(username);
    }

    public boolean login(String username, String password) {
        Admin admin = adminDAO.getAdminByUsername(username);
        if (admin != null) {
            // Here you can use a method to check hashed passwords.
            // For now, we'll assume plain text for simplicity.
            return admin.getPassword().equals(password);
        }
        return false; // Login failed
=======
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
>>>>>>> 72ae542dfd6364ed1a2fd6a2eb44d3e556980607
    }
}
