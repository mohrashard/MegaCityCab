package com.megacitycab.dao;

import com.megacitycab.model.Admin;

public interface AdminDAOInterface {
    void saveAdmin(Admin admin);
    Admin getAdminByUsername(String username);
    boolean validateAdmin(String username, String password); // Added validateAdmin method
}
