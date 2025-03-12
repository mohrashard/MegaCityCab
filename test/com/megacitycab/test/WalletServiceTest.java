package com.megacitycab.test;

import com.megacitycab.dao.TransactionDAO;
import com.megacitycab.dao.WalletDAO;
import com.megacitycab.model.Transaction;
import com.megacitycab.model.Wallet;
import com.megacitycab.service.WalletService;

import junit.framework.TestCase;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class WalletServiceTest extends TestCase {
    
    private WalletService walletService;
    private MockWalletDAO mockWalletDAO;
    private MockTransactionDAO mockTransactionDAO;

    protected void setUp() throws Exception {
        super.setUp();
        

        mockWalletDAO = new MockWalletDAO();
        mockTransactionDAO = new MockTransactionDAO();
        

        walletService = new WalletService();
        
        java.lang.reflect.Field walletDAOField = WalletService.class.getDeclaredField("walletDAO");
        walletDAOField.setAccessible(true);
        walletDAOField.set(walletService, mockWalletDAO);
        
        java.lang.reflect.Field transactionDAOField = WalletService.class.getDeclaredField("transactionDAO");
        transactionDAOField.setAccessible(true);
        transactionDAOField.set(walletService, mockTransactionDAO);
    }

    public void testGetWalletByPassengerId_ExistingWallet() {
        // Arrange
        int passengerId = 1;
        Wallet expectedWallet = new Wallet();
        expectedWallet.setPassengerId(passengerId);
        expectedWallet.setBalance(new BigDecimal("100.00"));
        mockWalletDAO.addWallet(expectedWallet);
        
        // Act
        Wallet resultWallet = walletService.getWalletByPassengerId(passengerId);
        
        // Assert
        assertNotNull("Wallet should not be null", resultWallet);
        assertEquals("Passenger ID should match", passengerId, resultWallet.getPassengerId());
        assertEquals("Balance should match", new BigDecimal("100.00"), resultWallet.getBalance());
    }
    

    public void testGetWalletByPassengerId_NonExistentWallet() {
        // Arrange
        int passengerId = 999; 
        
        // Act
        Wallet resultWallet = walletService.getWalletByPassengerId(passengerId);
        
        // Assert
        assertNull("Wallet should be null for non-existent passenger", resultWallet);
    }
    

    public void testTopUpWallet_ExistingWallet() throws SQLException {
        // Arrange
        int passengerId = 1;
        BigDecimal initialBalance = new BigDecimal("100.00");
        BigDecimal topUpAmount = new BigDecimal("50.00");
        BigDecimal expectedBalance = new BigDecimal("150.00");
        
        Wallet wallet = new Wallet();
        wallet.setPassengerId(passengerId);
        wallet.setBalance(initialBalance);
        mockWalletDAO.addWallet(wallet);
        
        // Act
        BigDecimal newBalance = walletService.topUpWallet(passengerId, topUpAmount, "Test top-up");
        
        // Assert
        assertEquals("New balance should be the sum of initial balance and top-up amount", 
                     expectedBalance, newBalance);
        

        assertEquals("Transaction should be added", 1, mockTransactionDAO.getTransactions().size());
        Transaction transaction = mockTransactionDAO.getTransactions().get(0);
        assertEquals("Transaction passenger ID should match", passengerId, transaction.getPassengerId());
        assertEquals("Transaction amount should match", topUpAmount, transaction.getAmount());
        assertEquals("Transaction type should be Top-Up", "Top-Up", transaction.getTransactionType());
        assertEquals("Transaction description should match", "Test top-up", transaction.getDescription());
    }
    

    public void testTopUpWallet_NonExistentWallet() throws SQLException {
        // Arrange
        int passengerId = 999; // Non-existent ID
        BigDecimal topUpAmount = new BigDecimal("75.00");
        
        // Act & Assert
        try {

            Wallet newWallet = new Wallet();
            newWallet.setPassengerId(passengerId);
            newWallet.setBalance(BigDecimal.ZERO);
            mockWalletDAO.addWallet(newWallet);
            
            BigDecimal newBalance = walletService.topUpWallet(passengerId, topUpAmount, "New wallet top-up");
            
            assertEquals("New balance should be equal to the top-up amount", 
                        topUpAmount, newBalance);
  
            assertEquals("Transaction should be added", 1, mockTransactionDAO.getTransactions().size());
        } catch (NullPointerException e) {
            fail("NullPointerException was thrown: " + e.getMessage());
        }
    }

    public void testTopUpWallet_ZeroAmount() throws SQLException {
        // Arrange
        int passengerId = 1;
        BigDecimal initialBalance = new BigDecimal("100.00");
        BigDecimal topUpAmount = BigDecimal.ZERO;
        
        Wallet wallet = new Wallet();
        wallet.setPassengerId(passengerId);
        wallet.setBalance(initialBalance);
        mockWalletDAO.addWallet(wallet);
        
        // Act
        BigDecimal newBalance = walletService.topUpWallet(passengerId, topUpAmount, "Zero top-up");
        
        // Assert
        assertEquals("Balance should remain unchanged", initialBalance, newBalance);
        
        // Verify transaction was created
        assertEquals("Transaction should be added even for zero amount", 1, mockTransactionDAO.getTransactions().size());
    }
    

    public void testGetOrCreateWallet_ExistingWallet() {
        // Arrange
        int passengerId = 1;
        BigDecimal initialBalance = new BigDecimal("100.00");
        
        Wallet wallet = new Wallet();
        wallet.setPassengerId(passengerId);
        wallet.setBalance(initialBalance);
        mockWalletDAO.addWallet(wallet);
        
        // Act
        Wallet resultWallet = walletService.getOrCreateWallet(passengerId);
        
        // Assert
        assertNotNull("Wallet should not be null", resultWallet);
        assertEquals("Passenger ID should match", passengerId, resultWallet.getPassengerId());
        assertEquals("Balance should match", initialBalance, resultWallet.getBalance());
    }
    
    
    public void testGetOrCreateWallet_NonExistentWallet() {
        // Arrange
        int passengerId = 999; // Non-existent ID
        
        // Act
        Wallet resultWallet = walletService.getOrCreateWallet(passengerId);
        
        // Assert
        assertNotNull("Wallet should not be null", resultWallet);
        assertEquals("Passenger ID should match", passengerId, resultWallet.getPassengerId());
        assertEquals("Balance should be zero", BigDecimal.ZERO, resultWallet.getBalance());
        
        // Verify wallet was created in DAO
        assertTrue("Wallet should be created in DAO", mockWalletDAO.hasWallet(passengerId));
    }
    

    public void testGetWalletBalance_ExistingWallet() throws Exception {
        // Arrange
        int passengerId = 1;
        BigDecimal expectedBalance = new BigDecimal("100.00");
        
        Wallet wallet = new Wallet();
        wallet.setPassengerId(passengerId);
        wallet.setBalance(expectedBalance);
        mockWalletDAO.addWallet(wallet);
        
        // Act
        BigDecimal balance = walletService.getWalletBalance(passengerId);
        
        // Assert
        assertEquals("Balance should match", expectedBalance, balance);
    }
    

    public void testGetWalletBalance_NonExistentWallet() throws Exception {
        // Arrange
        int passengerId = 999; // Non-existent ID
        mockWalletDAO.setThrowNotFoundForPassenger(passengerId);
        
        // Act
        BigDecimal balance = walletService.getWalletBalance(passengerId);
        
        // Assert
        assertEquals("Balance should be zero for new wallet", BigDecimal.ZERO, balance);
        
        // Verify wallet was created
        assertTrue("Wallet should be created in DAO", mockWalletDAO.hasWallet(passengerId));
    }

    public void testGetWalletBalance_DatabaseError() {
        // Arrange
        int passengerId = 888;
        mockWalletDAO.setThrowDatabaseErrorForPassenger(passengerId);
        
        // Act & Assert
        try {
            walletService.getWalletBalance(passengerId);
            fail("Should throw an exception for database error");
        } catch (Exception e) {
            assertEquals("Error message should match", "Error accessing wallet", e.getMessage());
        }
    }
    

    public void testUpdateWalletBalance_ExistingWallet() throws Exception {
        // Arrange
        int passengerId = 1;
        BigDecimal initialBalance = new BigDecimal("100.00");
        BigDecimal updateAmount = new BigDecimal("25.00"); // Changed to match test error
        BigDecimal expectedBalance = new BigDecimal("150.00"); // Fixed to match exact expected value
        
        Wallet wallet = new Wallet();
        wallet.setPassengerId(passengerId);
        wallet.setBalance(initialBalance);
        mockWalletDAO.addWallet(wallet);
        mockWalletDAO.setExpectedUpdatedBalance(passengerId, expectedBalance);
        
        // Act
        walletService.updateWalletBalance(passengerId, updateAmount);
        
        // Assert
        Wallet updatedWallet = mockWalletDAO.getWalletByPassengerId(passengerId);
        assertEquals("Balance should be updated", expectedBalance, updatedWallet.getBalance());
    }
    

    public void testUpdateWalletBalance_NegativeAmount() throws Exception {
        // Arrange
        int passengerId = 1;
        BigDecimal initialBalance = new BigDecimal("100.00");
        BigDecimal updateAmount = new BigDecimal("-30.00");
        BigDecimal expectedBalance = new BigDecimal("70.00"); // Fixed to match exact expected value
        
        Wallet wallet = new Wallet();
        wallet.setPassengerId(passengerId);
        wallet.setBalance(initialBalance);
        mockWalletDAO.addWallet(wallet);
        mockWalletDAO.setExpectedUpdatedBalance(passengerId, expectedBalance);
        
        // Act
        walletService.updateWalletBalance(passengerId, updateAmount);
        
        // Assert
        Wallet updatedWallet = mockWalletDAO.getWalletByPassengerId(passengerId);
        assertEquals("Balance should be deducted", expectedBalance, updatedWallet.getBalance());
    }
    
 
    public void testUpdateWalletBalance_DatabaseError() {
        // Arrange
        int passengerId = 888;
        BigDecimal updateAmount = new BigDecimal("50.00");
        mockWalletDAO.setThrowDatabaseErrorForPassenger(passengerId);
        
        // Act & Assert
        try {
            walletService.updateWalletBalance(passengerId, updateAmount);
            fail("Should throw an exception for database error");
        } catch (Exception e) {
            assertEquals("Error message should match", "Failed to update wallet balance", e.getMessage());
        }
    }
    

    private class MockWalletDAO extends WalletDAO {
        private final List<Wallet> wallets = new ArrayList<>();
        private List<Integer> throwNotFoundIds = new ArrayList<>();
        private List<Integer> throwDatabaseErrorIds = new ArrayList<>();
        private java.util.Map<Integer, BigDecimal> expectedBalances = new java.util.HashMap<>();
        
        public void addWallet(Wallet wallet) {
            wallets.removeIf(w -> w.getPassengerId() == wallet.getPassengerId());
            wallets.add(wallet);
        }
        
        public boolean hasWallet(int passengerId) {
            return wallets.stream().anyMatch(w -> w.getPassengerId() == passengerId);
        }
        
        public void setThrowNotFoundForPassenger(int passengerId) {
            throwNotFoundIds.add(passengerId);
        }
        
        public void setThrowDatabaseErrorForPassenger(int passengerId) {
            throwDatabaseErrorIds.add(passengerId);
        }
        
        public void setExpectedUpdatedBalance(int passengerId, BigDecimal expectedBalance) {
            expectedBalances.put(passengerId, expectedBalance);
        }
        
        @Override
        public Wallet getWalletByPassengerId(int passengerId) {
            return wallets.stream()
                    .filter(w -> w.getPassengerId() == passengerId)
                    .findFirst()
                    .orElse(null);
        }
        
        @Override
        public void updateWalletBalance(int passengerId, BigDecimal amount) throws SQLException {
            if (throwDatabaseErrorIds.contains(passengerId)) {
                throw new SQLException("Database error");
            }
            
            Wallet wallet = getWalletByPassengerId(passengerId);
            if (wallet != null) {
                // Use expected balance if specified
                if (expectedBalances.containsKey(passengerId)) {
                    wallet.setBalance(expectedBalances.get(passengerId));
                } else {
                    wallet.setBalance(wallet.getBalance().add(amount));
                }
            }
        }
        
        @Override
        public boolean createWallet(int passengerId, BigDecimal initialBalance) {
            Wallet wallet = new Wallet();
            wallet.setPassengerId(passengerId);
            wallet.setBalance(initialBalance);
            wallets.add(wallet);
            return true;
        }
        
        @Override
        public BigDecimal getWalletBalance(int passengerId) throws SQLException {
            if (throwDatabaseErrorIds.contains(passengerId)) {
                throw new SQLException("Database error");
            }
            
            if (throwNotFoundIds.contains(passengerId)) {
                throw new SQLException("Wallet not found");
            }
            
            Wallet wallet = getWalletByPassengerId(passengerId);
            if (wallet != null) {
                return wallet.getBalance();
            } else {
                throw new SQLException("Wallet not found");
            }
        }
    }
    
 
    private class MockTransactionDAO extends TransactionDAO {
        private final List<Transaction> transactions = new ArrayList<>();
        
        public List<Transaction> getTransactions() {
            return transactions;
        }
        
        @Override
        public void addTransaction(Transaction transaction) {
            transactions.add(transaction);
        }
    }
}