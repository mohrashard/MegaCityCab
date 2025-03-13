package com.megacitycab.model;

public class DriverInfo {
    private int driverId;
    private String fullName;
    private String email;
    private String phone;
    private String licenseNo;
    private String vehicleType;
    private String status;

    public DriverInfo() {
    }
    
    public DriverInfo(int driverId, String fullName, String email, String phone, String licenseNo, String vehicleType, String status) {
        this.driverId = driverId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.licenseNo = licenseNo;
        this.vehicleType = vehicleType;
        this.status = status;
    }

    // Getters and Setters
    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
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
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
