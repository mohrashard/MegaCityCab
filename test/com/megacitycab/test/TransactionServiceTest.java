
package com.megacitycab.test;

import com.megacitycab.model.Transaction;
import com.megacitycab.service.TransactionService;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class TransactionServiceTest {
    private TransactionService transactionService;

    @Before
    public void setUp() {
        transactionService = new TransactionService();
    }

    @Test
    public void testAddTransaction_ValidTransaction() {
        Transaction transaction = new Transaction();
        transaction.setPassengerId(1);
        transaction.setAmount(new BigDecimal("50.00"));
        transaction.setTransactionType("Credit");
        transaction.setDescription("Ride Payment");
        transaction.setDateTime(LocalDateTime.now());

        try {
            transactionService.addTransaction(transaction);
        } catch (SQLException e) {
            fail("SQLException should not have been thrown: " + e.getMessage());
        }
    }

    @Test
    public void testAddTransaction_InvalidTransaction() {
        Transaction transaction = new Transaction();
        try {
            transactionService.addTransaction(transaction);
            fail("Expected SQLException due to invalid transaction");
        } catch (SQLException e) {
            assertNotNull("SQLException expected", e.getMessage());
        }
    }

    @Test
    public void testGetTransactionsByPassengerId_ValidId() {
        List<Transaction> transactions = transactionService.getTransactionsByPassengerId(1);
        assertNotNull("Transaction list should not be null", transactions);
    }

    @Test
    public void testGetTransactionsByPassengerId_InvalidId() {
        List<Transaction> transactions = transactionService.getTransactionsByPassengerId(-1);
        assertNotNull("Transaction list should not be null", transactions);
        assertTrue("Transaction list should be empty", transactions.isEmpty());
    }
}

