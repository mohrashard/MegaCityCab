package com.megacitycab.service;

import com.megacitycab.model.DriverInfo;
import com.megacitycab.repository.DriverRepository;
import com.megacitycab.repository.DriverRepositoryImpl;

import com.megacitycab.service.DriverService;

import java.util.List;

public class DriverServiceImpl implements DriverServiceTwo {
    
    private final DriverRepository driverRepository;
    

    public DriverServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }
    

    public DriverServiceImpl() {
        this.driverRepository = new DriverRepositoryImpl();
    }

    @Override
    public List<DriverInfo> getDriversByVehicleType(String vehicleType) {
        return driverRepository.getDriversByVehicleType(vehicleType);
    }

    @Override
    public DriverInfo getDriverById(int id) {
        return driverRepository.getDriverById(id);
    }

    @Override
    public List<DriverInfo> getAllDrivers() {
        return driverRepository.getAllDrivers();
    }
}
