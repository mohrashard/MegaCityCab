package com.megacitycab.model;

public class Booking {
    private int bookingId;
    private int passengerId;
    private String vehicleType;
    private String pickupLocation;
    private String dropoffLocation;
    private String bookingDateTime;
    private String paymentMethod;
    private Double hireFee;

    public Booking() {
    }


    public Booking(int passengerId, String vehicleType, String pickupLocation, 
                   String dropoffLocation, String bookingDateTime, String paymentMethod) {
        this.passengerId = passengerId;
        this.vehicleType = vehicleType;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.bookingDateTime = bookingDateTime;
        this.paymentMethod = paymentMethod;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public String getBookingDateTime() {
        return bookingDateTime;
    }

    public void setBookingDateTime(String bookingDateTime) {
        this.bookingDateTime = bookingDateTime;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getHireFee() {
        return hireFee;
    }

    public void setHireFee(Double hireFee) {
        this.hireFee = hireFee;
    }

    @Override
    public String toString() {
        return "Booking [bookingId=" + bookingId + ", passengerId=" + passengerId + ", vehicleType=" + vehicleType
                + ", pickupLocation=" + pickupLocation + ", dropoffLocation=" + dropoffLocation + ", bookingDateTime="
                + bookingDateTime + ", paymentMethod=" + paymentMethod + ", hireFee=" + hireFee + "]";
    }
}