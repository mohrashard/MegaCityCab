package com.megacitycab.model;

public class Driver {
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String licenseNo;
    private String vehicleType;
    


    public Driver(String fullName, String email, String phone, String password, String licenseNo, String vehicleType) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.licenseNo = licenseNo;
        this.vehicleType = vehicleType;
        
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

   
    
}
