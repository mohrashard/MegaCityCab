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
            String hashedPassword = PasswordUtil.hashPassword(admin.getPassword());
            admin.setPassword(hashedPassword);
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
