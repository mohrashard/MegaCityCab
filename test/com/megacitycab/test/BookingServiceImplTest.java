package com.megacitycab.test;

import com.megacitycab.model.Booking;
import com.megacitycab.repository.BookingRepository;
import com.megacitycab.service.BookingServiceImpl;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingServiceImplTest {
    
    private BookingServiceImpl bookingServiceImpl;
    private TestBookingRepository bookingRepository;
    
    @Before
    public void setUp() {
        bookingRepository = new TestBookingRepository();
        bookingServiceImpl = new BookingServiceImpl(bookingRepository);
    }
    
    @Test
    public void testCreateBooking_WithValidationChecks() {
        // Arrange
        Booking validBooking = createValidBooking();
        
        // Act
        boolean result = bookingServiceImpl.createBooking(validBooking);
        
        // Assert
        assertTrue(result);
        assertEquals(1, bookingRepository.bookingsSaved.size());
    }
    
    @Test
    public void testCreateBooking_WithEmptyFields_ShouldFail() {
        assertFalse(bookingServiceImpl.createBooking(null));

        Booking booking1 = createValidBooking();
        booking1.setPassengerId(0);
        assertFalse(bookingServiceImpl.createBooking(booking1));

        Booking booking2 = createValidBooking();
        booking2.setVehicleType(null);
        assertFalse(bookingServiceImpl.createBooking(booking2));

        Booking booking3 = createValidBooking();
        booking3.setVehicleType("");
        assertFalse(bookingServiceImpl.createBooking(booking3));

        Booking booking4 = createValidBooking();
        booking4.setPickupLocation("");
        assertFalse(bookingServiceImpl.createBooking(booking4));

        Booking booking5 = createValidBooking();
        booking5.setDropoffLocation(null);
        assertFalse(bookingServiceImpl.createBooking(booking5));

        Booking booking6 = createValidBooking();
        booking6.setBookingDateTime(null);
        assertFalse(bookingServiceImpl.createBooking(booking6));

        Booking booking7 = createValidBooking();
        booking7.setPaymentMethod(null);
        assertFalse(bookingServiceImpl.createBooking(booking7));

        assertEquals(0, bookingRepository.bookingsSaved.size());
    }
    
    @Test
    public void testGetBooking_ReturnsBookingFromRepository() {
        // Arrange
        int bookingId = 123;
        Booking expectedBooking = createValidBooking();
        expectedBooking.setBookingId(bookingId);
        bookingRepository.bookingToReturn = expectedBooking;
        
        // Act
        Booking result = bookingServiceImpl.getBooking(bookingId);
        
        // Assert
        assertEquals(expectedBooking, result);
        assertEquals(Integer.valueOf(bookingId), bookingRepository.lastQueriedBookingId);
    }
    
    @Test
    public void testGetPassengerBookings_ReturnsListFromRepository() {
        // Arrange
        int passengerId = 456;
        List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(createValidBooking());
        expectedBookings.add(createValidBooking());
        bookingRepository.passengerBookingsToReturn = expectedBookings;
        
        // Act
        List<Booking> result = bookingServiceImpl.getPassengerBookings(passengerId);
        
        // Assert
        assertEquals(expectedBookings, result);
        assertEquals(Integer.valueOf(passengerId), bookingRepository.lastQueriedPassengerId);
    }
    
    @Test
    public void testUpdateBooking_WithValidBooking_Success() {
        // Arrange
        Booking validBooking = createValidBooking();
        validBooking.setBookingId(1);
        bookingRepository.updateBookingResult = true;
        
        // Act
        boolean result = bookingServiceImpl.updateBooking(validBooking);
        
        // Assert
        assertTrue(result);
        assertEquals(validBooking, bookingRepository.lastUpdatedBooking);
    }
    
    @Test
    public void testUpdateBooking_WithInvalidBooking_Failure() {

        assertFalse(bookingServiceImpl.updateBooking(null));

        Booking booking1 = createValidBooking();
        booking1.setPassengerId(0);
        assertFalse(bookingServiceImpl.updateBooking(booking1));

        Booking booking2 = createValidBooking();
        booking2.setVehicleType("");
        assertFalse(bookingServiceImpl.updateBooking(booking2));
    
        Booking booking3 = createValidBooking();
        booking3.setPickupLocation(null);
        assertFalse(bookingServiceImpl.updateBooking(booking3));
    }
    
    @Test
    public void testCancelBooking_CallsRepositoryDelete() {
        // Arrange
        int bookingId = 789;
        bookingRepository.deleteBookingResult = true;
        
        // Act
        boolean result = bookingServiceImpl.cancelBooking(bookingId);
        
        // Assert
        assertTrue(result);
        assertEquals(Integer.valueOf(bookingId), bookingRepository.lastDeletedBookingId);
    }
    
    @Test
    public void testGetAllBookings_ReturnsRepositoryResult() {
        // Arrange
        List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(createValidBooking());
        expectedBookings.add(createValidBooking());
        bookingRepository.allBookingsToReturn = expectedBookings;
        
        // Act
        List<Booking> result = bookingServiceImpl.getAllBookings();
        
        // Assert
        assertEquals(expectedBookings, result);
        assertTrue(bookingRepository.getAllBookingsCalled);
    }
    
    @Test
    public void testGetBookingsByStatus_ReturnsRepositoryResult() {
        // Arrange
        String status = "PENDING";
        List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(createValidBooking());
        bookingRepository.statusBookingsToReturn = expectedBookings;
        
        // Act
        List<Booking> result = bookingServiceImpl.getBookingsByStatus(status);
        
        // Assert
        assertEquals(expectedBookings, result);
        assertEquals(status, bookingRepository.lastQueriedStatus);
    }
    
    @Test
    public void testAssignDriver_WithValidBookingAndDriver_Success() {
        // Arrange
        int bookingId = 123;
        int driverId = 456;
        Booking booking = createValidBooking();
        booking.setBookingId(bookingId);
        bookingRepository.bookingToReturn = booking;
        bookingRepository.updateBookingResult = true;
        
        // Act
        boolean result = bookingServiceImpl.assignDriver(bookingId, driverId);
        
        // Assert
        assertTrue(result);
        assertNotNull(bookingRepository.lastUpdatedBooking);
        assertEquals(Integer.valueOf(driverId), bookingRepository.lastUpdatedBooking.getDriverId());
        assertEquals("ASSIGNED", bookingRepository.lastUpdatedBooking.getStatus());
    }
    
    @Test
    public void testAssignDriver_WithNonExistentBooking_Failure() {
        // Arrange
        int bookingId = 999;
        int driverId = 456;
        bookingRepository.bookingToReturn = null; // Booking doesn't exist
        
        // Act
        boolean result = bookingServiceImpl.assignDriver(bookingId, driverId);
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    public void testUpdateBookingFee_CallsRepository() {
        // Arrange
        int bookingId = 123;
        double hireFee = 50.75;
        bookingRepository.updateFeeResult = true;
        
        // Act
        boolean result = bookingServiceImpl.updateBookingFee(bookingId, hireFee);
        
        // Assert
        assertTrue(result);
        assertEquals(Integer.valueOf(bookingId), bookingRepository.lastFeeUpdatedBookingId);
        assertEquals(Double.valueOf(hireFee), bookingRepository.lastUpdatedFee);
    }
    
    private Booking createValidBooking() {
        Booking booking = new Booking();
        booking.setPassengerId(1);
        booking.setVehicleType("SUV");
        booking.setPickupLocation("123 Main St");
        booking.setDropoffLocation("456 Oak Ave");
        booking.setBookingDateTime("2025-03-12T10:00:00");
        booking.setPaymentMethod("Credit Card");
        return booking;
    }
    

    private class TestBookingRepository implements BookingRepository {
        private List<Booking> bookingsSaved = new ArrayList<>();
        private Booking bookingToReturn;
        private List<Booking> passengerBookingsToReturn = new ArrayList<>();
        private List<Booking> allBookingsToReturn = new ArrayList<>();
        private List<Booking> statusBookingsToReturn = new ArrayList<>();
        
        private Booking lastUpdatedBooking;
        private Integer lastQueriedBookingId;
        private Integer lastQueriedPassengerId;
        private Integer lastDeletedBookingId;
        private String lastQueriedStatus;
        private Integer lastFeeUpdatedBookingId;
        private Double lastUpdatedFee;
        
        private boolean updateBookingResult = true;
        private boolean deleteBookingResult = true;
        private boolean updateFeeResult = true;
        private boolean getAllBookingsCalled = false;
        
        @Override
        public boolean saveBooking(Booking booking) {
            if (booking != null) {
                bookingsSaved.add(booking);
                return true;
            }
            return false;
        }
        
        @Override
        public Booking getBookingById(int bookingId) {
            lastQueriedBookingId = bookingId;
            return bookingToReturn;
        }
        
        @Override
        public List<Booking> getBookingsByPassengerId(int passengerId) {
            lastQueriedPassengerId = passengerId;
            return passengerBookingsToReturn;
        }
        
        @Override
        public boolean updateBooking(Booking booking) {
            lastUpdatedBooking = booking;
            return updateBookingResult;
        }
        
        @Override
        public boolean deleteBooking(int bookingId) {
            lastDeletedBookingId = bookingId;
            return deleteBookingResult;
        }
        
        @Override
        public List<Booking> getAllBookings() {
            getAllBookingsCalled = true;
            return allBookingsToReturn;
        }
        
        @Override
        public List<Booking> getBookingsByStatus(String status) {
            lastQueriedStatus = status;
            return statusBookingsToReturn;
        }
        
        @Override
        public boolean updateBookingFee(int bookingId, double hireFee) {
            lastFeeUpdatedBookingId = bookingId;
            lastUpdatedFee = hireFee;
            return updateFeeResult;
        }
        
        @Override
        public boolean assignDriver(int bookingId, int driverId) throws SQLException {
            return false;
        }
    }
}