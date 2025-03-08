package com.megacitycab.service;

import com.megacitycab.model.AdminWallet;
import java.math.BigDecimal;
import java.sql.SQLException;

public interface AdminWalletService {
    AdminWallet getWalletByAdminId(int adminId) throws SQLException;
    boolean createWalletIfNotExists(int adminId) throws SQLException;
    boolean updateBalance(int adminId, BigDecimal amount, boolean isIncrease) throws SQLException;
}