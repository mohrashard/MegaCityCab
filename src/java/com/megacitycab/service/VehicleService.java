package com.megacitycab.service;

import com.megacitycab.model.Vehicle;
import java.util.List;

public interface VehicleService {
    boolean addVehicle(Vehicle vehicle);
    Vehicle getVehicleById(int id);
    List<Vehicle> getAllVehicles();
    List<Vehicle> searchVehicles(String keyword);
    List<Vehicle> filterVehiclesByType(String type);
    List<Vehicle> filterVehiclesByStatus(String status);
    boolean updateVehicle(Vehicle vehicle);
    boolean deleteVehicle(int id);
    boolean assignDriverToVehicle(int vehicleId, int driverId);
    boolean unassignDriver(int vehicleId);
 Vehicle getVehicleByPlate(String plateNumber);
}
