package com.megacitycab.test;

import com.megacitycab.model.Booking;
import com.megacitycab.repository.BookingRepository;
import com.megacitycab.service.BookingService;
import com.megacitycab.service.BookingServiceImpl;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class BookingServiceTest {
    
    private BookingService bookingService;
    private TestBookingRepository bookingRepository;
    
    @Before
    public void setUp() {
        bookingRepository = new TestBookingRepository();
        bookingService = new BookingServiceImpl(bookingRepository);
    }
    
    @Test
    public void testCreateBooking_ValidBooking_Success() {
        // Arrange
        Booking booking = createValidBooking();
        
        // Act
        boolean result = bookingService.createBooking(booking);
        
        // Assert
        assertTrue(result);
        assertEquals(1, bookingRepository.savedBookings.size());
        assertEquals(booking, bookingRepository.savedBookings.get(0));
    }
    
    @Test
    public void testCreateBooking_NullBooking_Failure() {
        // Arrange
        Booking booking = null;
        
        // Act
        boolean result = bookingService.createBooking(booking);
        
        // Assert
        assertFalse(result);
        assertEquals(0, bookingRepository.savedBookings.size());
    }
    
    @Test
    public void testCreateBooking_InvalidPassengerId_Failure() {
        // Arrange
        Booking booking = createValidBooking();
        booking.setPassengerId(0); // Invalid passenger ID
        
        // Act
        boolean result = bookingService.createBooking(booking);
        
        // Assert
        assertFalse(result);
        assertEquals(0, bookingRepository.savedBookings.size());
    }
    
    @Test
    public void testCreateBooking_MissingVehicleType_Failure() {
        // Arrange
        Booking booking = createValidBooking();
        booking.setVehicleType(null);
        
        // Act
        boolean result = bookingService.createBooking(booking);
        
        // Assert
        assertFalse(result);
        assertEquals(0, bookingRepository.savedBookings.size());
    }
    
    @Test
    public void testCreateBooking_EmptyPickupLocation_Failure() {
        // Arrange
        Booking booking = createValidBooking();
        booking.setPickupLocation("");
        
        // Act
        boolean result = bookingService.createBooking(booking);
        
        // Assert
        assertFalse(result);
        assertEquals(0, bookingRepository.savedBookings.size());
    }
    
    @Test
    public void testGetBooking_ExistingId_ReturnsBooking() {
        // Arrange
        int bookingId = 1;
        Booking expectedBooking = createValidBooking();
        expectedBooking.setBookingId(bookingId);
        bookingRepository.bookingById.put(bookingId, expectedBooking);
        
        // Act
        Booking result = bookingService.getBooking(bookingId);
        
        // Assert
        assertNotNull(result);
        assertEquals(expectedBooking, result);
    }
    
    @Test
    public void testGetBooking_NonExistingId_ReturnsNull() {
        // Arrange
        int nonExistingId = 999;
        
        // Act
        Booking result = bookingService.getBooking(nonExistingId);
        
        // Assert
        assertNull(result);
    }
    
    @Test
    public void testGetPassengerBookings_ExistingPassenger_ReturnsBookingList() {
        // Arrange
        int passengerId = 5;
        List<Booking> expectedBookings = new ArrayList<>();
        
        Booking booking1 = createValidBooking();
        booking1.setBookingId(1);
        booking1.setPassengerId(passengerId);
        
        Booking booking2 = createValidBooking();
        booking2.setBookingId(2);
        booking2.setPassengerId(passengerId);
        
        expectedBookings.add(booking1);
        expectedBookings.add(booking2);
        
        bookingRepository.bookingsByPassengerId.put(passengerId, expectedBookings);
        
        // Act
        List<Booking> result = bookingService.getPassengerBookings(passengerId);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedBookings, result);
    }
    
    @Test
    public void testGetPassengerBookings_NonExistingPassenger_ReturnsEmptyList() {
        // Arrange
        int nonExistingPassengerId = 999;
        
        // Act
        List<Booking> result = bookingService.getPassengerBookings(nonExistingPassengerId);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    public void testUpdateBooking_ValidBooking_Success() {
        // Arrange
        Booking booking = createValidBooking();
        booking.setBookingId(1);
        
        // Act
        boolean result = bookingService.updateBooking(booking);
        
        // Assert
        assertTrue(result);
        assertEquals(1, bookingRepository.updatedBookings.size());
        assertEquals(booking, bookingRepository.updatedBookings.get(0));
    }
    
    @Test
    public void testUpdateBooking_NullBooking_Failure() {
        // Arrange
        Booking booking = null;
        
        // Act
        boolean result = bookingService.updateBooking(booking);
        
        // Assert
        assertFalse(result);
        assertEquals(0, bookingRepository.updatedBookings.size());
    }
    
    @Test
    public void testUpdateBooking_InvalidPassengerId_Failure() {
        // Arrange
        Booking booking = createValidBooking();
        booking.setPassengerId(-1);
        
        // Act
        boolean result = bookingService.updateBooking(booking);
        
        // Assert
        assertFalse(result);
        assertEquals(0, bookingRepository.updatedBookings.size());
    }
    
    @Test
    public void testCancelBooking_ExistingId_Success() {
        // Arrange
        int bookingId = 1;
        bookingRepository.deletedBookingIds.clear();
        bookingRepository.deleteBookingResult = true;
        
        // Act
        boolean result = bookingService.cancelBooking(bookingId);
        
        // Assert
        assertTrue(result);
        assertEquals(1, bookingRepository.deletedBookingIds.size());
        assertEquals(Integer.valueOf(bookingId), bookingRepository.deletedBookingIds.get(0));
    }
    
    @Test
    public void testCancelBooking_NonExistingId_Failure() {
        // Arrange
        int bookingId = 999;
        bookingRepository.deletedBookingIds.clear();
        bookingRepository.deleteBookingResult = false;
        
        // Act
        boolean result = bookingService.cancelBooking(bookingId);
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    public void testGetAllBookings_ReturnsAllBookings() {
        // Arrange
        List<Booking> expectedBookings = new ArrayList<>();
        
        Booking booking1 = createValidBooking();
        booking1.setBookingId(1);
        
        Booking booking2 = createValidBooking();
        booking2.setBookingId(2);
        
        expectedBookings.add(booking1);
        expectedBookings.add(booking2);
        
        bookingRepository.allBookings = expectedBookings;
        
        // Act
        List<Booking> result = bookingService.getAllBookings();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedBookings, result);
    }
    
    @Test
    public void testGetBookingsByStatus_ValidStatus_ReturnsFilteredBookings() {
        // Arrange
        String status = "PENDING";
        List<Booking> expectedBookings = new ArrayList<>();
        
        Booking booking1 = createValidBooking();
        booking1.setBookingId(1);
        booking1.setStatus(status);
        
        Booking booking2 = createValidBooking();
        booking2.setBookingId(2);
        booking2.setStatus(status);
        
        expectedBookings.add(booking1);
        expectedBookings.add(booking2);
        
        bookingRepository.bookingsByStatus.put(status, expectedBookings);
        
        // Act
        List<Booking> result = bookingService.getBookingsByStatus(status);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedBookings, result);
    }
    
    @Test
    public void testGetBookingsByStatus_NonExistingStatus_ReturnsEmptyList() {
        // Arrange
        String nonExistingStatus = "NON_EXISTING";
        
        // Act
        List<Booking> result = bookingService.getBookingsByStatus(nonExistingStatus);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    public void testAssignDriver_ValidBookingAndDriver_Success() {
        // Arrange
        int bookingId = 1;
        int driverId = 5;
        Booking booking = createValidBooking();
        booking.setBookingId(bookingId);
        
        bookingRepository.bookingById.put(bookingId, booking);
        
        // Act
        boolean result = bookingService.assignDriver(bookingId, driverId);
        
        // Assert
        assertTrue(result);
        assertEquals(1, bookingRepository.updatedBookings.size());
        assertEquals(Integer.valueOf(driverId), bookingRepository.updatedBookings.get(0).getDriverId());
        assertEquals("ASSIGNED", bookingRepository.updatedBookings.get(0).getStatus());
    }
    
    @Test
    public void testAssignDriver_NonExistingBooking_Failure() {
        // Arrange
        int nonExistingBookingId = 999;
        int driverId = 5;
        
        // Act
        boolean result = bookingService.assignDriver(nonExistingBookingId, driverId);
        
        // Assert
        assertFalse(result);
        assertEquals(0, bookingRepository.updatedBookings.size());
    }
    
    @Test
    public void testUpdateBookingFee_ValidBookingAndFee_Success() {
        // Arrange
        int bookingId = 1;
        double hireFee = 25.50;
        bookingRepository.updateFeeResult = true;
        
        // Act
        boolean result = bookingService.updateBookingFee(bookingId, hireFee);
        
        // Assert
        assertTrue(result);
        assertEquals(Integer.valueOf(bookingId), bookingRepository.updatedFeeBookingId);
        assertEquals(Double.valueOf(hireFee), bookingRepository.updatedFeeAmount);
    }
    
    @Test
    public void testUpdateBookingFee_RepositoryFailure_ReturnsFalse() {
        // Arrange
        int bookingId = 1;
        double hireFee = 25.50;
        bookingRepository.updateFeeResult = false;
        
        // Act
        boolean result = bookingService.updateBookingFee(bookingId, hireFee);
        
        // Assert
        assertFalse(result);
    }
    
    private Booking createValidBooking() {
        Booking booking = new Booking();
        booking.setPassengerId(5);
        booking.setVehicleType("Sedan");
        booking.setPickupLocation("123 Main St");
        booking.setDropoffLocation("456 Elm St");
        booking.setBookingDateTime("2025-03-12T14:30:00");
        booking.setPaymentMethod("Credit Card");
        return booking;
    }
    
    // Test double for BookingRepository
    private class TestBookingRepository implements BookingRepository {
        private List<Booking> savedBookings = new ArrayList<>();
        private List<Booking> updatedBookings = new ArrayList<>();
        private List<Integer> deletedBookingIds = new ArrayList<>();
        private List<Booking> allBookings = new ArrayList<>();
        
        private java.util.Map<Integer, Booking> bookingById = new java.util.HashMap<>();
        private java.util.Map<Integer, List<Booking>> bookingsByPassengerId = new java.util.HashMap<>();
        private java.util.Map<String, List<Booking>> bookingsByStatus = new java.util.HashMap<>();
        
        private boolean deleteBookingResult = true;
        private boolean updateFeeResult = true;
        private Integer updatedFeeBookingId = null;
        private Double updatedFeeAmount = null;
        
        @Override
        public boolean saveBooking(Booking booking) {
            if (booking != null) {
                savedBookings.add(booking);
                return true;
            }
            return false;
        }
        
        @Override
        public Booking getBookingById(int bookingId) {
            return bookingById.get(bookingId);
        }
        
        @Override
        public List<Booking> getBookingsByPassengerId(int passengerId) {
            List<Booking> bookings = bookingsByPassengerId.get(passengerId);
            return bookings != null ? bookings : new ArrayList<>();
        }
        
        @Override
        public boolean updateBooking(Booking booking) {
            if (booking != null) {
                updatedBookings.add(booking);
                return true;
            }
            return false;
        }
        
        @Override
        public boolean deleteBooking(int bookingId) {
            deletedBookingIds.add(bookingId);
            return deleteBookingResult;
        }
        
        @Override
        public List<Booking> getAllBookings() {
            return allBookings;
        }
        
        @Override
        public List<Booking> getBookingsByStatus(String status) {
            List<Booking> bookings = bookingsByStatus.get(status);
            return bookings != null ? bookings : new ArrayList<>();
        }
        
        @Override
        public boolean updateBookingFee(int bookingId, double hireFee) {
            updatedFeeBookingId = bookingId;
            updatedFeeAmount = hireFee;
            return updateFeeResult;
        }
        
        @Override
        public boolean assignDriver(int bookingId, int driverId) {
            return false; // Not needed for this test
        }
    }
}