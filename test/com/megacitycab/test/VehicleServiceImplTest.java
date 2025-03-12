
package com.megacitycab.test;
import static org.junit.Assert.*;
import org.junit.Test;

import com.megacitycab.model.Vehicle;
import com.megacitycab.repository.VehicleRepository;
import com.megacitycab.service.VehicleServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class VehicleServiceImplTest {


    @Test
    public void testDefaultConstructor() {
        VehicleServiceImpl service = new VehicleServiceImpl();
        assertNotNull("Service should be created with default constructor", service);
    }
    

    @Test
    public void testAddVehicle_RepositoryException() {
        // Arrange
        VehicleRepository exceptionThrowingRepo = new ExceptionThrowingRepository();
        VehicleServiceImpl service = new VehicleServiceImpl(exceptionThrowingRepo);
        Vehicle vehicle = new Vehicle("TEST123", "Sedan", 4, "Test", "Model");
        
        // Act
        boolean result = service.addVehicle(vehicle);
        
        // Assert
        assertFalse("Should return false when repository throws exception", result);
    }
    

    @Test
    public void testServiceDelegation() {
        // Arrange
        CountingRepository countingRepo = new CountingRepository();
        VehicleServiceImpl service = new VehicleServiceImpl(countingRepo);
        
        // Act - Call various methods
        service.getAllVehicles();
        service.getVehicleById(1);
        service.searchVehicles("test");
        service.filterVehiclesByType("Sedan");
        service.filterVehiclesByStatus("Available");
        service.getVehicleByPlate("TEST123");
        
        // Assert
        assertEquals("getAllVehicles should be called", 1, countingRepo.getAllVehiclesCount);
        assertEquals("getVehicleById should be called", 1, countingRepo.getVehicleByIdCount);
        assertEquals("searchVehicles should be called", 1, countingRepo.searchVehiclesCount);
        assertEquals("getVehiclesByType should be called", 1, countingRepo.getVehiclesByTypeCount);
        assertEquals("getVehiclesByStatus should be called", 1, countingRepo.getVehiclesByStatusCount);
        assertEquals("getVehicleByPlate should be called", 1, countingRepo.getVehicleByPlateCount);
    }
    

    private static class ExceptionThrowingRepository implements VehicleRepository {
        @Override
        public void createVehicle(Vehicle vehicle) {
            throw new RuntimeException("Test exception");
        }
        
        @Override
        public Vehicle getVehicleById(int id) {
            throw new RuntimeException("Test exception");
        }
        
        @Override
        public List<Vehicle> getAllVehicles() {
            throw new RuntimeException("Test exception");
        }
        
        @Override
        public List<Vehicle> getVehiclesByType(String type) {
            throw new RuntimeException("Test exception");
        }
        
        @Override
        public List<Vehicle> getVehiclesByStatus(String status) {
            throw new RuntimeException("Test exception");
        }
        
        @Override
        public List<Vehicle> searchVehicles(String keyword) {
            throw new RuntimeException("Test exception");
        }
        
        @Override
        public boolean updateVehicle(Vehicle vehicle) {
            throw new RuntimeException("Test exception");
        }
        
        @Override
        public boolean deleteVehicle(int id) {
            throw new RuntimeException("Test exception");
        }
        
        @Override
        public boolean assignDriverToVehicle(int vehicleId, int driverId) {
            throw new RuntimeException("Test exception");
        }
        
        @Override
        public boolean unassignVehicle(int id) {
            throw new RuntimeException("Test exception");
        }
        
        @Override
        public Vehicle getVehicleByPlate(String plateNumber) {
            throw new RuntimeException("Test exception");
        }
    }
    

    private static class CountingRepository implements VehicleRepository {
        public int createVehicleCount = 0;
        public int getVehicleByIdCount = 0;
        public int getAllVehiclesCount = 0;
        public int getVehiclesByTypeCount = 0;
        public int getVehiclesByStatusCount = 0;
        public int searchVehiclesCount = 0;
        public int updateVehicleCount = 0;
        public int deleteVehicleCount = 0;
        public int assignDriverToVehicleCount = 0;
        public int unassignVehicleCount = 0;
        public int getVehicleByPlateCount = 0;
        
        @Override
        public void createVehicle(Vehicle vehicle) {
            createVehicleCount++;
        }
        
        @Override
        public Vehicle getVehicleById(int id) {
            getVehicleByIdCount++;
            return null;
        }
        
        @Override
        public List<Vehicle> getAllVehicles() {
            getAllVehiclesCount++;
            return new ArrayList<>();
        }
        
        @Override
        public List<Vehicle> getVehiclesByType(String type) {
            getVehiclesByTypeCount++;
            return new ArrayList<>();
        }
        
        @Override
        public List<Vehicle> getVehiclesByStatus(String status) {
            getVehiclesByStatusCount++;
            return new ArrayList<>();
        }
        
        @Override
        public List<Vehicle> searchVehicles(String keyword) {
            searchVehiclesCount++;
            return new ArrayList<>();
        }
        
        @Override
        public boolean updateVehicle(Vehicle vehicle) {
            updateVehicleCount++;
            return true;
        }
        
        @Override
        public boolean deleteVehicle(int id) {
            deleteVehicleCount++;
            return true;
        }
        
        @Override
        public boolean assignDriverToVehicle(int vehicleId, int driverId) {
            assignDriverToVehicleCount++;
            return true;
        }
        
        @Override
        public boolean unassignVehicle(int id) {
            unassignVehicleCount++;
            return true;
        }
        
        @Override
        public Vehicle getVehicleByPlate(String plateNumber) {
            getVehicleByPlateCount++;
            return null;
        }
    }
}