package com.megacitycab.service;

import com.megacitycab.dao.AdminBankDAO;
import com.megacitycab.dao.AdminBankDAOImpl;
import com.megacitycab.model.AdminBank;

import java.sql.SQLException;
import java.util.List;

public class AdminBankServiceImpl implements AdminBankService {
    private final AdminBankDAO adminbankDAO;
    private static final int MAX_BANKS = 3;
    
    public AdminBankServiceImpl() {
        this.adminbankDAO = new AdminBankDAOImpl();
    }

    @Override
    public List<AdminBank> getAllBanksByAdminId(int adminId) throws SQLException {
        return adminbankDAO.getAllBanksByAdminId(adminId);
    }

    @Override
    public AdminBank getBankById(int bankId) throws SQLException {
        return adminbankDAO.getBankById(bankId);
    }

    @Override
    public boolean addBank(AdminBank bank) throws SQLException {
        if (!canAddBank(bank.getAdminId())) {
            return false;
        }
        
        return adminbankDAO.addBank(bank);
    }

    @Override
    public boolean updateBank(AdminBank bank) throws SQLException {
        return adminbankDAO.updateBank(bank);
    }

    @Override
    public boolean deleteBank(int bankId) throws SQLException {
        return adminbankDAO.deleteBank(bankId);
    }

    @Override
    public boolean canAddBank(int adminId) throws SQLException {
        int currentBankCount = adminbankDAO.getBankCountByAdminId(adminId);
        return currentBankCount < MAX_BANKS;
    }
}
