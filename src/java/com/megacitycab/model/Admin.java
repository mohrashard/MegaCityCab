package com.megacitycab.model;

public class Admin {
    private String adminID;
    private String adminName;
    private String password;

    public Admin(String adminID, String adminName, String password) {
        this.adminID = adminID;
        this.adminName = adminName;
        this.password = password;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
    
    
}
