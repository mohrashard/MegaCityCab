
package com.megacitycab.service;

import com.megacitycab.dao.DriverWalletDAO;
import com.megacitycab.dao.DriverWalletDAOImpl;
import com.megacitycab.model.DriverWallet;
import java.math.BigDecimal;
import java.util.Optional;

public class DriverWalletService {
    private final DriverWalletDAO walletDao = new DriverWalletDAOImpl();

    public Optional<DriverWallet> getWalletByDriverId(int driverId) {
        return walletDao.findByDriverId(driverId);
    }

    public boolean updateWalletBalance(int driverId, BigDecimal amount, boolean isCredit) {
        return walletDao.updateBalance(driverId, amount, isCredit);
    }

    public boolean createWallet(DriverWallet wallet) {
        return walletDao.save(wallet);
    }

    public boolean processTopUp(int driverId, BigDecimal amount) {
        return updateWalletBalance(driverId, amount, true);
    }

    public boolean processWithdrawal(int driverId, BigDecimal amount) {
        return updateWalletBalance(driverId, amount, false);
    }
}