package com.megacitycab.test;

import com.megacitycab.dao.DriverTransactionDAOImpl;
import com.megacitycab.model.DriverTransaction;
import com.megacitycab.service.DriverTransactionService;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class DriverTransactionServiceTest {
    
    private DriverTransactionService service;
    private TestDriverTransactionDAO testDao;
    

    private static class TestDriverTransactionDAO extends DriverTransactionDAOImpl {
        private final List<DriverTransaction> transactions = new ArrayList<>();
        private boolean saveSuccessful = true;
        
        @Override
        public boolean save(DriverTransaction transaction) {
            if (saveSuccessful) {
                transactions.add(transaction);
                return true;
            }
            return false;
        }
        
        @Override
        public Optional<DriverTransaction> findById(int transactionId) {
            return transactions.stream()
                    .filter(t -> t.getTransactionId() == transactionId)
                    .findFirst();
        }
        
        @Override
        public List<DriverTransaction> findByDriverId(int driverId) {
            return transactions.stream()
                    .filter(t -> t.getDriverId() == driverId)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
        
        @Override
        public List<DriverTransaction> findByDriverIdAndFilters(int driverId, String type, 
                                                              Date startDate, Date endDate) {
            List<DriverTransaction> filtered = new ArrayList<>();
            
            for (DriverTransaction t : transactions) {
                if (t.getDriverId() != driverId) {
                    continue;
                }
                
                if (type != null && !type.isEmpty() && !t.getTransactionType().equals(type)) {
                    continue;
                }
                
                if (startDate != null) {
                    LocalDateTime startDateTime = startDate.toLocalDate().atStartOfDay();
                    if (t.getDateTime().isBefore(startDateTime)) {
                        continue;
                    }
                }
                
                if (endDate != null) {
                    LocalDateTime endDateTime = endDate.toLocalDate().atTime(23, 59, 59);
                    if (t.getDateTime().isAfter(endDateTime)) {
                        continue;
                    }
                }
                
                filtered.add(t);
            }
            
            return filtered;
        }
        
        public void setSaveSuccessful(boolean successful) {
            this.saveSuccessful = successful;
        }
        
        public void clear() {
            transactions.clear();
        }
        
        public List<DriverTransaction> getAllTransactions() {
            return new ArrayList<>(transactions);
        }
    }
    
    @Before
    public void setUp() {
        testDao = new TestDriverTransactionDAO();

        try {
            service = new DriverTransactionService();
            java.lang.reflect.Field daoField = DriverTransactionService.class.getDeclaredField("transactionDao");
            daoField.setAccessible(true);
            daoField.set(service, testDao);
        } catch (Exception e) {
            fail("Could not set up test: " + e.getMessage());
        }
        

        setupTestData();
    }
    
    private void setupTestData() {
        testDao.clear();
        
        testDao.save(new DriverTransaction(1, 1, "Top Up", new BigDecimal("100.00"), 
                "Added funds", LocalDateTime.of(2025, 3, 1, 10, 0)));
        testDao.save(new DriverTransaction(2, 1, "Ride Earnings", new BigDecimal("25.50"), 
                "Trip #12345", LocalDateTime.of(2025, 3, 2, 15, 30)));
        testDao.save(new DriverTransaction(3, 1, "Commission", new BigDecimal("5.00"), 
                "Platform fee", LocalDateTime.of(2025, 3, 3, 9, 15)));
        

        testDao.save(new DriverTransaction(4, 2, "Top Up", new BigDecimal("200.00"), 
                "Added funds", LocalDateTime.of(2025, 3, 1, 11, 0)));
        testDao.save(new DriverTransaction(5, 2, "Ride Earnings", new BigDecimal("30.00"), 
                "Trip #12346", LocalDateTime.of(2025, 3, 2, 16, 0)));
    }
    
    @Test
    public void testCreateTransaction_Success() {
        // Arrange
        DriverTransaction transaction = new DriverTransaction(0, 3, "Top Up", 
                new BigDecimal("50.00"), "New driver funds", LocalDateTime.now());
        int initialCount = testDao.getAllTransactions().size();
        
        // Act
        boolean result = service.createTransaction(transaction);
        
        // Assert
        assertTrue("Transaction creation should return true", result);
        assertEquals("Transaction list should increase by one", initialCount + 1, 
                testDao.getAllTransactions().size());
    }
    
    @Test
    public void testCreateTransaction_Failure() {
        // Arrange
        DriverTransaction transaction = new DriverTransaction(0, 3, "Top Up", 
                new BigDecimal("50.00"), "New driver funds", LocalDateTime.now());
        testDao.setSaveSuccessful(false);
        int initialCount = testDao.getAllTransactions().size();
        
        // Act
        boolean result = service.createTransaction(transaction);
        
        // Assert
        assertFalse("Transaction creation should return false", result);
        assertEquals("Transaction list should remain the same", initialCount, 
                testDao.getAllTransactions().size());
        
        // Reset for other tests
        testDao.setSaveSuccessful(true);
    }
    
    @Test
    public void testGetTransactionsByDriver_WithExistingDriver() {
        // Act
        List<DriverTransaction> transactions = service.getTransactionsByDriver(1);
        
        // Assert
        assertNotNull("Returned list should not be null", transactions);
        assertEquals("Should return 3 transactions for driver 1", 3, transactions.size());
        
 
        for (DriverTransaction transaction : transactions) {
            assertEquals("All transactions should be for driver 1", 1, transaction.getDriverId());
        }
    }
    
    @Test
    public void testGetTransactionsByDriver_WithNonExistingDriver() {
        // Act
        List<DriverTransaction> transactions = service.getTransactionsByDriver(999);
        
        // Assert
        assertNotNull("Returned list should not be null even for non-existing driver", transactions);
        assertEquals("Should return empty list for non-existing driver", 0, transactions.size());
    }
    
    @Test
    public void testGetFilteredTransactions_ByType() {
        // Act
        List<DriverTransaction> transactions = service.getFilteredTransactions(1, "Top Up", null, null);
        
        // Assert
        assertNotNull("Returned list should not be null", transactions);
        assertEquals("Should return 1 Top Up transaction for driver 1", 1, transactions.size());
        assertEquals("Transaction type should be Top Up", "Top Up", transactions.get(0).getTransactionType());
    }
    
    @Test
    public void testGetFilteredTransactions_ByDateRange() {
        // Arrange
        Date startDate = Date.valueOf("2025-03-02");
        Date endDate = Date.valueOf("2025-03-03");
        
        // Act
        List<DriverTransaction> transactions = service.getFilteredTransactions(1, null, startDate, endDate);
        
        // Assert
        assertNotNull("Returned list should not be null", transactions);
        assertEquals("Should return 2 transactions in the date range", 2, transactions.size());
        
  
        for (DriverTransaction transaction : transactions) {
            LocalDateTime transactionDate = transaction.getDateTime();
            assertTrue("Transaction date should be after or equal to start date", 
                    !transactionDate.isBefore(startDate.toLocalDate().atStartOfDay()));
            assertTrue("Transaction date should be before or equal to end date", 
                    !transactionDate.isAfter(endDate.toLocalDate().atTime(23, 59, 59)));
        }
    }
    
    @Test
    public void testGetFilteredTransactions_ByTypeAndDateRange() {
        // Arrange
        Date startDate = Date.valueOf("2025-03-01");
        Date endDate = Date.valueOf("2025-03-02");
        
        // Act
        List<DriverTransaction> transactions = service.getFilteredTransactions(1, "Top Up", startDate, endDate);
        
        // Assert
        assertNotNull("Returned list should not be null", transactions);
        assertEquals("Should return 1 Top Up transaction in the date range", 1, transactions.size());
        assertEquals("Transaction type should be Top Up", "Top Up", transactions.get(0).getTransactionType());
    }
    
    @Test
    public void testGetFilteredTransactions_WithNullType() {
        // Arrange
        Date startDate = Date.valueOf("2025-03-01");
        
        // Act
        List<DriverTransaction> transactions = service.getFilteredTransactions(1, null, startDate, null);
        
        // Assert
        assertNotNull("Returned list should not be null", transactions);
        assertEquals("Should return all transactions after start date", 3, transactions.size());
    }
    
    @Test
    public void testGetFilteredTransactions_WithEmptyType() {
        // Arrange
        Date startDate = Date.valueOf("2025-03-01");
        
        // Act
        List<DriverTransaction> transactions = service.getFilteredTransactions(1, "", startDate, null);
        
        // Assert
        assertNotNull("Returned list should not be null", transactions);
        assertEquals("Should return all transactions after start date", 3, transactions.size());
    }
    
    @Test
    public void testGetFilteredTransactions_WithNullDates() {
        // Act
        List<DriverTransaction> transactions = service.getFilteredTransactions(1, "Ride Earnings", null, null);
        
        // Assert
        assertNotNull("Returned list should not be null", transactions);
        assertEquals("Should return transactions by type only", 1, transactions.size());
        assertEquals("Transaction type should be Ride Earnings", "Ride Earnings", 
                transactions.get(0).getTransactionType());
    }
    
    @Test
    public void testGetFilteredTransactions_WithNonExistingType() {
        // Act
        List<DriverTransaction> transactions = service.getFilteredTransactions(1, "Non-existing Type", null, null);
        
        // Assert
        assertNotNull("Returned list should not be null", transactions);
        assertEquals("Should return empty list for non-existing type", 0, transactions.size());
    }
    
    @Test
    public void testGetFilteredTransactions_WithNonOverlappingDateRange() {
        // Arrange
        Date startDate = Date.valueOf("2024-01-01");
        Date endDate = Date.valueOf("2024-02-01");
        
        // Act
        List<DriverTransaction> transactions = service.getFilteredTransactions(1, null, startDate, endDate);
        
        // Assert
        assertNotNull("Returned list should not be null", transactions);
        assertEquals("Should return empty list for non-overlapping date range", 0, transactions.size());
    }
}