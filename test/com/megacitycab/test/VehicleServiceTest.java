
package com.megacitycab.test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.megacitycab.model.Vehicle;
import com.megacitycab.repository.VehicleRepository;
import com.megacitycab.service.VehicleService;
import com.megacitycab.service.VehicleServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class VehicleServiceTest {
    
    private VehicleService vehicleService;
    private TestVehicleRepository testRepository;
    
    @Before
    public void setUp() {
        // Create test repository with predefined data
        testRepository = new TestVehicleRepository();
        // Initialize service with test repository
        vehicleService = new VehicleServiceImpl(testRepository);
    }
    
    @Test
    public void testAddVehicle_ValidVehicle() {
        // Arrange
        Vehicle vehicle = new Vehicle("ABC123", "Sedan", 4, "Toyota", "Camry");
        
        // Act
        boolean result = vehicleService.addVehicle(vehicle);
        
        // Assert
        assertTrue("Adding a valid vehicle should return true", result);
        Vehicle addedVehicle = testRepository.getVehicleByPlate("ABC123");
        assertNotNull("Vehicle should be stored in repository", addedVehicle);
        assertEquals("Plate number should match", "ABC123", addedVehicle.getPlateNumber());
    }
    
    @Test
    public void testAddVehicle_NullVehicle() {
        // Act
        boolean result = vehicleService.addVehicle(null);
        
        // Assert
        assertFalse("Adding a null vehicle should return false", result);
    }
    
    @Test
    public void testGetVehicleById_ExistingId() {
        // Act
        Vehicle vehicle = vehicleService.getVehicleById(1);
        
        // Assert
        assertNotNull("Should return a vehicle for existing ID", vehicle);
        assertEquals("Should return the correct vehicle", 1, vehicle.getId());
    }
    
    @Test
    public void testGetVehicleById_NonExistingId() {
        // Act
        Vehicle vehicle = vehicleService.getVehicleById(999);
        
        // Assert
        assertNull("Should return null for non-existing ID", vehicle);
    }
    
    @Test
    public void testGetAllVehicles() {
        // Act
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        
        // Assert
        assertNotNull("Should return a list of vehicles", vehicles);
        assertEquals("Should return all vehicles in repository", 3, vehicles.size());
    }
    
    @Test
    public void testSearchVehicles_MatchingKeyword() {
        // Act
        List<Vehicle> vehicles = vehicleService.searchVehicles("Toyota");
        
        // Assert
        assertNotNull("Should return a list of vehicles", vehicles);
        assertEquals("Should return vehicles matching the keyword", 2, vehicles.size());
        assertEquals("First vehicle should match", "Toyota", vehicles.get(0).getBrand());
    }
    
    @Test
    public void testSearchVehicles_NonMatchingKeyword() {
        // Act
        List<Vehicle> vehicles = vehicleService.searchVehicles("NonExistingBrand");
        
        // Assert
        assertNotNull("Should return an empty list", vehicles);
        assertEquals("Should return no vehicles for non-matching keyword", 0, vehicles.size());
    }
    
    @Test
    public void testFilterVehiclesByType_ExistingType() {
        // Act
        List<Vehicle> vehicles = vehicleService.filterVehiclesByType("Sedan");
        
        // Assert
        assertNotNull("Should return a list of vehicles", vehicles);
        assertEquals("Should return vehicles of specified type", 2, vehicles.size());
        assertEquals("All vehicles should match the type", "Sedan", vehicles.get(0).getVehicleType());
    }
    
    @Test
    public void testFilterVehiclesByType_NonExistingType() {
        // Act
        List<Vehicle> vehicles = vehicleService.filterVehiclesByType("NonExistingType");
        
        // Assert
        assertNotNull("Should return an empty list", vehicles);
        assertEquals("Should return no vehicles for non-existing type", 0, vehicles.size());
    }
    
    @Test
    public void testFilterVehiclesByStatus_ExistingStatus() {
        // Act
        List<Vehicle> vehicles = vehicleService.filterVehiclesByStatus("Assigned");
        
        // Assert
        assertNotNull("Should return a list of vehicles", vehicles);
        assertEquals("Should return vehicles with specified status", 1, vehicles.size());
        assertEquals("All vehicles should match the status", "Assigned", vehicles.get(0).getStatus());
    }
    
    @Test
    public void testFilterVehiclesByStatus_NonExistingStatus() {
        // Act
        List<Vehicle> vehicles = vehicleService.filterVehiclesByStatus("NonExistingStatus");
        
        // Assert
        assertNotNull("Should return an empty list", vehicles);
        assertEquals("Should return no vehicles for non-existing status", 0, vehicles.size());
    }
    
    @Test
    public void testUpdateVehicle_ExistingVehicle() {
        // Arrange
        Vehicle vehicle = vehicleService.getVehicleById(1);
        assertNotNull("Test setup failed: Vehicle not found", vehicle);
        vehicle.setModel("Updated Model");
        
        // Act
        boolean result = vehicleService.updateVehicle(vehicle);
        
        // Assert
        assertTrue("Updating an existing vehicle should return true", result);
        Vehicle updatedVehicle = vehicleService.getVehicleById(1);
        assertEquals("Model should be updated", "Updated Model", updatedVehicle.getModel());
    }
    
    @Test
    public void testUpdateVehicle_NonExistingVehicle() {
        // Arrange
        Vehicle vehicle = new Vehicle("XYZ999", "SUV", 6, "Honda", "CRV");
        vehicle.setId(999); // Non-existing ID
        
        // Act
        boolean result = vehicleService.updateVehicle(vehicle);
        
        // Assert
        assertFalse("Updating a non-existing vehicle should return false", result);
    }
    
    @Test
    public void testDeleteVehicle_ExistingId() {
        // Act
        boolean result = vehicleService.deleteVehicle(1);
        
        // Assert
        assertTrue("Deleting an existing vehicle should return true", result);
        Vehicle deletedVehicle = vehicleService.getVehicleById(1);
        assertNull("Vehicle should be removed from repository", deletedVehicle);
    }
    
    @Test
    public void testDeleteVehicle_NonExistingId() {
        // Act
        boolean result = vehicleService.deleteVehicle(999);
        
        // Assert
        assertFalse("Deleting a non-existing vehicle should return false", result);
    }
    
    @Test
    public void testDeleteVehicle_WithAssignedDriver() {
        // Arrange
        Vehicle vehicle = vehicleService.getVehicleById(3);
        assertNotNull("Test setup failed: Vehicle not found", vehicle);
        assertNotNull("Test setup failed: Vehicle should have a driver", vehicle.getDriverId());
        
        // Act
        boolean result = vehicleService.deleteVehicle(3);
        
        // Assert
        assertTrue("Deleting an assigned vehicle should return true", result);
        Vehicle deletedVehicle = vehicleService.getVehicleById(3);
        assertNull("Vehicle should be removed from repository", deletedVehicle);
        // Additionally verify if unassign was called
        assertTrue("Unassign should be called before deletion", testRepository.wasUnassignCalled());
    }
    
    @Test
    public void testAssignDriverToVehicle_ValidIds() {
        // Act
        boolean result = vehicleService.assignDriverToVehicle(2, 101);
        
        // Assert
        assertTrue("Assigning a valid driver to a valid vehicle should return true", result);
        Vehicle vehicle = vehicleService.getVehicleById(2);
        assertNotNull("Vehicle should exist after assignment", vehicle);
        assertEquals("Vehicle should have the assigned driver ID", Integer.valueOf(101), vehicle.getDriverId());
        assertEquals("Vehicle status should be Assigned", "Assigned", vehicle.getStatus());
    }
    
    @Test
    public void testAssignDriverToVehicle_InvalidVehicleId() {
        // Act
        boolean result = vehicleService.assignDriverToVehicle(999, 101);
        
        // Assert
        assertFalse("Assigning to a non-existing vehicle should return false", result);
    }
    
    @Test
    public void testUnassignDriver_AssignedVehicle() {
        // Arrange
        Vehicle vehicle = vehicleService.getVehicleById(3);
        assertNotNull("Test setup failed: Vehicle not found", vehicle);
        assertNotNull("Test setup failed: Vehicle should have a driver", vehicle.getDriverId());
        
        // Act
        boolean result = vehicleService.unassignDriver(3);
        
        // Assert
        assertTrue("Unassigning a driver from an assigned vehicle should return true", result);
        Vehicle updatedVehicle = vehicleService.getVehicleById(3);
        assertNull("Vehicle should not have a driver after unassign", updatedVehicle.getDriverId());
        assertEquals("Vehicle status should be Unassigned", "Unassigned", updatedVehicle.getStatus());
    }
    
    @Test
    public void testUnassignDriver_UnassignedVehicle() {
        // Arrange
        Vehicle vehicle = vehicleService.getVehicleById(1);
        assertNotNull("Test setup failed: Vehicle not found", vehicle);
        assertNull("Test setup failed: Vehicle should not have a driver", vehicle.getDriverId());
        
        // Act
        boolean result = vehicleService.unassignDriver(1);
        
        // Assert
        // This is implementation-dependent. Some implementations might return true
        // even for already unassigned vehicles.
        assertTrue("Unassigning from an already unassigned vehicle should return true", result);
    }
    
    @Test
    public void testUnassignDriver_NonExistingVehicle() {
        // Act
        boolean result = vehicleService.unassignDriver(999);
        
        // Assert
        assertFalse("Unassigning from a non-existing vehicle should return false", result);
    }
    
    @Test
    public void testGetVehicleByPlate_ExistingPlate() {
        // Act
        Vehicle vehicle = vehicleService.getVehicleByPlate("JKL789");
        
        // Assert
        assertNotNull("Should return a vehicle for existing plate", vehicle);
        assertEquals("Should return the correct vehicle", "JKL789", vehicle.getPlateNumber());
    }
    
    @Test
    public void testGetVehicleByPlate_NonExistingPlate() {
        // Act
        Vehicle vehicle = vehicleService.getVehicleByPlate("NONEXISTING");
        
        // Assert
        assertNull("Should return null for non-existing plate", vehicle);
    }
    
    @Test
    public void testGetVehicleByPlate_NullPlate() {
        // Act
        Vehicle vehicle = vehicleService.getVehicleByPlate(null);
        
        // Assert
        assertNull("Should return null for null plate", vehicle);
    }
    
    // Mock implementation of VehicleRepository for testing
    private static class TestVehicleRepository implements VehicleRepository {
        private List<Vehicle> vehicles = new ArrayList<>();
        private boolean unassignCalled = false;
        
        public TestVehicleRepository() {
            // Initialize with test data
            Vehicle v1 = new Vehicle("ABC123", "Sedan", 4, "Toyota", "Camry");
            v1.setId(1);
            
            Vehicle v2 = new Vehicle("DEF456", "Sedan", 4, "Toyota", "Corolla");
            v2.setId(2);
            
            Vehicle v3 = new Vehicle("JKL789", "SUV", 7, "Honda", "Pilot");
            v3.setId(3);
            v3.setDriverId(100);
            
            vehicles.add(v1);
            vehicles.add(v2);
            vehicles.add(v3);
        }
        
        @Override
        public void createVehicle(Vehicle vehicle) {
            if (vehicle == null) {
                throw new NullPointerException("Vehicle cannot be null");
            }
            
            vehicle.setId(vehicles.size() + 1);
            vehicles.add(vehicle);
        }
        
        @Override
        public Vehicle getVehicleById(int id) {
            for (Vehicle vehicle : vehicles) {
                if (vehicle.getId() == id) {
                    return vehicle;
                }
            }
            return null;
        }
        
        @Override
        public List<Vehicle> getAllVehicles() {
            return new ArrayList<>(vehicles);
        }
        
        @Override
        public List<Vehicle> getVehiclesByType(String type) {
            List<Vehicle> result = new ArrayList<>();
            for (Vehicle vehicle : vehicles) {
                if (vehicle.getVehicleType().equals(type)) {
                    result.add(vehicle);
                }
            }
            return result;
        }
        
        @Override
        public List<Vehicle> getVehiclesByStatus(String status) {
            List<Vehicle> result = new ArrayList<>();
            for (Vehicle vehicle : vehicles) {
                if (vehicle.getStatus().equals(status)) {
                    result.add(vehicle);
                }
            }
            return result;
        }
        
        @Override
        public List<Vehicle> searchVehicles(String keyword) {
            List<Vehicle> result = new ArrayList<>();
            for (Vehicle vehicle : vehicles) {
                if (vehicle.getPlateNumber().contains(keyword) ||
                    vehicle.getBrand().contains(keyword) ||
                    vehicle.getModel().contains(keyword) ||
                    vehicle.getStatus().contains(keyword)) {
                    result.add(vehicle);
                }
            }
            return result;
        }
        
        @Override
        public boolean updateVehicle(Vehicle vehicle) {
            for (int i = 0; i < vehicles.size(); i++) {
                if (vehicles.get(i).getId() == vehicle.getId()) {
                    vehicles.set(i, vehicle);
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public boolean deleteVehicle(int id) {
            for (int i = 0; i < vehicles.size(); i++) {
                if (vehicles.get(i).getId() == id) {
                    vehicles.remove(i);
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public boolean assignDriverToVehicle(int vehicleId, int driverId) {
            Vehicle vehicle = getVehicleById(vehicleId);
            if (vehicle == null) {
                return false;
            }
            
            vehicle.setDriverId(driverId);
            vehicle.setStatus("Assigned");
            return true;
        }
        
        @Override
        public boolean unassignVehicle(int id) {
            unassignCalled = true;
            
            Vehicle vehicle = getVehicleById(id);
            if (vehicle == null) {
                return false;
            }
            
            vehicle.setDriverId(null);
            vehicle.setStatus("Unassigned");
            return true;
        }
        
        @Override
        public Vehicle getVehicleByPlate(String plateNumber) {
            if (plateNumber == null) {
                return null;
            }
            
            for (Vehicle vehicle : vehicles) {
                if (vehicle.getPlateNumber().equals(plateNumber)) {
                    return vehicle;
                }
            }
            return null;
        }
        
        public boolean wasUnassignCalled() {
            return unassignCalled;
        }
    }
}