package com.megacitycab.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.megacitycab.dao.DriverDAO;
import com.megacitycab.model.Driver;
import com.megacitycab.service.DriverService;

public class DriverServiceTest {
    
    private DriverService driverService;
    private TestDriverDAO testDriverDAO;
    
    @Before
    public void setUp() {
        // Create a test version of DriverDAO that we can control
        testDriverDAO = new TestDriverDAO();
        
        // Set up the service with our test DAO
        driverService = new DriverService() {
            {
                this.driverDAO = testDriverDAO;
            }
        };
    }
    
    @Test
    public void testRegisterDriver_Success() {
        // Arrange
        Driver driver = new Driver("John Doe", "john@example.com", "1234567890", "DL123456", "Sedan");
        String password = "password123";
        
        // Act
        boolean result = driverService.registerDriver(driver, password);
        
        // Assert
        assertTrue("Driver registration should succeed", result);
        assertNotNull("Driver password should be set", driver.getPassword());
        assertNotNull("Driver salt should be set", driver.getSalt());
        assertEquals("Driver should be saved", driver, testDriverDAO.savedDriver);
    }
    
    @Test
    public void testRegisterDriver_HandlesException() {
        // Arrange
        Driver driver = new Driver("John Doe", "john@example.com", "1234567890", "DL123456", "Sedan");
        String password = "password123";
        testDriverDAO.throwExceptionOnSave = true;
        
        // Act
        boolean result = driverService.registerDriver(driver, password);
        
        // Assert
        assertFalse("Driver registration should fail when exception occurs", result);
    }
    
    @Test
    public void testGetDriverByEmail_Found() {
        // Arrange
        String email = "john@example.com";
        Driver expectedDriver = new Driver("John Doe", email, "1234567890", "DL123456", "Sedan");
        testDriverDAO.driverByEmail = expectedDriver;
        
        // Act
        Driver result = driverService.getDriverByEmail(email);
        
        // Assert
        assertEquals("Should return the driver with matching email", expectedDriver, result);
        assertEquals("Should query with correct email", email, testDriverDAO.lastQueriedEmail);
    }
    
    @Test
    public void testGetDriverByEmail_NotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        testDriverDAO.driverByEmail = null;
        
        // Act
        Driver result = driverService.getDriverByEmail(email);
        
        // Assert
        assertNull("Should return null when driver not found", result);
        assertEquals("Should query with correct email", email, testDriverDAO.lastQueriedEmail);
    }
    
    @Test
    public void testIsEmailTaken_True() {
        // Arrange
        String email = "taken@example.com";
        testDriverDAO.driverByEmail = new Driver("John Doe", email, "1234567890", "DL123456", "Sedan");
        
        // Act
        boolean result = driverService.isEmailTaken(email);
        
        // Assert
        assertTrue("Email should be reported as taken", result);
        assertEquals("Should query with correct email", email, testDriverDAO.lastQueriedEmail);
    }
    
    @Test
    public void testIsEmailTaken_False() {
        // Arrange
        String email = "available@example.com";
        testDriverDAO.driverByEmail = null;
        
        // Act
        boolean result = driverService.isEmailTaken(email);
        
        // Assert
        assertFalse("Email should be reported as available", result);
        assertEquals("Should query with correct email", email, testDriverDAO.lastQueriedEmail);
    }
    
    @Test
    public void testIsPhoneTaken_True() {
        // Arrange
        String phone = "1234567890";
        testDriverDAO.driverByPhone = new Driver("John Doe", "john@example.com", phone, "DL123456", "Sedan");
        
        // Act
        boolean result = driverService.isPhoneTaken(phone);
        
        // Assert
        assertTrue("Phone should be reported as taken", result);
        assertEquals("Should query with correct phone", phone, testDriverDAO.lastQueriedPhone);
    }
    
    @Test
    public void testIsPhoneTaken_False() {
        // Arrange
        String phone = "9876543210";
        testDriverDAO.driverByPhone = null;
        
        // Act
        boolean result = driverService.isPhoneTaken(phone);
        
        // Assert
        assertFalse("Phone should be reported as available", result);
        assertEquals("Should query with correct phone", phone, testDriverDAO.lastQueriedPhone);
    }
    
    @Test
    public void testIsLicenseTaken_True() {
        // Arrange
        String license = "DL123456";
        testDriverDAO.driverByLicense = new Driver("John Doe", "john@example.com", "1234567890", license, "Sedan");
        
        // Act
        boolean result = driverService.isLicenseTaken(license);
        
        // Assert
        assertTrue("License should be reported as taken", result);
        assertEquals("Should query with correct license", license, testDriverDAO.lastQueriedLicense);
    }
    
    @Test
    public void testIsLicenseTaken_False() {
        // Arrange
        String license = "DL654321";
        testDriverDAO.driverByLicense = null;
        
        // Act
        boolean result = driverService.isLicenseTaken(license);
        
        // Assert
        assertFalse("License should be reported as available", result);
        assertEquals("Should query with correct license", license, testDriverDAO.lastQueriedLicense);
    }
    
    @Test
    public void testRegisterDriver_NullDriver() {
        // Arrange
        Driver driver = null;
        String password = "password123";
        
        // Act
        boolean result = driverService.registerDriver(driver, password);
        
        // Assert
        assertFalse("Should fail when driver is null", result);
    }
    
    @Test
    public void testRegisterDriver_NullPassword() {
        // Arrange
        Driver driver = new Driver("John Doe", "john@example.com", "1234567890", "DL123456", "Sedan");
        String password = null;
        
        // Act
        boolean result = driverService.registerDriver(driver, password);
        
        // Assert
        assertFalse("Should fail when password is null", result);
    }
    
    @Test
    public void testGetDriverByEmail_NullEmail() {
        // Arrange
        String email = null;
        
        // Act
        Driver result = driverService.getDriverByEmail(email);
        
        // Assert
        assertNull("Should return null when email is null", result);
    }
    
    // Test DriverDAO implementation for testing
    private static class TestDriverDAO extends DriverDAO {
        Driver savedDriver;
        Driver driverByEmail;
        Driver driverByPhone;
        Driver driverByLicense;
        String lastQueriedEmail;
        String lastQueriedPhone;
        String lastQueriedLicense;
        boolean throwExceptionOnSave;
        
        @Override
        public void saveDriver(Driver driver) {
            if (throwExceptionOnSave) {
                throw new RuntimeException("Test exception");
            }
            this.savedDriver = driver;
        }
        
        @Override
        public Driver getDriverByEmail(String email) {
            this.lastQueriedEmail = email;
            return driverByEmail;
        }
        
        @Override
        public Driver getDriverByPhone(String phone) {
            this.lastQueriedPhone = phone;
            return driverByPhone;
        }
        
        @Override
        public Driver getDriverByLicense(String licenseNo) {
            this.lastQueriedLicense = licenseNo;
            return driverByLicense;
        }
    }
}