package com.megacitycab.dao;

import com.megacitycab.model.AdminWallet;
import java.math.BigDecimal;
import java.sql.SQLException;

public interface AdminWalletDAO {
    AdminWallet getWalletByAdminId(int adminId) throws SQLException;
    boolean createWallet(AdminWallet wallet) throws SQLException;
    boolean updateWalletBalance(int adminId, BigDecimal newBalance) throws SQLException;
}