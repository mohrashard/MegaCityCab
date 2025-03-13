package com.megacitycab.test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.megacitycab.model.Admin;
import com.megacitycab.service.AdminService;
import com.megacitycab.util.PasswordUtil;

public class AdminServiceTest {
    
    private AdminService adminService;
    
    @Before
    public void setUp() {
        adminService = new AdminService();
    }
    
    @Test
    public void testIsUsernameTaken_WithNonExistingUsername() {
        String username = "newUser" + System.currentTimeMillis();
        
        // Act
        boolean result = adminService.isUsernameTaken(username);
        
        // Assert
        assertFalse("Username should not be taken", result);
    }
    
    @Test
    public void testIsUsernameTaken_WithExistingUsername() {
        String username = "existingUser" + System.currentTimeMillis();
        String adminName = "Existing Admin";
        Admin admin = new Admin(username, adminName);
        
 
        boolean registered = adminService.registerAdmin(admin, "password123");
        assertTrue("Setup failed: Could not register test admin", registered);
        
        // Act
        boolean result = adminService.isUsernameTaken(username);
        
        // Assert
        assertTrue("Username should be taken", result);
    }
    
    @Test
    public void testRegisterAdmin_WithNewUsername() {
        String username = "testAdmin" + System.currentTimeMillis();
        String adminName = "Test Admin";
        String password = "securePassword123";
        Admin admin = new Admin(username, adminName);
        
        // Act
        boolean result = adminService.registerAdmin(admin, password);
        
        // Assert
        assertTrue("Admin registration should succeed", result);
        
        Admin savedAdmin = adminService.getAdminByUsername(username);
        assertNotNull("Retrieved admin should not be null", savedAdmin);
        assertEquals("Username should match", username, savedAdmin.getUsername());
        assertEquals("Admin name should match", adminName, savedAdmin.getAdminName());
        assertNotNull("Password should not be null", savedAdmin.getPassword());
        assertNotNull("Salt should not be null", savedAdmin.getSalt());
    }
    
    @Test
    public void testRegisterAdmin_WithExistingUsername() {
        String baseUsername = "duplicateUser" + System.currentTimeMillis();
        String adminName = "First Admin";
        String password = "password123";
        Admin firstAdmin = new Admin(baseUsername, adminName);
        
        boolean firstRegistered = adminService.registerAdmin(firstAdmin, password);
        assertTrue("Setup failed: Could not register first test admin", firstRegistered);
        
        Admin secondAdmin = new Admin(baseUsername, "Second Admin");
        
        // Act
        boolean result = adminService.registerAdmin(secondAdmin, "anotherPassword");
        
        // Assert
        assertFalse("Registration with duplicate username should fail", result);
    }
    
    @Test
    public void testRegisterAdmin_WithNullAdmin() {
        // Act & Assert
        try {
            boolean result = adminService.registerAdmin(null, "password");
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {

        }
    }
    
    @Test
    public void testRegisterAdmin_WithNullPassword() {
        // Arrange
        Admin admin = new Admin("nullPasswordUser" + System.currentTimeMillis(), "Null Password User");
        
        // Act
        boolean result = adminService.registerAdmin(admin, null);
        
        // Assert
        assertFalse("Registration with null password should fail", result);
    }
    
    @Test
    public void testRegisterAdmin_WithEmptyPassword() {
        // Arrange
        Admin admin = new Admin("emptyPasswordUser" + System.currentTimeMillis(), "Empty Password User");
        
        // Act
        boolean result = adminService.registerAdmin(admin, "");
        
        // Assert
        assertTrue("Registration with empty password should succeed", result);
    }
    
    @Test
    public void testGetAdminByUsername_WithExistingUsername() {
        String username = "retrieveUser" + System.currentTimeMillis();
        String adminName = "Retrieve Test User";
        String password = "retrievePassword";
        Admin admin = new Admin(username, adminName);
        
        boolean registered = adminService.registerAdmin(admin, password);
        assertTrue("Setup failed: Could not register test admin", registered);
        
        // Act
        Admin retrievedAdmin = adminService.getAdminByUsername(username);
        
        // Assert
        assertNotNull("Retrieved admin should not be null", retrievedAdmin);
        assertEquals("Username should match", username, retrievedAdmin.getUsername());
        assertEquals("Admin name should match", adminName, retrievedAdmin.getAdminName());
    }
    
    @Test
    public void testGetAdminByUsername_WithNonExistingUsername() {
        String username = "nonExistingUser" + System.currentTimeMillis();
        
        // Act
        Admin retrievedAdmin = adminService.getAdminByUsername(username);
        
        // Assert
        assertNull("Retrieved admin should be null for non-existing username", retrievedAdmin);
    }
    
    @Test
    public void testGetAdminByUsername_WithNullUsername() {
        // Act
        Admin retrievedAdmin = adminService.getAdminByUsername(null);
        
        // Assert
        assertNull("Retrieved admin should be null for null username", retrievedAdmin);
    }
    
    @Test
    public void testPasswordHashing() {
        String username = "hashTestUser" + System.currentTimeMillis();
        String adminName = "Hash Test User";
        String password = "testPassword123";
        Admin admin = new Admin(username, adminName);
        
        // Act
        boolean registered = adminService.registerAdmin(admin, password);
        assertTrue("Setup failed: Could not register test admin", registered);
        
        Admin savedAdmin = adminService.getAdminByUsername(username);
        
        // Assert
        assertNotNull("Saved admin should not be null", savedAdmin);
        assertNotNull("Password should not be null", savedAdmin.getPassword());
        assertNotNull("Salt should not be null", savedAdmin.getSalt());
        

        String salt = savedAdmin.getSalt();
        String hashedPassword = savedAdmin.getPassword();
        String rehashedPassword = PasswordUtil.hashPassword(password, salt);
        
        assertEquals("Rehashed password should match stored hash", hashedPassword, rehashedPassword);
        assertNotEquals("Stored password should not be plaintext", password, hashedPassword);
    }
}