package com.megacitycab.dto;

public class RideDTO {
    private int bookingId;
    private int passengerId;
    private String passengerName;
    private String passengerPhone;
    private String vehicleType;
    private String pickupLocation;
    private String dropoffLocation;
    private String bookingDatetime;
    private String paymentMethod;
    private double hireFee;
    private double adminCharge; // Add this field for admin charge
    private double driverEarnings; // Add this field for driver earnings
    private String status;
    private int driverId;

    // Getter and setter methods for each field

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    
    public int getPassengerId() { return passengerId; }
    public void setPassengerId(int passengerId) { this.passengerId = passengerId; }
    
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    
    public String getPassengerPhone() { return passengerPhone; }
    public void setPassengerPhone(String passengerPhone) { this.passengerPhone = passengerPhone; }
    
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    
    public String getDropoffLocation() { return dropoffLocation; }
    public void setDropoffLocation(String dropoffLocation) { this.dropoffLocation = dropoffLocation; }
    
    public String getBookingDatetime() { return bookingDatetime; }
    public void setBookingDatetime(String bookingDatetime) { this.bookingDatetime = bookingDatetime; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public double getHireFee() { return hireFee; }
    public void setHireFee(double hireFee) { this.hireFee = hireFee; }

    public double getAdminCharge() { return adminCharge; }
    public void setAdminCharge(double adminCharge) { this.adminCharge = adminCharge; }

    public double getDriverEarnings() { return driverEarnings; }
    public void setDriverEarnings(double driverEarnings) { this.driverEarnings = driverEarnings; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    // The method for calculating admin charge and driver earnings should be updated
    public void calculateAdminChargeAndEarnings() {
        this.adminCharge = Math.round(this.hireFee * 0.3 * 100.0) / 100.0;
        this.driverEarnings = Math.round(this.hireFee * 0.7 * 100.0) / 100.0;
    }
}
