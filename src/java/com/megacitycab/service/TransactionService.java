package com.megacitycab.service;

import com.megacitycab.dao.TransactionDAO;
import com.megacitycab.model.Transaction;
import java.sql.SQLException;

import java.util.List;

public class TransactionService {
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public void addTransaction(Transaction transaction) throws SQLException {
        transactionDAO.addTransaction(transaction);
    }

    public List<Transaction> getTransactionsByPassengerId(int passengerId) {
        return transactionDAO.getTransactionsByPassengerId(passengerId);
    }
}