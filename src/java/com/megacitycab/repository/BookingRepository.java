package com.megacitycab.repository;

import com.megacitycab.model.Booking;
import java.util.List;


public interface BookingRepository {
    boolean saveBooking(Booking booking);
    Booking getBookingById(int bookingId);
    List<Booking> getBookingsByPassengerId(int passengerId);
    boolean updateBooking(Booking booking);
    boolean deleteBooking(int bookingId);
    
    
        // New method
    List<Booking> getAllBookings();
    
    // If you want filtering capability
    List<Booking> getBookingsByStatus(String status);
    
    boolean updateBookingFee(int bookingId, double hireFee);

}