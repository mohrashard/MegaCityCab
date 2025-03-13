package com.megacitycab.repository;

import com.megacitycab.model.DriverInfo;
import java.util.List;

public interface DriverRepository {
    List<DriverInfo> getDriversByVehicleType(String vehicleType);
    DriverInfo getDriverById(int id);
    List<DriverInfo> getAllDrivers();
}