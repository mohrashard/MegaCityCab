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

    public boolean registerAdmin(Admin admin , String password) {
    try {
        String salt = PasswordUtil.generateSalt();
        String hashedPassword = PasswordUtil.hashPassword(password, salt);
        admin.setPassword(hashedPassword);
        admin.setSalt(salt);
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
}
