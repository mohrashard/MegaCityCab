
package com.megacitycab.service;

import com.megacitycab.dao.DriverTransactionDAO;
import com.megacitycab.dao.DriverTransactionDAOImpl;
import com.megacitycab.model.DriverTransaction;
import java.sql.Date;
import java.util.List;

public class DriverTransactionService {
    private final DriverTransactionDAO transactionDao = new DriverTransactionDAOImpl();

    public boolean createTransaction(DriverTransaction transaction) {
        return transactionDao.save(transaction);
    }

    public List<DriverTransaction> getTransactionsByDriver(int driverId) {
        return transactionDao.findByDriverId(driverId);
    }

    public List<DriverTransaction> getFilteredTransactions(int driverId, String type, 
                                                          Date startDate, Date endDate) {
        return transactionDao.findByDriverIdAndFilters(driverId, type, startDate, endDate);
    }
}