package com.megacitycab.service;

import com.megacitycab.model.AdminBank;
import java.sql.SQLException;
import java.util.List;

public interface AdminBankService {
    List<AdminBank> getAllBanksByAdminId(int adminId) throws SQLException;
    AdminBank getBankById(int bankId) throws SQLException;
    boolean addBank(AdminBank bank) throws SQLException;
    boolean updateBank(AdminBank bank) throws SQLException;
    boolean deleteBank(int bankId) throws SQLException;
    boolean canAddBank(int adminId) throws SQLException;
}