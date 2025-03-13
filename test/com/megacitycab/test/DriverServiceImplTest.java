package com.megacitycab.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.megacitycab.model.DriverInfo;
import com.megacitycab.repository.DriverRepository;
import com.megacitycab.service.DriverServiceImpl;

public class DriverServiceImplTest {
    
    private DriverServiceImpl driverService;
    private TestDriverRepository testRepository;
    
    @Before
    public void setUp() {
        // Create a test repository that we can control
        testRepository = new TestDriverRepository();
        
        // Set up the service with our test repository
        driverService = new DriverServiceImpl(testRepository);
    }
    
    @Test
    public void testGetDriversByVehicleType_Found() {
        // Arrange
        String vehicleType = "Sedan";
        List<DriverInfo> expectedDrivers = new ArrayList<>();
        expectedDrivers.add(new TestDriverInfo(1, "John Doe", "Sedan"));
        expectedDrivers.add(new TestDriverInfo(2, "Jane Smith", "Sedan"));
        testRepository.driversByVehicleType = expectedDrivers;
        
        // Act
        List<DriverInfo> result = driverService.getDriversByVehicleType(vehicleType);
        
        // Assert
        assertEquals("Should return the correct number of drivers", expectedDrivers.size(), result.size());
        assertEquals("Should return the correct drivers", expectedDrivers, result);
        assertEquals("Should query with correct vehicle type", vehicleType, testRepository.lastQueriedVehicleType);
    }
    
    @Test
    public void testGetDriversByVehicleType_NotFound() {
        // Arrange
        String vehicleType = "SUV";
        List<DriverInfo> emptyList = new ArrayList<>();
        testRepository.driversByVehicleType = emptyList;
        
        // Act
        List<DriverInfo> result = driverService.getDriversByVehicleType(vehicleType);
        
        // Assert
        assertTrue("Should return an empty list when no drivers found", result.isEmpty());
        assertEquals("Should query with correct vehicle type", vehicleType, testRepository.lastQueriedVehicleType);
    }
    
    @Test
    public void testGetDriverById_Found() {
        // Arrange
        int driverId = 1;
        DriverInfo expectedDriver = new TestDriverInfo(driverId, "John Doe", "Sedan");
        testRepository.driverById = expectedDriver;
        
        // Act
        DriverInfo result = driverService.getDriverById(driverId);
        
        // Assert
        assertEquals("Should return the driver with matching ID", expectedDriver, result);
        assertEquals("Should query with correct ID", driverId, testRepository.lastQueriedDriverId);
    }
    
    @Test
    public void testGetDriverById_NotFound() {
        // Arrange
        int driverId = 999;
        testRepository.driverById = null;
        
        // Act
        DriverInfo result = driverService.getDriverById(driverId);
        
        // Assert
        assertNull("Should return null when driver not found", result);
        assertEquals("Should query with correct ID", driverId, testRepository.lastQueriedDriverId);
    }
    
    @Test
    public void testGetAllDrivers_Found() {
        // Arrange
        List<DriverInfo> expectedDrivers = new ArrayList<>();
        expectedDrivers.add(new TestDriverInfo(1, "John Doe", "Sedan"));
        expectedDrivers.add(new TestDriverInfo(2, "Jane Smith", "SUV"));
        testRepository.allDrivers = expectedDrivers;
        
        // Act
        List<DriverInfo> result = driverService.getAllDrivers();
        
        // Assert
        assertEquals("Should return the correct number of drivers", expectedDrivers.size(), result.size());
        assertEquals("Should return the correct drivers", expectedDrivers, result);
    }
    
    @Test
    public void testGetAllDrivers_Empty() {
        // Arrange
        List<DriverInfo> emptyList = new ArrayList<>();
        testRepository.allDrivers = emptyList;
        
        // Act
        List<DriverInfo> result = driverService.getAllDrivers();
        
        // Assert
        assertTrue("Should return an empty list when no drivers found", result.isEmpty());
    }
    
    @Test
    public void testDefaultConstructor() {
        // Act
        DriverServiceImpl service = new DriverServiceImpl();
        
        // Assert
        assertNotNull("Service should be created with default constructor", service);
    }
    
    @Test
    public void testGetDriversByVehicleType_NullVehicleType() {
        // Arrange
        String vehicleType = null;
        
        // Act
        List<DriverInfo> result = driverService.getDriversByVehicleType(vehicleType);
        
        // Assert
        assertNotNull("Should not return null for null vehicle type", result);
        assertTrue("Should return empty list for null vehicle type", result.isEmpty());
        assertEquals("Should query with null vehicle type", vehicleType, testRepository.lastQueriedVehicleType);
    }
    
    @Test
    public void testGetDriverById_InvalidId() {
        // Arrange
        int driverId = -1;
        testRepository.driverById = null;
        
        // Act
        DriverInfo result = driverService.getDriverById(driverId);
        
        // Assert
        assertNull("Should return null for invalid ID", result);
        assertEquals("Should query with invalid ID", driverId, testRepository.lastQueriedDriverId);
    }
    
    // Test DriverRepository implementation for testing
    private static class TestDriverRepository implements DriverRepository {
        List<DriverInfo> driversByVehicleType;
        DriverInfo driverById;
        List<DriverInfo> allDrivers;
        String lastQueriedVehicleType;
        int lastQueriedDriverId;
        
        @Override
        public List<DriverInfo> getDriversByVehicleType(String vehicleType) {
            this.lastQueriedVehicleType = vehicleType;
            return vehicleType == null ? new ArrayList<>() : driversByVehicleType;
        }
        
        @Override
        public DriverInfo getDriverById(int id) {
            this.lastQueriedDriverId = id;
            return driverById;
        }
        
        @Override
        public List<DriverInfo> getAllDrivers() {
            return allDrivers;
        }
    }
    
    // Test DriverInfo implementation for testing
    private static class TestDriverInfo extends DriverInfo {
        private int id;
        private String name;
        private String vehicleType;
        
        public TestDriverInfo(int id, String name, String vehicleType) {
            this.id = id;
            this.name = name;
            this.vehicleType = vehicleType;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestDriverInfo that = (TestDriverInfo) obj;
            return id == that.id;
        }
    }
}