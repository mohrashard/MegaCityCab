package com.megacitycab.test;

import com.megacitycab.model.AdminTransaction;
import com.megacitycab.dao.AdminTransactionDAO;
import com.megacitycab.model.AdminWallet;
import com.megacitycab.service.AdminTransactionService;
import com.megacitycab.service.AdminTransactionServiceImpl;
import com.megacitycab.service.AdminWalletService;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class AdminTransactionServiceTest {
    

    private AdminTransactionService adminTransactionService;
    private TestableAdminTransactionServiceImpl testableService;
    private TestAdminTransactionDAO testAdminTransactionDAO;
    private TestAdminWalletService testAdminWalletService;
    
    @Before
    public void setUp() {
        testAdminTransactionDAO = new TestAdminTransactionDAO();
        testAdminWalletService = new TestAdminWalletService();
        

        testableService = new TestableAdminTransactionServiceImpl(
            testAdminTransactionDAO, testAdminWalletService);

        adminTransactionService = (AdminTransactionService) testableService;
    }
    
    @Test
    public void testGetAllTransactionsByAdminId_ValidId_ReturnsTransactionList() throws SQLException {
        // Arrange
        int adminId = 1;
        List<AdminTransaction> expectedTransactions = createSampleTransactions(adminId, 5);
        testAdminTransactionDAO.setTransactionsToReturn(expectedTransactions);
        
        // Act
        List<AdminTransaction> result = adminTransactionService.getAllTransactionsByAdminId(adminId);
        
        // Assert
        assertEquals("Should return the expected number of transactions", 5, result.size());
        assertEquals("First transaction should have the expected adminId", adminId, result.get(0).getAdminId());
    }
    
    @Test
    public void testGetRecentTransactionsByAdminId_ValidIdAndLimit_ReturnsLimitedTransactions() throws SQLException {
        // Arrange
        int adminId = 1;
        int limit = 3;
        List<AdminTransaction> allTransactions = createSampleTransactions(adminId, 5);
        testAdminTransactionDAO.setTransactionsToReturn(allTransactions);
        
        // Act
        List<AdminTransaction> result = adminTransactionService.getRecentTransactionsByAdminId(adminId, limit);
        
        // Assert
        assertEquals("Should return only the requested number of transactions", limit, result.size());
    }
    
    @Test
    public void testAddTransaction_ValidTransaction_ReturnsTrue() throws SQLException {
        // Arrange
        AdminTransaction validTransaction = new AdminTransaction(
            1, new BigDecimal("100.00"), "DEPOSIT", "Test deposit");
        testAdminTransactionDAO.setAddTransactionResult(true);
        
        // Act
        boolean result = adminTransactionService.addTransaction(validTransaction);
        
        // Assert
        assertTrue("Adding valid transaction should return true", result);
        assertEquals("DAO should have been called with the transaction", 
            validTransaction, testAdminTransactionDAO.getLastAddedTransaction());
    }
    
    @Test
    public void testAddTransaction_InvalidTransaction_ReturnsFalse() throws SQLException {
        // Arrange
        AdminTransaction invalidTransaction = new AdminTransaction(
            1, new BigDecimal("-100.00"), "DEPOSIT", "Negative amount");
        testAdminTransactionDAO.setAddTransactionResult(false);
        
        // Act
        boolean result = adminTransactionService.addTransaction(invalidTransaction);
        
        // Assert
        assertFalse("Adding invalid transaction should return false", result);
    }
    
    @Test
    public void testProcessWithdrawal_ValidAmountAndBalance_ReturnsTrue() throws SQLException {
        // Arrange
        int adminId = 1;
        BigDecimal withdrawalAmount = new BigDecimal("500.00");
        String description = "Valid withdrawal";
        

        testableService.setCanWithdrawResult(true);
        testAdminWalletService.setUpdateBalanceResult(true);
        testAdminTransactionDAO.setAddTransactionResult(true);
        
        // Act
        boolean result = adminTransactionService.processWithdrawal(adminId, withdrawalAmount, description);
        
        // Assert
        assertTrue("Valid withdrawal should return true", result);
        assertEquals("Wallet service should be called with correct amount", 
            withdrawalAmount, testAdminWalletService.getLastUpdatedAmount());
        assertFalse("Wallet update should be a deduction", testAdminWalletService.getLastUpdatedIsAddition());

        AdminTransaction addedTransaction = testAdminTransactionDAO.getLastAddedTransaction();
        assertNotNull("Transaction should have been added", addedTransaction);
        assertEquals("Transaction should have correct admin ID", adminId, addedTransaction.getAdminId());
        assertEquals("Transaction should have correct amount", withdrawalAmount, addedTransaction.getAmount());
        assertEquals("Transaction should have correct type", "WITHDRAWAL", addedTransaction.getTransactionType());
        assertEquals("Transaction should have correct description", description, addedTransaction.getDescription());
    }
    
    @Test
    public void testProcessWithdrawal_ExceedsDailyLimit_ReturnsFalse() throws SQLException {
        // Arrange
        int adminId = 1;
        BigDecimal withdrawalAmount = new BigDecimal("500.00");
        String description = "Exceeded daily limit";
        

        testableService.setCanWithdrawResult(false);
        
        // Act
        boolean result = adminTransactionService.processWithdrawal(adminId, withdrawalAmount, description);
        
        // Assert
        assertFalse("Withdrawal exceeding daily limit should return false", result);
        // Verify that wallet service was not called
        assertNull("Wallet service should not be called", testAdminWalletService.getLastUpdatedAmount());
    }
    
    @Test
    public void testProcessWithdrawal_WalletUpdateFails_ReturnsFalse() throws SQLException {
        // Arrange
        int adminId = 1;
        BigDecimal withdrawalAmount = new BigDecimal("500.00");
        String description = "Wallet update failure";
        

        testableService.setCanWithdrawResult(true);
        testAdminWalletService.setUpdateBalanceResult(false);
        
        // Act
        boolean result = adminTransactionService.processWithdrawal(adminId, withdrawalAmount, description);
        
        // Assert
        assertFalse("Withdrawal with wallet update failure should return false", result);
        // Verify transaction was not added
        assertNull("Transaction should not be added when wallet update fails", 
            testAdminTransactionDAO.getLastAddedTransaction());
    }
    
    @Test
    public void testCanWithdraw_WithinDailyLimit_ReturnsTrue() throws SQLException {
        // Arrange
        int adminId = 1;
        BigDecimal withdrawalAmount = new BigDecimal("1000.00");
        BigDecimal totalWithdrawalsToday = new BigDecimal("5000.00");
        
        testAdminTransactionDAO.setTotalWithdrawalForDay(totalWithdrawalsToday);

        testableService.setUseRealCanWithdraw(true);
        
        // Act
        boolean result = adminTransactionService.canWithdraw(adminId, withdrawalAmount);
        
        // Assert
        assertTrue("Should be able to withdraw within daily limit", result);
    }
    
    @Test
    public void testCanWithdraw_ExceedsDailyLimit_ReturnsFalse() throws SQLException {
        // Arrange
        int adminId = 1;
        BigDecimal withdrawalAmount = new BigDecimal("50000.00");
        BigDecimal totalWithdrawalsToday = new BigDecimal("60000.00");

        testAdminTransactionDAO.setTotalWithdrawalForDay(totalWithdrawalsToday);

        testableService.setUseRealCanWithdraw(true);
        
        // Act
        boolean result = adminTransactionService.canWithdraw(adminId, withdrawalAmount);
        
        // Assert
        assertFalse("Should not be able to withdraw beyond daily limit", result);
    }
    

    
    @Test(expected = SQLException.class)
    public void testGetAllTransactionsByAdminId_DAOThrowsException_PropagatesException() throws SQLException {
        // Arrange
        int adminId = 1;
        testAdminTransactionDAO.setShouldThrowException(true);
        

        adminTransactionService.getAllTransactionsByAdminId(adminId);
    }
    

    
    private List<AdminTransaction> createSampleTransactions(int adminId, int count) {
        List<AdminTransaction> transactions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            AdminTransaction transaction = new AdminTransaction(
                adminId, 
                new BigDecimal((i + 1) * 100), 
                (i % 2 == 0) ? "DEPOSIT" : "WITHDRAWAL",
                "Test transaction " + (i + 1)
            );
            transaction.setTransactionId(i + 1);
            transaction.setCreatedAt(LocalDateTime.now().minusDays(i));
            transactions.add(transaction);
        }
        return transactions;
    }

    private class TestableAdminTransactionServiceImpl extends AdminTransactionServiceImpl {
        private boolean canWithdrawResult = true;
        private boolean useRealCanWithdraw = false;
        
        public TestableAdminTransactionServiceImpl(
                AdminTransactionDAO adminTransactionDAO, 
                AdminWalletService adminWalletService) {
            super();
            try {
                java.lang.reflect.Field daoField = AdminTransactionServiceImpl.class.getDeclaredField("admintransactionDAO");
                daoField.setAccessible(true);
                daoField.set(this, adminTransactionDAO);
                
                java.lang.reflect.Field walletServiceField = AdminTransactionServiceImpl.class.getDeclaredField("adminwalletService");
                walletServiceField.setAccessible(true);
                walletServiceField.set(this, adminWalletService);
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize test double", e);
            }
        }
        
        @Override
        public boolean canWithdraw(int adminId, BigDecimal amount) throws SQLException {
            if (useRealCanWithdraw) {
                return super.canWithdraw(adminId, amount);
            }
            return canWithdrawResult;
        }
        
        public void setCanWithdrawResult(boolean result) {
            this.canWithdrawResult = result;
        }
        
        public void setUseRealCanWithdraw(boolean useReal) {
            this.useRealCanWithdraw = useReal;
        }
    }
    

    private class TestAdminTransactionDAO implements AdminTransactionDAO {
        private List<AdminTransaction> transactionsToReturn = new ArrayList<>();
        private boolean addTransactionResult = true;
        private AdminTransaction lastAddedTransaction = null;
        private BigDecimal totalWithdrawalForDay = BigDecimal.ZERO;
        private boolean shouldThrowException = false;
        
        @Override
        public List<AdminTransaction> getAllTransactionsByAdminId(int adminId) throws SQLException {
            if (shouldThrowException) {
                throw new SQLException("Test exception");
            }
            return transactionsToReturn;
        }
        
        @Override
        public List<AdminTransaction> getRecentTransactionsByAdminId(int adminId, int limit) throws SQLException {
            if (shouldThrowException) {
                throw new SQLException("Test exception");
            }
            
            List<AdminTransaction> limitedTransactions = new ArrayList<>();
            int actualLimit = Math.min(limit, transactionsToReturn.size());
            for (int i = 0; i < actualLimit; i++) {
                limitedTransactions.add(transactionsToReturn.get(i));
            }
            return limitedTransactions;
        }
        
        @Override
        public boolean addTransaction(AdminTransaction transaction) throws SQLException {
            if (shouldThrowException) {
                throw new SQLException("Test exception");
            }
            this.lastAddedTransaction = transaction;
            return addTransactionResult;
        }
        
        @Override
        public BigDecimal getTotalWithdrawalForDay(int adminId, LocalDate date) throws SQLException {
            if (shouldThrowException) {
                throw new SQLException("Test exception");
            }
            return totalWithdrawalForDay;
        }

        public void setTransactionsToReturn(List<AdminTransaction> transactions) {
            this.transactionsToReturn = transactions;
        }
        
        public void setAddTransactionResult(boolean result) {
            this.addTransactionResult = result;
        }
        
        public AdminTransaction getLastAddedTransaction() {
            return lastAddedTransaction;
        }
        
        public void setTotalWithdrawalForDay(BigDecimal total) {
            this.totalWithdrawalForDay = total;
        }
        
        public void setShouldThrowException(boolean shouldThrow) {
            this.shouldThrowException = shouldThrow;
        }
    }
    

    private class TestAdminWalletService implements AdminWalletService {
        private boolean updateBalanceResult = true;
        private BigDecimal lastUpdatedAmount = null;
        private Boolean lastUpdatedIsAddition = null;
        
        @Override
        public boolean updateBalance(int adminId, BigDecimal amount, boolean isAddition) throws SQLException {
            this.lastUpdatedAmount = amount;
            this.lastUpdatedIsAddition = isAddition;
            return updateBalanceResult;
        }
        

        public void setUpdateBalanceResult(boolean result) {
            this.updateBalanceResult = result;
        }
        
        public BigDecimal getLastUpdatedAmount() {
            return lastUpdatedAmount;
        }
        
        public Boolean getLastUpdatedIsAddition() {
            return lastUpdatedIsAddition;
        }

        @Override
        public AdminWallet getWalletByAdminId(int adminId) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean createWalletIfNotExists(int adminId) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet."); 
        }
    }
}