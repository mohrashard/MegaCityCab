package com.megacitycab.repository;

import com.megacitycab.model.Vehicle;
import java.util.List;

public interface VehicleRepository {
    void createVehicle(Vehicle vehicle);
    Vehicle getVehicleById(int id);
    List<Vehicle> getAllVehicles();
    List<Vehicle> getVehiclesByType(String type);
    List<Vehicle> getVehiclesByStatus(String status);
    List<Vehicle> searchVehicles(String keyword);
    boolean updateVehicle(Vehicle vehicle);
    boolean deleteVehicle(int id);
    boolean assignDriverToVehicle(int vehicleId, int driverId);
    boolean unassignVehicle(int id);
    Vehicle getVehicleByPlate(String plateNumber); 
}
