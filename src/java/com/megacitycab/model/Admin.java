package com.megacitycab.model;

public class Admin {

    private String username; 
    private String adminName; 
    private String password; 
    private String salt; 

  
    public Admin(String username, String adminName) {
        this.username = username;
        this.adminName = adminName;
      
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
