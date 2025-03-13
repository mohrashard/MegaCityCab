package com.megacitycab.dao;

import com.megacitycab.model.DriverBank;
import java.util.List;
import java.util.Optional;

public interface DriverBankDAO {
    boolean save(DriverBank bank);
    boolean update(DriverBank bank);
    boolean delete(int bankId);
    Optional<DriverBank> findById(int bankId);
    List<DriverBank> findByDriverId(int driverId);
    int countByDriverId(int driverId);
    boolean setDefaultBank(int driverId, int bankId);
}