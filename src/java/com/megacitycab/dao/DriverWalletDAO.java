package com.megacitycab.dao;

import com.megacitycab.model.DriverWallet;
import com.megacitycab.config.DBConnection;

import java.sql.*;
import java.math.BigDecimal;
import java.util.Optional;


public interface DriverWalletDAO {
    Optional<DriverWallet> findByDriverId(int driverId);
    boolean save(DriverWallet wallet);
    boolean update(DriverWallet wallet);
    boolean updateBalance(int driverId, BigDecimal amount, boolean isCredit);
}