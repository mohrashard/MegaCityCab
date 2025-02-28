package com.megacitycab.service;

import com.megacitycab.model.Booking;
import java.util.List;

public interface BookingService {
    boolean createBooking(Booking booking);
    Booking getBooking(int bookingId);
    List<Booking> getPassengerBookings(int passengerId);
    boolean updateBooking(Booking booking);
    boolean cancelBooking(int bookingId);
    List<Booking> getAllBookings();
    List<Booking> getBookingsByStatus(String status);
    
    // New method to assign a driver to a booking
    boolean assignDriver(int bookingId, int driverId);
 boolean updateBookingFee(int bookingId, double hireFee);
}
