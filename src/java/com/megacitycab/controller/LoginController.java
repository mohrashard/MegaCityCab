package com.megacitycab.controller;

import com.megacitycab.model.Admin;
import com.megacitycab.service.AdminService;

public class LoginController {
    private AdminService adminService;

    public LoginController() {
        adminService = new AdminService();
    }

    public boolean login(String username, String password) {
        // Retrieve the admin by username
        Admin admin = adminService.getAdminByUsername(username);
        // Check if the admin exists and the password matches (use hashed password comparison in production)
        return admin != null && admin.getPassword().equals(password);
    }
}
