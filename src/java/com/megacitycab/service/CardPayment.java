
package com.megacitycab.service;

import com.megacitycab.dao.CardDAO;
import com.megacitycab.dao.TransactionDAO;

import com.megacitycab.model.Transaction;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class CardPayment implements PaymentProcessor {
    private final CardDAO cardDAO;
    private final TransactionDAO transactionDAO;

    public CardPayment(CardDAO cardDAO, TransactionDAO transactionDAO) {
        this.cardDAO = cardDAO;
        this.transactionDAO = transactionDAO;
    }

    @Override
    public void processPayment(int passengerId, int bookingId, BigDecimal amount, String description) 
        throws SQLException, PaymentException {
        

        if (cardDAO.getCardsByPassengerId(passengerId).isEmpty()) {
            throw new PaymentException("No saved cards found");
        }


        Transaction transaction = new Transaction();
        transaction.setPassengerId(passengerId);
        transaction.setAmount(amount);
        transaction.setTransactionType("Expense");
        transaction.setDescription(description != null ? description : "Paid from Card");
        transaction.setDateTime(LocalDateTime.now());
        
        transactionDAO.addTransaction(transaction);
    }
}