package com.megacitycab.model;

public class Vehicle {
    private int id;
    private String plateNumber;
    private String vehicleType;
    private int passengerCapacity;
    private String brand;
    private String model;
    private String status;
    private Integer driverId;

    public Vehicle() {
        this.status = "Unassigned";
    }

    public Vehicle(String plateNumber, String vehicleType, int passengerCapacity, String brand, String model) {
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
        this.passengerCapacity = passengerCapacity;
        this.brand = brand;
        this.model = model;
        this.status = "Unassigned";
        this.driverId = null;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setPassengerCapacity(int passengerCapacity) {
        this.passengerCapacity = passengerCapacity;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
        this.status = driverId != null ? "Assigned" : "Unassigned";
    }
}
