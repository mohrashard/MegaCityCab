package com.megacitycab.test;

import com.megacitycab.dao.AdminBankDAO;
import com.megacitycab.model.AdminBank;
import com.megacitycab.service.AdminBankService;
import com.megacitycab.service.AdminBankServiceImpl;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AdminBankServiceImplTest {
    
    private AdminBankService adminBankService;
    private AdminBankDAO adminBankDAO;
    

    private AdminBank createTestAdminBank(int bankId, int adminId) {
        AdminBank bank = new AdminBank();
        bank.setBankId(bankId);
        bank.setAdminId(adminId);
        bank.setBankName("Test Bank");
        bank.setAccountNumber("123456789");
        bank.setAccountHolder("Test Account Holder");
        bank.setBranch("Grandpass");
        return bank;
    }
    
    @Before
    public void setUp() {
        adminBankService = new AdminBankServiceImpl();
        adminBankDAO = new TestAdminBankDAO();
        try {
            java.lang.reflect.Field daoField = AdminBankServiceImpl.class.getDeclaredField("adminbankDAO");
            daoField.setAccessible(true);
            daoField.set(adminBankService, adminBankDAO);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
    }
    
    @Test
    public void testGetAllBanksByAdminId() throws SQLException {
        List<AdminBank> banks = adminBankService.getAllBanksByAdminId(1);
        assertNotNull("Banks list should not be null", banks);
        assertEquals("Should return 2 banks for admin ID 1", 2, banks.size());
        banks = adminBankService.getAllBanksByAdminId(99);
        assertNotNull("Banks list should not be null even when empty", banks);
        assertEquals("Should return empty list for admin with no banks", 0, banks.size());
    }
    
    @Test
    public void testGetBankById() throws SQLException {
        AdminBank bank = adminBankService.getBankById(1);
        assertNotNull("Bank should not be null", bank);
        assertEquals("Bank ID should match", 1, bank.getBankId());
        bank = adminBankService.getBankById(99);
        assertNull("Bank should be null for non-existent ID", bank);
    }
    
    @Test
    public void testAddBank() throws SQLException {
        AdminBank newBank = createTestAdminBank(0, 2);
        boolean result = adminBankService.addBank(newBank);
        assertTrue("Should return true when bank is added successfully", result);
        newBank = createTestAdminBank(0, 3);
        result = adminBankService.addBank(newBank);
        assertFalse("Should return false when bank limit is reached", result);
    }
    
    @Test
    public void testUpdateBank() throws SQLException {
        AdminBank bank = createTestAdminBank(1, 1);
        bank.setBankName("Updated Bank Name");
        boolean result = adminBankService.updateBank(bank);
        assertTrue("Should return true when bank is updated successfully", result);
        bank = createTestAdminBank(99, 1);
        result = adminBankService.updateBank(bank);
        assertFalse("Should return false when bank doesn't exist", result);
    }
    
    @Test
    public void testDeleteBank() throws SQLException {
        boolean result = adminBankService.deleteBank(1);
        assertTrue("Should return true when bank is deleted successfully", result);
        result = adminBankService.deleteBank(99);
        assertFalse("Should return false when bank doesn't exist", result);
    }
    
    @Test
    public void testCanAddBank() throws SQLException {
        boolean result = adminBankService.canAddBank(1);
        assertTrue("Should return true when admin has less than max banks", result);
        result = adminBankService.canAddBank(3);
        assertFalse("Should return false when admin already has max banks", result);
    }
    
    @Test(expected = SQLException.class)
    public void testSQLException() throws SQLException {
        adminBankService.getAllBanksByAdminId(-1);
    }
    
    @Test
    public void testWithNullAdminBank() throws SQLException {
        try {
            adminBankService.addBank(null);
            fail("Should throw NullPointerException when bank is null");
        } catch (NullPointerException e) {
        
        }
    }

    private class TestAdminBankDAO implements AdminBankDAO {
        private List<AdminBank> banks;
        
        public TestAdminBankDAO() {
            banks = new ArrayList<>();

            AdminBank bank1 = createTestAdminBank(1, 1);
            AdminBank bank2 = createTestAdminBank(2, 1);
            AdminBank bank3 = createTestAdminBank(3, 3);
            AdminBank bank4 = createTestAdminBank(4, 3);
            AdminBank bank5 = createTestAdminBank(5, 3);
            
            banks.add(bank1);
            banks.add(bank2);
            banks.add(bank3);
            banks.add(bank4);
            banks.add(bank5);
        }
        
        @Override
        public List<AdminBank> getAllBanksByAdminId(int adminId) throws SQLException {
            if (adminId < 0) {
                throw new SQLException("Invalid admin ID");
            }
            
            List<AdminBank> result = new ArrayList<>();
            for (AdminBank bank : banks) {
                if (bank.getAdminId() == adminId) {
                    result.add(bank);
                }
            }
            return result;
        }
        
        @Override
        public AdminBank getBankById(int bankId) throws SQLException {
            for (AdminBank bank : banks) {
                if (bank.getBankId() == bankId) {
                    return bank;
                }
            }
            return null;
        }
        
        @Override
        public boolean addBank(AdminBank bank) throws SQLException {
            if (bank == null) {
                throw new NullPointerException("Bank cannot be null");
            }
            int newId = banks.size() + 1;
            bank.setBankId(newId);
            banks.add(bank);
            return true;
        }
        
        @Override
        public boolean updateBank(AdminBank bank) throws SQLException {
            if (bank == null) {
                throw new NullPointerException("Bank cannot be null");
            }
            
            for (int i = 0; i < banks.size(); i++) {
                if (banks.get(i).getBankId() == bank.getBankId()) {
                    banks.set(i, bank);
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public boolean deleteBank(int bankId) throws SQLException {
            for (int i = 0; i < banks.size(); i++) {
                if (banks.get(i).getBankId() == bankId) {
                    banks.remove(i);
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public int getBankCountByAdminId(int adminId) throws SQLException {
            int count = 0;
            for (AdminBank bank : banks) {
                if (bank.getAdminId() == adminId) {
                    count++;
                }
            }
            return count;
        }
    }
}