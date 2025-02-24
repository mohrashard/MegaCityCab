package com.megacitycab.service;

import com.megacitycab.dao.AdminDAOInterface;
import com.megacitycab.dao.AdminDAO;
import com.megacitycab.model.Admin;
import com.megacitycab.util.PasswordUtil;

public class AdminService {

    private AdminDAOInterface adminDAO;

    public AdminService() {
        adminDAO = new AdminDAO();
    }

    public boolean registerAdmin(Admin admin) {
        try {
            // Generate salt and hash the password
            String salt = PasswordUtil.generateSalt();
            String hashedPassword = PasswordUtil.hashPassword(admin.getPassword(), salt);
            admin.setPassword(hashedPassword); // Set the hashed password

            // Set the salt in the admin object
            admin.setSalt(salt); // Make sure to have a setSalt method in the Admin model

            adminDAO.saveAdmin(admin);
            return true;
        } catch (Exception e) {
            System.out.println("Error registering admin: " + e.getMessage());
            return false;
        }
    }

    public boolean login(String username, String password) {
        // Validate the admin using the AdminDAO's validateAdmin method
        return adminDAO.validateAdmin(username, password);
    }

    public Admin getAdminByUsername(String username) {
        return adminDAO.getAdminByUsername(username);
    }
}
