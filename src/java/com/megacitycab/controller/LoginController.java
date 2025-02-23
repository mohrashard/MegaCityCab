package com.megacitycab.controller;

import com.megacitycab.dao.AdminDAO;

public class LoginController {
    private AdminDAO adminDAO;

    public LoginController() {
        adminDAO = new AdminDAO();
    }

    public boolean login(String username, String password) {
        // Validate the admin using the AdminDAO's validateAdmin method
        return adminDAO.validateAdmin(username, password);
    }
}
