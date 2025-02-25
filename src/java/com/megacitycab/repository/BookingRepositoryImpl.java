package com.megacitycab.repository;

import com.megacitycab.model.Booking;
import com.megacitycab.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp; 
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class BookingRepositoryImpl implements BookingRepository {
    
   @Override
public boolean saveBooking(Booking booking) {
    String sql = "INSERT INTO [megacitycab].[dbo].[Bookings] "
               + "([passenger_id], [vehicle_type], [pickup_location], [dropoff_location], "
               + "[booking_datetime], [payment_method]) "
               + "VALUES (?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        

        String bookingDateTimeStr = booking.getBookingDateTime();
        if (bookingDateTimeStr == null || bookingDateTimeStr.isEmpty()) {
            throw new IllegalArgumentException("Booking datetime is required");
        }
        
       
        LocalDateTime localDateTime = LocalDateTime.parse(
            bookingDateTimeStr, 
            DateTimeFormatter.ISO_LOCAL_DATE_TIME 
        );
        
   
        Timestamp bookingTimestamp = Timestamp.valueOf(localDateTime);
        
  
        stmt.setInt(1, booking.getPassengerId());
        stmt.setString(2, booking.getVehicleType());
        stmt.setString(3, booking.getPickupLocation());
        stmt.setString(4, booking.getDropoffLocation());
        stmt.setTimestamp(5, bookingTimestamp);
        stmt.setString(6, booking.getPaymentMethod());
        

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Creating booking failed, no rows affected.");
        }
        
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                booking.setBookingId(generatedKeys.getInt(1));
            }
        }
        return true;
        
    } catch (SQLException e) {
        System.err.println("SQL Error: " + e.getMessage());
        return false;
    } catch (DateTimeParseException e) {
        System.err.println("Invalid datetime format: " + booking.getBookingDateTime());
        return false;
    }
}
    @Override
    public Booking getBookingById(int bookingId) {
        String sql = "SELECT * FROM [megacitycab].[dbo].[Bookings] WHERE [booking_id] = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Booking booking = new Booking();
                    booking.setBookingId(rs.getInt("booking_id"));
                    booking.setPassengerId(rs.getInt("passenger_id"));
                    booking.setVehicleType(rs.getString("vehicle_type"));
                    booking.setPickupLocation(rs.getString("pickup_location"));
                    booking.setDropoffLocation(rs.getString("dropoff_location"));
                    booking.setBookingDateTime(rs.getString("booking_datetime"));
                    booking.setPaymentMethod(rs.getString("payment_method"));
          
                    if (rs.getObject("hire_fee") != null) {
                        booking.setHireFee(rs.getDouble("hire_fee"));
                    }
                    
                    return booking;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<Booking> getBookingsByPassengerId(int passengerId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM [megacitycab].[dbo].[Bookings] WHERE [passenger_id] = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, passengerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking();
                    booking.setBookingId(rs.getInt("booking_id"));
                    booking.setPassengerId(rs.getInt("passenger_id"));
                    booking.setVehicleType(rs.getString("vehicle_type"));
                    booking.setPickupLocation(rs.getString("pickup_location"));
                    booking.setDropoffLocation(rs.getString("dropoff_location"));
                    booking.setBookingDateTime(rs.getString("booking_datetime"));
                    booking.setPaymentMethod(rs.getString("payment_method"));
                    
                    if (rs.getObject("hire_fee") != null) {
                        booking.setHireFee(rs.getDouble("hire_fee"));
                    }
                    
                    bookings.add(booking);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bookings;
    }
    
    @Override
    public boolean updateBooking(Booking booking) {
        String sql = "UPDATE [megacitycab].[dbo].[Bookings] SET "
                   + "[passenger_id] = ?, "
                   + "[vehicle_type] = ?, "
                   + "[pickup_location] = ?, "
                   + "[dropoff_location] = ?, "
                   + "[booking_datetime] = ?, "
                   + "[payment_method] = ?, "
                   + "[hire_fee] = ? "
                   + "WHERE [booking_id] = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, booking.getPassengerId());
            stmt.setString(2, booking.getVehicleType());
            stmt.setString(3, booking.getPickupLocation());
            stmt.setString(4, booking.getDropoffLocation());
            stmt.setString(5, booking.getBookingDateTime());
            stmt.setString(6, booking.getPaymentMethod());
            
            if (booking.getHireFee() != null) {
                stmt.setDouble(7, booking.getHireFee());
            } else {
                stmt.setNull(7, java.sql.Types.DOUBLE);
            }
            
            stmt.setInt(8, booking.getBookingId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deleteBooking(int bookingId) {
        String sql = "DELETE FROM [megacitycab].[dbo].[Bookings] WHERE [booking_id] = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}