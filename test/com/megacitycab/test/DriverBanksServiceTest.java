package com.megacitycab.test;

import com.megacitycab.dao.DriverBankDAO;
import com.megacitycab.model.DriverBank;
import com.megacitycab.service.DriverBanksService;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class DriverBanksServiceTest extends TestCase {
    
    private DriverBanksService service;
    private DriverBankDAO mockDao;
    

    private class MockDriverBankDAO implements DriverBankDAO {
        private boolean saveShouldSucceed = true;
        private boolean updateShouldSucceed = true;
        private boolean deleteShouldSucceed = true;
        private boolean setDefaultShouldSucceed = true;
        private DriverBank storedBank;
        private List<DriverBank> bankList = new ArrayList<>();
        private int bankCounter = 0;
        
        @Override
        public boolean save(DriverBank bank) {
            if (saveShouldSucceed) {
                bank.setBankId(++bankCounter);
                bankList.add(bank);
                storedBank = bank;
                return true;
            }
            return false;
        }
        
        @Override
        public boolean update(DriverBank bank) {
            if (updateShouldSucceed) {
                for (int i = 0; i < bankList.size(); i++) {
                    if (bankList.get(i).getBankId() == bank.getBankId()) {
                        bankList.set(i, bank);
                        storedBank = bank;
                        return true;
                    }
                }
            }
            return false;
        }
        
        @Override
        public boolean delete(int bankId) {
            if (deleteShouldSucceed) {
                for (int i = 0; i < bankList.size(); i++) {
                    if (bankList.get(i).getBankId() == bankId) {
                        bankList.remove(i);
                        return true;
                    }
                }
            }
            return false;
        }
        
        @Override
        public Optional<DriverBank> findById(int bankId) {
            for (DriverBank bank : bankList) {
                if (bank.getBankId() == bankId) {
                    return Optional.of(bank);
                }
            }
            return Optional.empty();
        }
        
        @Override
        public List<DriverBank> findByDriverId(int driverId) {
            List<DriverBank> result = new ArrayList<>();
            for (DriverBank bank : bankList) {
                if (bank.getDriverId() == driverId) {
                    result.add(bank);
                }
            }
            return result;
        }
        
        @Override
        public int countByDriverId(int driverId) {
            int count = 0;
            for (DriverBank bank : bankList) {
                if (bank.getDriverId() == driverId) {
                    count++;
                }
            }
            return count;
        }
        
        @Override
        public boolean setDefaultBank(int driverId, int bankId) {
            if (setDefaultShouldSucceed) {
                for (DriverBank bank : bankList) {
                    if (bank.getDriverId() == driverId) {
                        bank.setDefault(bank.getBankId() == bankId);
                    }
                }
                return true;
            }
            return false;
        }
        
        public void setSaveShouldSucceed(boolean value) {
            this.saveShouldSucceed = value;
        }
        
        public void setUpdateShouldSucceed(boolean value) {
            this.updateShouldSucceed = value;
        }
        
        public void setDeleteShouldSucceed(boolean value) {
            this.deleteShouldSucceed = value;
        }
        
        public void setSetDefaultShouldSucceed(boolean value) {
            this.setDefaultShouldSucceed = value;
        }
        
        public void addBank(DriverBank bank) {
            bank.setBankId(++bankCounter);
            bankList.add(bank);
        }
    }
    

    private class TestableDriverBanksService extends DriverBanksService {
        private DriverBankDAO customDao;
        
        public TestableDriverBanksService(DriverBankDAO dao) {
            this.customDao = dao;
        }
        
        protected DriverBankDAO getDao() {
            return customDao;
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mockDao = new MockDriverBankDAO();
        service = new TestableDriverBanksService(mockDao);
    }
    

    private DriverBank createSampleBank(int driverId, boolean isDefault) {
        DriverBank bank = new DriverBank();
        bank.setDriverId(driverId);
        bank.setBankName("Test Bank");
        bank.setAccountNumber("1234567890");
        bank.setAccountHolderName("Test Driver");
        bank.setIfscCode("TEST0001234");
        bank.setDefault(isDefault);
        return bank;
    }
  
    public void testAddBankSuccess() {
        DriverBank bank = createSampleBank(1, true);
        boolean result = service.addBank(bank);
        assertTrue("Adding bank should succeed", result);
    }
    

    public void testAddBankFailure() {
        ((MockDriverBankDAO) mockDao).setSaveShouldSucceed(false);
        DriverBank bank = createSampleBank(1, true);
        boolean result = service.addBank(bank);
        assertFalse("Adding bank should fail when DAO returns false", result);
    }
    

    public void testUpdateBankSuccess() {

        DriverBank bank = createSampleBank(1, true);
        ((MockDriverBankDAO) mockDao).addBank(bank);

        bank.setBankName("Updated Bank");
        boolean result = service.updateBank(bank);
        assertTrue("Updating bank should succeed", result);
        
        Optional<DriverBank> updated = service.getBankById(bank.getBankId());
        assertTrue("Bank should be found after update", updated.isPresent());
        assertEquals("Bank name should be updated", "Updated Bank", updated.get().getBankName());
    }

    public void testUpdateBankFailure() {
        ((MockDriverBankDAO) mockDao).setUpdateShouldSucceed(false);
        DriverBank bank = createSampleBank(1, true);
        ((MockDriverBankDAO) mockDao).addBank(bank);
        
        boolean result = service.updateBank(bank);
        assertFalse("Updating bank should fail when DAO returns false", result);
    }
    

    public void testDeleteBankSuccess() {

        DriverBank bank = createSampleBank(1, true);
        ((MockDriverBankDAO) mockDao).addBank(bank);

        boolean result = service.deleteBank(bank.getBankId());
        assertTrue("Deleting bank should succeed", result);

        Optional<DriverBank> deleted = service.getBankById(bank.getBankId());
        assertFalse("Bank should not be found after delete", deleted.isPresent());
    }
    

    public void testDeleteBankFailure() {
        ((MockDriverBankDAO) mockDao).setDeleteShouldSucceed(false);
        DriverBank bank = createSampleBank(1, true);
        ((MockDriverBankDAO) mockDao).addBank(bank);
        
        boolean result = service.deleteBank(bank.getBankId());
        assertFalse("Deleting bank should fail when DAO returns false", result);
    }
    

    public void testGetBankByIdExisting() {

        DriverBank bank = createSampleBank(1, true);
        ((MockDriverBankDAO) mockDao).addBank(bank);
        

        Optional<DriverBank> result = service.getBankById(bank.getBankId());
        assertTrue("Bank should be found by ID", result.isPresent());
        assertEquals("Bank ID should match", bank.getBankId(), result.get().getBankId());
    }
    

    public void testGetBankByIdNonExisting() {
        Optional<DriverBank> result = service.getBankById(999);
        assertFalse("Non-existing bank should not be found", result.isPresent());
    }
    

    public void testGetDriverBanksExisting() {

        DriverBank bank1 = createSampleBank(1, true);
        DriverBank bank2 = createSampleBank(1, false);
        ((MockDriverBankDAO) mockDao).addBank(bank1);
        ((MockDriverBankDAO) mockDao).addBank(bank2);
        
 
        DriverBank bank3 = createSampleBank(2, true);
        ((MockDriverBankDAO) mockDao).addBank(bank3);
        

        List<DriverBank> result = service.getDriverBanks(1);
        assertEquals("Should return 2 banks for driver 1", 2, result.size());
    }

    public void testGetDriverBanksNonExisting() {
        List<DriverBank> result = service.getDriverBanks(999);
        assertTrue("Should return empty list for non-existing driver", result.isEmpty());
    }

    public void testSetDefaultBankSuccess() {

        DriverBank bank1 = createSampleBank(1, true);
        DriverBank bank2 = createSampleBank(1, false);
        ((MockDriverBankDAO) mockDao).addBank(bank1);
        ((MockDriverBankDAO) mockDao).addBank(bank2);
        

        boolean result = service.setDefaultBank(1, bank2.getBankId());
        assertTrue("Setting default bank should succeed", result);
    }
    

    public void testSetDefaultBankFailure() {
        ((MockDriverBankDAO) mockDao).setSetDefaultShouldSucceed(false);
        
        // Add a bank
        DriverBank bank = createSampleBank(1, false);
        ((MockDriverBankDAO) mockDao).addBank(bank);
        
        boolean result = service.setDefaultBank(1, bank.getBankId());
        assertFalse("Setting default bank should fail when DAO returns false", result);
    }
    
 
    public void testAddNullBank() {
        try {
            service.addBank(null);
            fail("Should throw NullPointerException when bank is null");
        } catch (NullPointerException e) {

        }
    }
    

    public void testGetBankByInvalidId() {
        Optional<DriverBank> result = service.getBankById(-1);
        assertFalse("Invalid bank ID should not be found", result.isPresent());
    }
    
    public void testBankWithNoAccountNumber() {
        DriverBank bank = createSampleBank(1, true);
        bank.setAccountNumber(null);
        
        boolean result = service.addBank(bank);
        assertTrue("Adding bank with null account number should still succeed", result);
       
        List<DriverBank> banks = service.getDriverBanks(1);
        assertEquals("Should have one bank", 1, banks.size());
        assertEquals("Masked account number should be '****' for null account", "****", banks.get(0).getMaskedAccountNumber());
    }
}