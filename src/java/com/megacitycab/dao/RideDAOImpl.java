package com.megacitycab.dao;

import com.megacitycab.config.DBConnection;
import com.megacitycab.dto.RideDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RideDAOImpl implements RideDAO {
    
@Override
public List<RideDTO> getCurrentRides(int driverId) throws SQLException {
    List<RideDTO> rides = new ArrayList<>();
  
    String sql = "SELECT b.booking_id, b.passenger_id, p.full_name, p.phone, b.vehicle_type, "
               + "b.pickup_location, b.dropoff_location, b.booking_datetime, b.payment_method, "
               + "b.hire_fee, b.status, b.driver_id "
               + "FROM Bookings b "
               + "JOIN Passengers p ON b.passenger_id = p.passenger_id "
               + "WHERE UPPER(b.status) = 'ASSIGNED' AND b.driver_id = ?";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, driverId);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                RideDTO ride = new RideDTO();
                ride.setBookingId(rs.getInt("booking_id"));
                ride.setPassengerId(rs.getInt("passenger_id"));
                ride.setPassengerName(rs.getString("full_name"));
                ride.setPassengerPhone(rs.getString("phone"));
                ride.setVehicleType(rs.getString("vehicle_type"));
                ride.setPickupLocation(rs.getString("pickup_location"));
                ride.setDropoffLocation(rs.getString("dropoff_location"));
                ride.setBookingDatetime(rs.getString("booking_datetime"));
                ride.setPaymentMethod(rs.getString("payment_method"));
                
       
                double hireFee = rs.getDouble("hire_fee");
                ride.setHireFee(hireFee);
 
                double adminCharge = hireFee * 0.3;
                ride.setAdminCharge(adminCharge);
                
             
                double driverEarnings = hireFee * 0.7;
                ride.setDriverEarnings(driverEarnings);
                
                ride.setStatus(rs.getString("status"));
                ride.setDriverId(driverId);

                rides.add(ride);
            }
        }
    }
    return rides;
}


    
@Override
public List<RideDTO> getEndedRides(int driverId) throws SQLException {
    List<RideDTO> rides = new ArrayList<>();
    String sql = "SELECT b.booking_id, b.passenger_id, p.full_name, p.phone, b.vehicle_type, " +
                 "b.pickup_location, b.dropoff_location, b.booking_datetime, b.payment_method, " +
                 "b.hire_fee, b.status, b.driver_id " +
                 "FROM Bookings b " +
                 "JOIN Passengers p ON b.passenger_id = p.passenger_id " +
                 "WHERE b.driver_id = ? AND b.status = 'ended'";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, driverId);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                RideDTO ride = new RideDTO();
                ride.setBookingId(rs.getInt("booking_id"));
                ride.setPassengerId(rs.getInt("passenger_id"));
                ride.setPassengerName(rs.getString("full_name"));
                ride.setPassengerPhone(rs.getString("phone"));
                ride.setVehicleType(rs.getString("vehicle_type"));
                ride.setPickupLocation(rs.getString("pickup_location"));
                ride.setDropoffLocation(rs.getString("dropoff_location"));
                ride.setBookingDatetime(rs.getString("booking_datetime"));
                ride.setPaymentMethod(rs.getString("payment_method"));
                
             
                double hireFee = rs.getDouble("hire_fee");
                ride.setHireFee(hireFee);
                
              
                double adminCharge = hireFee * 0.3; 
                double driverEarnings = hireFee - adminCharge;
                
                ride.setAdminCharge(adminCharge);
                ride.setDriverEarnings(driverEarnings);
                
                ride.setStatus(rs.getString("status"));
                ride.setDriverId(rs.getInt("driver_id"));
                rides.add(ride);
            }
        }
    }
    
    return rides;
}
    
@Override
public boolean acceptRide(int bookingId, int driverId) throws SQLException {
    // Changed SQL to update driver_accepted field instead of changing status
    String sql = "UPDATE Bookings SET driver_accepted = true WHERE booking_id = ? AND driver_id = ?";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, bookingId);
        stmt.setInt(2, driverId);
        
        int rowsAffected = stmt.executeUpdate();
        return rowsAffected > 0;
    }
}
    
    @Override
    public boolean cancelRide(int bookingId, String reason) throws SQLException {

        String sql = "UPDATE Bookings SET status = 'pending', driver_id = NULL WHERE booking_id = ? AND status = 'assigned'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    @Override
    public boolean endRide(int bookingId) throws SQLException {
        String sql = "UPDATE Bookings SET status = 'ended' WHERE booking_id = ? AND status = 'assigned'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}