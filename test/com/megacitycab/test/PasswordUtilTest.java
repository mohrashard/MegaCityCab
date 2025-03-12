package com.megacitycab.test;

import com.megacitycab.util.PasswordUtil;
import org.junit.Test;
import static org.junit.Assert.*;

public class PasswordUtilTest {

    @Test
    public void testGenerateSalt_ReturnsDifferentValues() {
        // Act
        String salt1 = PasswordUtil.generateSalt();
        String salt2 = PasswordUtil.generateSalt();
        
        // Assert
        assertNotNull("Salt should not be null", salt1);
        assertNotNull("Salt should not be null", salt2);
        assertFalse("Generated salts should be different", salt1.equals(salt2));
    }
    
    @Test
    public void testHashPassword_SameInputSameSalt_ReturnsSameHash() {
        // Arrange
        String password = "testPassword123";
        String salt = "testSalt";
        
        // Act
        String hash1 = PasswordUtil.hashPassword(password, salt);
        String hash2 = PasswordUtil.hashPassword(password, salt);
        
        // Assert
        assertNotNull("Hash should not be null", hash1);
        assertEquals("Same password with same salt should produce same hash", hash1, hash2);
    }
    
    @Test
    public void testHashPassword_DifferentInputSameSalt_ReturnsDifferentHash() {
        // Arrange
        String password1 = "testPassword123";
        String password2 = "differentPassword456";
        String salt = "testSalt";
        
        // Act
        String hash1 = PasswordUtil.hashPassword(password1, salt);
        String hash2 = PasswordUtil.hashPassword(password2, salt);
        
        // Assert
        assertNotNull("Hash should not be null", hash1);
        assertNotNull("Hash should not be null", hash2);
        assertFalse("Different passwords with same salt should produce different hashes", hash1.equals(hash2));
    }
    

    
    @Test
    public void testHashPassword_NullPassword_ThrowsException() {
        // Arrange
        String password = null;
        String salt = "testSalt";
        
        // Act & Assert
        try {
            PasswordUtil.hashPassword(password, salt);
            fail("Expected NullPointerException was not thrown");
        } catch (NullPointerException e) {
            // Expected exception
        }
    }
    
    @Test
    public void testHashPassword_NullSalt_ThrowsException() {
        // Arrange
        String password = "testPassword123";
        String salt = null;
        
        // Act & Assert
        try {
            PasswordUtil.hashPassword(password, salt);
            fail("Expected NullPointerException was not thrown");
        } catch (NullPointerException e) {
            // Expected exception
        }
    }
}