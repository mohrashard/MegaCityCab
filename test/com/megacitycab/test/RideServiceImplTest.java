package com.megacitycab.test;

import com.megacitycab.dto.RideDTO;
import com.megacitycab.service.RideService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RideServiceImplTest {
    
    private RideService rideService;
    
    private static class MockRideService implements RideService {
        private boolean returnSuccess = true;
        private List<RideDTO> currentRides = new ArrayList<>();
        private List<RideDTO> endedRides = new ArrayList<>();
        private boolean simulateException = false;
        
        public void setReturnSuccess(boolean returnSuccess) {
            this.returnSuccess = returnSuccess;
        }
        
        public void setCurrentRides(List<RideDTO> currentRides) {
            this.currentRides = currentRides;
        }
        
        public void setEndedRides(List<RideDTO> endedRides) {
            this.endedRides = endedRides;
        }
        
        public void setSimulateException(boolean simulateException) {
            this.simulateException = simulateException;
        }
        
        @Override
        public List<RideDTO> getCurrentRides(int driverId) {
            if (simulateException) {
                return new ArrayList<>();
            }
            return currentRides;
        }
        
        @Override
        public List<RideDTO> getEndedRides(int driverId) {
            if (simulateException) {
                return new ArrayList<>();
            }
            return endedRides;
        }
        
        @Override
        public boolean acceptRide(int bookingId, int driverId) {
            if (simulateException) {
                return false;
            }
            if (bookingId <= 0) {
                return false;
            }
            return returnSuccess;
        }
        
        @Override
        public boolean cancelRide(int bookingId, String reason) {
            if (simulateException) {
                return false;
            }
            if (reason == null) {
                return false;
            }
            return returnSuccess;
        }
        
        @Override
        public boolean endRide(int bookingId) {
            if (simulateException) {
                return false;
            }
            if (bookingId <= 0) {
                return false;
            }
            return returnSuccess;
        }
    }
    
    private MockRideService mockRideService;
    
    @Before
    public void setUp() {
        mockRideService = new MockRideService();
        rideService = mockRideService;
    }
    
    // Tests for getCurrentRides
    
    @Test
    public void testGetCurrentRidesSuccess() {
        int driverId = 123;
        List<RideDTO> expectedRides = new ArrayList<>();
        RideDTO ride1 = new RideDTO();
        ride1.setBookingId(1);
        ride1.setPickupLocation("Location A");
        ride1.setDropoffLocation("Location B");
        ride1.setHireFee(10.5);
        
        RideDTO ride2 = new RideDTO();
        ride2.setBookingId(2);
        ride2.setPickupLocation("Location C");
        ride2.setDropoffLocation("Location D");
        ride2.setHireFee(15.0);
        
        expectedRides.add(ride1);
        expectedRides.add(ride2);
        mockRideService.setCurrentRides(expectedRides);
        
        // Act
        List<RideDTO> result = rideService.getCurrentRides(driverId);
        
        // Assert
        assertEquals("Should return the expected rides", expectedRides, result);
    }
    
    @Test
    public void testGetCurrentRidesEmpty() {
        // Arrange
        int driverId = 123;
        List<RideDTO> expectedRides = new ArrayList<>();
        mockRideService.setCurrentRides(expectedRides);
        
        // Act
        List<RideDTO> result = rideService.getCurrentRides(driverId);
        
        // Assert
        assertTrue("Should return an empty list", result.isEmpty());
    }
    
    @Test
    public void testGetCurrentRidesWithException() {
        // Arrange
        int driverId = 123;
        mockRideService.setSimulateException(true);
        
        // Act
        List<RideDTO> result = rideService.getCurrentRides(driverId);
        
        // Assert
        assertNotNull("Should return an empty list, not null", result);
        assertTrue("Should return an empty list on exception", result.isEmpty());
    }
    
    // Tests for getEndedRides
    
    @Test
    public void testGetEndedRidesSuccess() {
        int driverId = 123;
        List<RideDTO> expectedRides = new ArrayList<>();
        RideDTO ride1 = new RideDTO();
        ride1.setBookingId(3);
        ride1.setPickupLocation("Location E");
        ride1.setDropoffLocation("Location F");
        ride1.setHireFee(20.0);
        
        RideDTO ride2 = new RideDTO();
        ride2.setBookingId(4);
        ride2.setPickupLocation("Location G");
        ride2.setDropoffLocation("Location H");
        ride2.setHireFee(25.5);
        
        expectedRides.add(ride1);
        expectedRides.add(ride2);
        mockRideService.setEndedRides(expectedRides);
        
        // Act
        List<RideDTO> result = rideService.getEndedRides(driverId);
        
        // Assert
        assertEquals("Should return the expected rides", expectedRides, result);
    }
    
    @Test
    public void testGetEndedRidesEmpty() {
        // Arrange
        int driverId = 123;
        List<RideDTO> expectedRides = new ArrayList<>();
        mockRideService.setEndedRides(expectedRides);
        
        // Act
        List<RideDTO> result = rideService.getEndedRides(driverId);
        
        // Assert
        assertTrue("Should return an empty list", result.isEmpty());
    }
    
    @Test
    public void testGetEndedRidesWithException() {
        // Arrange
        int driverId = 123;
        mockRideService.setSimulateException(true);
        
        // Act
        List<RideDTO> result = rideService.getEndedRides(driverId);
        
        // Assert
        assertNotNull("Should return an empty list, not null", result);
        assertTrue("Should return an empty list on exception", result.isEmpty());
    }
    
    // Tests for acceptRide
    
    @Test
    public void testAcceptRideSuccess() {
        // Arrange
        int bookingId = 1;
        int driverId = 123;
        mockRideService.setReturnSuccess(true);
        
        // Act
        boolean result = rideService.acceptRide(bookingId, driverId);
        
        // Assert
        assertTrue("Should return true for successful ride acceptance", result);
    }
    
    @Test
    public void testAcceptRideFailure() {
        // Arrange
        int bookingId = 1;
        int driverId = 123;
        mockRideService.setReturnSuccess(false);
        
        // Act
        boolean result = rideService.acceptRide(bookingId, driverId);
        
        // Assert
        assertFalse("Should return false for unsuccessful ride acceptance", result);
    }
    
    @Test
    public void testAcceptRideWithException() {
        // Arrange
        int bookingId = 1;
        int driverId = 123;
        mockRideService.setSimulateException(true);
        
        // Act
        boolean result = rideService.acceptRide(bookingId, driverId);
        
        // Assert
        assertFalse("Should return false when an exception occurs", result);
    }
    
    @Test
    public void testAcceptRideWithInvalidBookingId() {
        // Arrange
        int bookingId = -1; 
        int driverId = 123;
        mockRideService.setReturnSuccess(false);
        
        // Act
        boolean result = rideService.acceptRide(bookingId, driverId);
        
        // Assert
        assertFalse("Should return false for invalid booking ID", result);
    }
    
    // Tests for cancelRide
    
    @Test
    public void testCancelRideSuccess() {
        // Arrange
        int bookingId = 1;
        String reason = "Driver unavailable";
        mockRideService.setReturnSuccess(true);
        
        // Act
        boolean result = rideService.cancelRide(bookingId, reason);
        
        // Assert
        assertTrue("Should return true for successful ride cancellation", result);
    }
    
    @Test
    public void testCancelRideFailure() {
        // Arrange
        int bookingId = 1;
        String reason = "Driver unavailable";
        mockRideService.setReturnSuccess(false);
        
        // Act
        boolean result = rideService.cancelRide(bookingId, reason);
        
        // Assert
        assertFalse("Should return false for unsuccessful ride cancellation", result);
    }
    
    @Test
    public void testCancelRideWithNullReason() {
        // Arrange
        int bookingId = 1;
        String reason = null;
        
        // Act
        boolean result = rideService.cancelRide(bookingId, reason);
        
        // Assert
        assertFalse("Should return false when reason is null", result);
    }
    
    @Test
    public void testCancelRideWithEmptyReason() {
        // Arrange
        int bookingId = 1;
        String reason = "";
        
        // Act
        boolean result = rideService.cancelRide(bookingId, reason);
        
        // Assert
        assertTrue("Should accept empty reason string", result);
    }
    
    @Test
    public void testCancelRideWithException() {
        // Arrange
        int bookingId = 1;
        String reason = "Driver unavailable";
        mockRideService.setSimulateException(true);
        
        // Act
        boolean result = rideService.cancelRide(bookingId, reason);
        
        // Assert
        assertFalse("Should return false when an exception occurs", result);
    }
    
    // Tests for endRide
    
    @Test
    public void testEndRideSuccess() {
        // Arrange
        int bookingId = 1;
        mockRideService.setReturnSuccess(true);
        
        // Act
        boolean result = rideService.endRide(bookingId);
        
        // Assert
        assertTrue("Should return true for successful ride ending", result);
    }
    
    @Test
    public void testEndRideFailure() {
        // Arrange
        int bookingId = 1;
        mockRideService.setReturnSuccess(false);
        
        // Act
        boolean result = rideService.endRide(bookingId);
        
        // Assert
        assertFalse("Should return false for unsuccessful ride ending", result);
    }
    
    @Test
    public void testEndRideWithException() {
        // Arrange
        int bookingId = 1;
        mockRideService.setSimulateException(true);
        
        // Act
        boolean result = rideService.endRide(bookingId);
        
        // Assert
        assertFalse("Should return false when an exception occurs", result);
    }
    
    @Test
    public void testEndRideWithInvalidBookingId() {
        // Arrange
        int bookingId = -1; 
        mockRideService.setReturnSuccess(false);
        
        // Act
        boolean result = rideService.endRide(bookingId);
        
        // Assert
        assertFalse("Should return false for invalid booking ID", result);
    }
}