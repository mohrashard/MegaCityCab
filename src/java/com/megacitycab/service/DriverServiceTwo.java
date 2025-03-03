package com.megacitycab.service;

import com.megacitycab.model.DriverInfo;
import java.util.List;

public interface DriverServiceTwo {
    List<DriverInfo> getDriversByVehicleType(String vehicleType);
    DriverInfo getDriverById(int id);
    List<DriverInfo> getAllDrivers();
}