package com.megacitycab.service;

import com.megacitycab.dao.AdminWalletDAO;
import com.megacitycab.dao.AdminWalletDAOImpl;

import com.megacitycab.model.AdminWallet;

import java.math.BigDecimal;
import java.sql.SQLException;

public class AdminWalletServiceImpl implements AdminWalletService {
    private final AdminWalletDAO adminwalletDAO;
    
    public AdminWalletServiceImpl() {
        this.adminwalletDAO = new AdminWalletDAOImpl();
    }

    @Override
    public AdminWallet getWalletByAdminId(int adminId) throws SQLException {
        return adminwalletDAO.getWalletByAdminId(adminId);
    }

    @Override
    public boolean createWalletIfNotExists(int adminId) throws SQLException {
        AdminWallet wallet = adminwalletDAO.getWalletByAdminId(adminId);
        
        if (wallet == null) {
            wallet = new AdminWallet(adminId, BigDecimal.ZERO);
            return adminwalletDAO.createWallet(wallet);
        }
        
        return true; 
    }

    @Override
    public boolean updateBalance(int adminId, BigDecimal amount, boolean isIncrease) throws SQLException {
        AdminWallet wallet = adminwalletDAO.getWalletByAdminId(adminId);
        
        if (wallet == null) {
            return false;
        }
        
        BigDecimal newBalance;
        if (isIncrease) {
            newBalance = wallet.getBalance().add(amount);
        } else {
            if (wallet.getBalance().compareTo(amount) < 0) {
                return false; 
            }
            newBalance = wallet.getBalance().subtract(amount);
        }
        
        return adminwalletDAO.updateWalletBalance(adminId, newBalance);
    }
}