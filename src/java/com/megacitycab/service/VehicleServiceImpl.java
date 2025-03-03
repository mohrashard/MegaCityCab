package com.megacitycab.service;

import com.megacitycab.model.Vehicle;
import com.megacitycab.repository.VehicleRepository;
import com.megacitycab.repository.VehicleRepositoryImpl;

import java.util.List;

public class VehicleServiceImpl implements VehicleService {
    
    private final VehicleRepository vehicleRepository;
    
    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public VehicleServiceImpl() {
        this.vehicleRepository = new VehicleRepositoryImpl();
    }

    @Override
    public boolean addVehicle(Vehicle vehicle) {
        try {
            vehicleRepository.createVehicle(vehicle);
            return true;
        } catch (Exception e) {
            System.out.println("Error adding vehicle: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Vehicle getVehicleById(int id) {
        return vehicleRepository.getVehicleById(id);
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.getAllVehicles();
    }

    @Override
    public List<Vehicle> searchVehicles(String keyword) {
        return vehicleRepository.searchVehicles(keyword);
    }

    @Override
    public List<Vehicle> filterVehiclesByType(String type) {
        return vehicleRepository.getVehiclesByType(type);
    }

    @Override
    public List<Vehicle> filterVehiclesByStatus(String status) {
        return vehicleRepository.getVehiclesByStatus(status);
    }

    @Override
    public boolean updateVehicle(Vehicle vehicle) {
        return vehicleRepository.updateVehicle(vehicle);
    }

    @Override
    public boolean deleteVehicle(int id) {
        Vehicle vehicle = vehicleRepository.getVehicleById(id);
        if (vehicle != null && vehicle.getDriverId() != null) {
            vehicleRepository.unassignVehicle(id);
        }
        return vehicleRepository.deleteVehicle(id);
    }

    @Override
    public boolean assignDriverToVehicle(int vehicleId, int driverId) {
        return vehicleRepository.assignDriverToVehicle(vehicleId, driverId);
    }

    @Override
    public boolean unassignDriver(int vehicleId) {
        return vehicleRepository.unassignVehicle(vehicleId);
    }
@Override
    public Vehicle getVehicleByPlate(String plateNumber) {
        return vehicleRepository.getVehicleByPlate(plateNumber);
    }
}
