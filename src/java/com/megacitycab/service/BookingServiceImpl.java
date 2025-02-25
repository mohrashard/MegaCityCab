package com.megacitycab.service;

import com.megacitycab.model.Booking;
import com.megacitycab.repository.BookingRepository;
import java.util.List;

public class BookingServiceImpl implements BookingService {
    
    private final BookingRepository bookingRepository;
    
    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }
    
    @Override
    public boolean createBooking(Booking booking) {
        try {
            validateBooking(booking);
            return bookingRepository.saveBooking(booking);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            return false;
        }
    }
    
    private void validateBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }
        
        if (booking.getPassengerId() <= 0) {
            throw new IllegalArgumentException("Invalid passenger ID");
        }
        
        validateField(booking.getVehicleType(), "Vehicle Type");
        validateField(booking.getPickupLocation(), "Pickup Location");
        validateField(booking.getDropoffLocation(), "Drop-off Location");
        validateField(booking.getBookingDateTime(), "Booking Date/Time");
        validateField(booking.getPaymentMethod(), "Payment Method");
    }
    
    private void validateField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }
    
    @Override
    public Booking getBooking(int bookingId) {
        return bookingRepository.getBookingById(bookingId);
    }
    
    @Override
    public List<Booking> getPassengerBookings(int passengerId) {
        return bookingRepository.getBookingsByPassengerId(passengerId);
    }
    
    @Override
    public boolean updateBooking(Booking booking) {
        if (!isValidBooking(booking)) {
            return false;
        }
        
        return bookingRepository.updateBooking(booking);
    }
    
    @Override
    public boolean cancelBooking(int bookingId) {

        
        return bookingRepository.deleteBooking(bookingId);
    }

    private boolean isValidBooking(Booking booking) {
        if (booking == null) {
            return false;
        }
        
        if (booking.getPassengerId() <= 0) {
            return false;
        }
        
        if (booking.getVehicleType() == null || booking.getVehicleType().trim().isEmpty()) {
            return false;
        }
        
        if (booking.getPickupLocation() == null || booking.getPickupLocation().trim().isEmpty()) {
            return false;
        }
        
        if (booking.getDropoffLocation() == null || booking.getDropoffLocation().trim().isEmpty()) {
            return false;
        }
        
        if (booking.getBookingDateTime() == null || booking.getBookingDateTime().trim().isEmpty()) {
            return false;
        }
        
        if (booking.getPaymentMethod() == null || booking.getPaymentMethod().trim().isEmpty()) {
            return false;
        }
        
        return true;
    }
}