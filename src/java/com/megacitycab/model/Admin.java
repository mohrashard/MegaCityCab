package com.megacitycab.model;

public class Admin {
<<<<<<< HEAD
    private int adminId;
    private String username;
    private String adminName;
    private String password;

    // Constructor for retrieving from DB
    public Admin(int adminId, String username, String adminName, String password) {
        this.adminId = adminId;
        this.username = username;
=======
    private String adminID;
    private String adminName;
    private String password;

    public Admin(String adminID, String adminName, String password) {
        this.adminID = adminID;
>>>>>>> 72ae542dfd6364ed1a2fd6a2eb44d3e556980607
        this.adminName = adminName;
        this.password = password;
    }

<<<<<<< HEAD
    // Constructor for sign-up (without adminId)
    public Admin(String username, String adminName, String password) {
        this.username = username;
        this.adminName = adminName;
        this.password = password;
    }

    // Getters and Setters
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
=======
    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
>>>>>>> 72ae542dfd6364ed1a2fd6a2eb44d3e556980607
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
<<<<<<< HEAD
=======
    
    
    
    
>>>>>>> 72ae542dfd6364ed1a2fd6a2eb44d3e556980607
}
