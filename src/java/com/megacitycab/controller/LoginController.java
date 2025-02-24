package com.megacitycab.controller;

import com.megacitycab.dao.AdminDAO;
import com.megacitycab.model.Admin;
import com.megacitycab.util.PasswordUtil;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LoginController {
    private AdminDAO adminDAO;
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    public LoginController() {
        adminDAO = new AdminDAO();
    }

    public boolean login(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            logger.warning("Login attempt with null or empty credentials");
            return false;
        }

        try {
            Admin admin = adminDAO.getAdminByUsername(username);
            if (admin == null) {
                logger.info("Login failed: no admin found with username: " + username);
                return false;
            }

            boolean verified = PasswordUtil.verifyPassword(password, admin.getPassword(), admin.getSalt());
            logger.info("Login attempt for user " + username + ": " + (verified ? "successful" : "failed"));
            return verified;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during login", e);
            return false;
        }
    }
}