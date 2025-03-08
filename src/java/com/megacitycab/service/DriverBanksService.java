
package com.megacitycab.service;

import com.megacitycab.dao.DriverBankDAO;
import com.megacitycab.dao.DriverBankDAOImpl;
import com.megacitycab.model.DriverBank;
import java.util.List;
import java.util.Optional;

public class DriverBanksService {
    private final DriverBankDAO bankDao = new DriverBankDAOImpl();

    public boolean addBank(DriverBank bank) {
        return bankDao.save(bank);
    }

    public boolean updateBank(DriverBank bank) {
        return bankDao.update(bank);
    }

    public boolean deleteBank(int bankId) {
        return bankDao.delete(bankId);
    }

    public Optional<DriverBank> getBankById(int bankId) {
        return bankDao.findById(bankId);
    }

    public List<DriverBank> getDriverBanks(int driverId) {
        return bankDao.findByDriverId(driverId);
    }

    public boolean setDefaultBank(int driverId, int bankId) {
        return bankDao.setDefaultBank(driverId, bankId);
    }
}