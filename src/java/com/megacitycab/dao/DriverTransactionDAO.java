package com.megacitycab.dao;

import com.megacitycab.model.DriverTransaction;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface DriverTransactionDAO {
    boolean save(DriverTransaction transaction);
    Optional<DriverTransaction> findById(int transactionId);
    List<DriverTransaction> findByDriverId(int driverId);
    List<DriverTransaction> findByDriverIdAndFilters(int driverId, String type, Date startDate, Date endDate);
}