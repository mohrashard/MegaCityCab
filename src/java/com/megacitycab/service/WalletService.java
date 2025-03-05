package com.megacitycab.service;

import com.megacitycab.dao.TransactionDAO;
import com.megacitycab.dao.WalletDAO;
import com.megacitycab.model.Transaction;
import com.megacitycab.model.Wallet;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class WalletService {
    private final WalletDAO walletDAO = new WalletDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public Wallet getWalletByPassengerId(int passengerId) {
        return walletDAO.getWalletByPassengerId(passengerId);
    }

    public BigDecimal topUpWallet(int passengerId, BigDecimal amount, String description) throws SQLException {
        Wallet wallet = walletDAO.getWalletByPassengerId(passengerId);
        if (wallet == null) {
            walletDAO.createWallet(passengerId, BigDecimal.ZERO); 
            walletDAO.updateWalletBalance(passengerId, amount);
        } else {
            walletDAO.updateWalletBalance(passengerId, amount);
        }

  
        Transaction transaction = new Transaction();
        transaction.setPassengerId(passengerId);
        transaction.setAmount(amount);
        transaction.setTransactionType("Top-Up"); 
        transaction.setDescription(description);
        transaction.setDateTime(LocalDateTime.now());


        transactionDAO.addTransaction(transaction);

        return wallet.getBalance().add(amount);
    }
        
    public Wallet getOrCreateWallet(int passengerId) {
        Wallet wallet = walletDAO.getWalletByPassengerId(passengerId);
        if (wallet == null) {
            walletDAO.createWallet(passengerId, BigDecimal.ZERO);
            wallet = new Wallet();
            wallet.setPassengerId(passengerId);
            wallet.setBalance(BigDecimal.ZERO);
        }
        return wallet;
    }

    public BigDecimal getWalletBalance(int passengerId) throws Exception {
        try {
            return walletDAO.getWalletBalance(passengerId);
        } catch (SQLException e) {
            if (e.getMessage().contains("Wallet not found")) {
                walletDAO.createWallet(passengerId, BigDecimal.ZERO);
                return BigDecimal.ZERO;
            }
            throw new Exception("Error accessing wallet", e);
        }
    }

    public void updateWalletBalance(int passengerId, BigDecimal amount) throws Exception {
        try {
            BigDecimal currentBalance = getWalletBalance(passengerId);
            BigDecimal newBalance = currentBalance.add(amount);
            

            walletDAO.updateWalletBalance(passengerId, newBalance);
        } catch (SQLException e) {
            throw new Exception("Failed to update wallet balance", e);
        }
    }
}