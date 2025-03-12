package com.megacitycab.test;

import com.megacitycab.dao.PassengerDAO;
import com.megacitycab.model.Passenger;
import com.megacitycab.service.PassengerService;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class PassengerServiceTest {
    
    private PassengerService passengerService;
    private TestPassengerDAO testPassengerDAO;
    
    @Before
    public void setUp() throws Exception {
        // Create service with our test DAO injected via reflection
        passengerService = new PassengerService();
        testPassengerDAO = new TestPassengerDAO();
        
        // Use reflection to replace the DAO with our test version
        Field daoField = PassengerService.class.getDeclaredField("passengerDAO");
        daoField.setAccessible(true);
        daoField.set(passengerService, testPassengerDAO);
    }
    
    @Test
    public void testRegisterPassenger_Success() {
        // Arrange
        Passenger passenger = new Passenger("John Doe", "john@example.com", "1234567890", 
                                           null, "NIC123456", "123 Main St");
        
        // Act
        boolean result = passengerService.registerPassenger(passenger, "password123");
        
        // Assert
        assertTrue("Registration should be successful", result);
        assertNotNull("Password should be set", passenger.getPassword());
        assertNotNull("Salt should be set", passenger.getSalt());
        assertEquals("Passenger should be saved", passenger, testPassengerDAO.savedPassenger);
    }
    
    @Test
    public void testRegisterPassenger_HandlesException() {
        // Arrange
        testPassengerDAO.throwExceptionOnSave = true;
        Passenger passenger = new Passenger("Jane Doe", "jane@example.com", "0987654321", 
                                           null, "NIC654321", "456 Oak St");
        
        // Act
        boolean result = passengerService.registerPassenger(passenger, "password456");
        
        // Assert
        assertFalse("Registration should fail when DAO throws exception", result);
    }
    
    @Test
    public void testRegisterPassenger_NullPassword() {
        // Arrange
        Passenger passenger = new Passenger("Bob Smith", "bob@example.com", "5555555555", 
                                           null, "NIC555555", "789 Pine St");
        
        // Act & Assert
        // Since we can't pass null to hashPassword method, we're expecting it to return false
        boolean result = passengerService.registerPassenger(passenger, null);
        assertFalse("Registration should fail with null password", result);
    }
    
    @Test
    public void testIsEmailTaken_WhenEmailExists() {
        // Arrange
        String email = "existing@example.com";
        Passenger existingPassenger = new Passenger("Test User", email, "1234567890", 
                                                 "hashedPassword", "NIC123456", "123 Test St");
        testPassengerDAO.existingEmail = email;
        testPassengerDAO.passengerToReturn = existingPassenger;
        
        // Act
        boolean result = passengerService.isEmailTaken(email);
        
        // Assert
        assertTrue("Should return true for existing email", result);
    }
    
    @Test
    public void testIsEmailTaken_WhenEmailDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";
        testPassengerDAO.existingEmail = "different@example.com"; // Different email
        
        // Act
        boolean result = passengerService.isEmailTaken(email);
        
        // Assert
        assertFalse("Should return false for non-existing email", result);
    }
    
    @Test
    public void testIsEmailTaken_WithNullEmail() {
        // Act
        boolean result = passengerService.isEmailTaken(null);
        
        // Assert
        assertFalse("Should return false for null email", result);
    }
    
    @Test
    public void testIsPhoneTaken_WhenPhoneExists() {
        // Arrange
        String phone = "1234567890";
        Passenger existingPassenger = new Passenger("Test User", "test@example.com", phone, 
                                                 "hashedPassword", "NIC123456", "123 Test St");
        testPassengerDAO.existingPhone = phone;
        testPassengerDAO.passengerToReturn = existingPassenger;
        
        // Act
        boolean result = passengerService.isPhoneTaken(phone);
        
        // Assert
        assertTrue("Should return true for existing phone", result);
    }
    
    @Test
    public void testIsPhoneTaken_WhenPhoneDoesNotExist() {
        // Arrange
        String phone = "9876543210";
        testPassengerDAO.existingPhone = "1112223333"; // Different phone
        
        // Act
        boolean result = passengerService.isPhoneTaken(phone);
        
        // Assert
        assertFalse("Should return false for non-existing phone", result);
    }
    
    @Test
    public void testIsPhoneTaken_WithNullPhone() {
        // Act
        boolean result = passengerService.isPhoneTaken(null);
        
        // Assert
        assertFalse("Should return false for null phone", result);
    }
    
    @Test
    public void testIsNicTaken_WhenNicExists() {
        // Arrange
        String nic = "NIC123456";
        Passenger existingPassenger = new Passenger("Test User", "test@example.com", "1234567890", 
                                                 "hashedPassword", nic, "123 Test St");
        testPassengerDAO.existingNic = nic;
        testPassengerDAO.passengerToReturn = existingPassenger;
        
        // Act
        boolean result = passengerService.isNicTaken(nic);
        
        // Assert
        assertTrue("Should return true for existing NIC", result);
    }
    
    @Test
    public void testIsNicTaken_WhenNicDoesNotExist() {
        // Arrange
        String nic = "NIC654321";
        testPassengerDAO.existingNic = "NIC999999"; // Different NIC
        
        // Act
        boolean result = passengerService.isNicTaken(nic);
        
        // Assert
        assertFalse("Should return false for non-existing NIC", result);
    }
    
    @Test
    public void testIsNicTaken_WithNullNic() {
        // Act
        boolean result = passengerService.isNicTaken(null);
        
        // Assert
        assertFalse("Should return false for null NIC", result);
    }
    
    @Test
    public void testGetPassengerByEmail_WhenEmailExists() {
        // Arrange
        String email = "existing@example.com";
        Passenger expectedPassenger = new Passenger("Test User", email, "1234567890", 
                                                  "hashedPassword", "NIC123456", "123 Test St");
        testPassengerDAO.existingEmail = email;
        testPassengerDAO.passengerToReturn = expectedPassenger;
        
        // Act
        Passenger result = passengerService.getPassengerByEmail(email);
        
        // Assert
        assertNotNull("Should return passenger for existing email", result);
        assertEquals("Should return the correct passenger", expectedPassenger, result);
    }
    
    @Test
    public void testGetPassengerByEmail_WhenEmailDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";
        testPassengerDAO.existingEmail = "different@example.com"; // Different email
        
        // Act
        Passenger result = passengerService.getPassengerByEmail(email);
        
        // Assert
        assertNull("Should return null for non-existing email", result);
    }
    
    @Test
    public void testGetPassengerByEmail_WithNullEmail() {
        // Act
        Passenger result = passengerService.getPassengerByEmail(null);
        
        // Assert
        assertNull("Should return null for null email", result);
    }
    

    private class TestPassengerDAO extends PassengerDAO {
        Passenger savedPassenger;
        Passenger passengerToReturn;
        String existingEmail;
        String existingPhone;
        String existingNic;
        boolean throwExceptionOnSave;
        
        @Override
        public void savePassenger(Passenger passenger) {
            if (throwExceptionOnSave) {
                throw new RuntimeException("Test exception");
            }
            this.savedPassenger = passenger;
        }
        
        @Override
        public Passenger getPassengerByEmail(String email) {
            if (email != null && email.equals(existingEmail)) {
                return passengerToReturn;
            }
            return null;
        }
        
        @Override
        public Passenger getPassengerByPhone(String phone) {
            if (phone != null && phone.equals(existingPhone)) {
                return passengerToReturn;
            }
            return null;
        }
        
        @Override
        public Passenger getPassengerByNic(String nic) {
            if (nic != null && nic.equals(existingNic)) {
                return passengerToReturn;
            }
            return null;
        }
    }
}