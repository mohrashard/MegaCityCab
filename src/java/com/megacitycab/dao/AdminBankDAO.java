package com.megacitycab.dao;

import com.megacitycab.model.AdminBank;
import java.sql.SQLException;
import java.util.List;

public interface AdminBankDAO {
    List<AdminBank> getAllBanksByAdminId(int adminId) throws SQLException;
    AdminBank getBankById(int bankId) throws SQLException;
    boolean addBank(AdminBank bank) throws SQLException;
    boolean updateBank(AdminBank bank) throws SQLException;
    boolean deleteBank(int bankId) throws SQLException;
    int getBankCountByAdminId(int adminId) throws SQLException;
}