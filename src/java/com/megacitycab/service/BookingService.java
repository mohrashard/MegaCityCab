package com.megacitycab.service;

import com.megacitycab.model.Booking;
import java.util.List;

public interface BookingService {
    boolean createBooking(Booking booking);
    Booking getBooking(int bookingId);
    List<Booking> getPassengerBookings(int passengerId);
    boolean updateBooking(Booking booking);
    boolean cancelBooking(int bookingId);
}