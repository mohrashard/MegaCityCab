package com.megacitycab.test;

import com.megacitycab.dao.DriverCardDAO;
import com.megacitycab.dao.DriverCardDAOImpl;
import com.megacitycab.model.DriverCard;
import com.megacitycab.service.DriverCardsService;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DriverCardsServiceTest {
    
    private DriverCardsService service;
    private TestDriverCardDAO mockDao;
    
    @Before
    public void setUp() {

        mockDao = new TestDriverCardDAO();
        

        try {
            service = new DriverCardsService();
            java.lang.reflect.Field daoField = DriverCardsService.class.getDeclaredField("cardDao");
            daoField.setAccessible(true);
            daoField.set(service, mockDao);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
    }
    
    @Test
    public void testAddCard_Success() {
        // Arrange
        DriverCard card = new DriverCard(0, 1, "4111111111111111", "12/25", "123", "John Doe", false);
        mockDao.saveReturnValue = true;
        
        // Act
        boolean result = service.addCard(card);
        
        // Assert
        assertTrue("Adding a valid card should return true", result);
        assertEquals("The card should be passed to the DAO", card, mockDao.lastSavedCard);
    }
    
    @Test
    public void testAddCard_Failure() {
        // Arrange
        DriverCard card = new DriverCard(0, 1, "4111111111111111", "12/25", "123", "John Doe", false);
        mockDao.saveReturnValue = false;
        
        // Act
        boolean result = service.addCard(card);
        
        // Assert
        assertFalse("Adding a card when DAO fails should return false", result);
    }
    
    @Test
    public void testUpdateCard_Success() {
        // Arrange
        DriverCard card = new DriverCard(1, 1, "4111111111111111", "12/25", "123", "John Doe", false);
        mockDao.updateReturnValue = true;
        
        // Act
        boolean result = service.updateCard(card);
        
        // Assert
        assertTrue("Updating a valid card should return true", result);
        assertEquals("The card should be passed to the DAO", card, mockDao.lastUpdatedCard);
    }
    
    @Test
    public void testUpdateCard_Failure() {
        // Arrange
        DriverCard card = new DriverCard(999, 1, "4111111111111111", "12/25", "123", "John Doe", false);
        mockDao.updateReturnValue = false;
        
        // Act
        boolean result = service.updateCard(card);
        
        // Assert
        assertFalse("Updating a non-existent card should return false", result);
    }
    
    @Test
    public void testDeleteCard_Success() {
        // Arrange
        int cardId = 1;
        mockDao.deleteReturnValue = true;
        
        // Act
        boolean result = service.deleteCard(cardId);
        
        // Assert
        assertTrue("Deleting an existing card should return true", result);
        assertEquals("The card ID should be passed to the DAO", cardId, mockDao.lastDeletedCardId);
    }
    
    @Test
    public void testDeleteCard_Failure() {
        // Arrange
        int cardId = 999;
        mockDao.deleteReturnValue = false;
        
        // Act
        boolean result = service.deleteCard(cardId);
        
        // Assert
        assertFalse("Deleting a non-existent card should return false", result);
    }
    
    @Test
    public void testGetCardById_Existing() {
        // Arrange
        int cardId = 1;
        DriverCard expectedCard = new DriverCard(1, 1, "4111111111111111", "12/25", "123", "John Doe", false);
        mockDao.findByIdReturnValue = Optional.of(expectedCard);
        
        // Act
        Optional<DriverCard> result = service.getCardById(cardId);
        
        // Assert
        assertTrue("Result should be present for existing card", result.isPresent());
        assertEquals("The correct card should be returned", expectedCard, result.get());
        assertEquals("The card ID should be passed to the DAO", cardId, mockDao.lastFindByIdCardId);
    }
    
    @Test
    public void testGetCardById_NonExisting() {
        // Arrange
        int cardId = 999;
        mockDao.findByIdReturnValue = Optional.empty();
        
        // Act
        Optional<DriverCard> result = service.getCardById(cardId);
        
        // Assert
        assertFalse("Result should be empty for non-existent card", result.isPresent());
    }
    
    @Test
    public void testGetDriverCards_HasCards() {
        // Arrange
        int driverId = 1;
        List<DriverCard> expectedCards = new ArrayList<>();
        expectedCards.add(new DriverCard(1, 1, "4111111111111111", "12/25", "123", "John Doe", true));
        expectedCards.add(new DriverCard(2, 1, "5555555555554444", "01/26", "456", "John Doe", false));
        mockDao.findByDriverIdReturnValue = expectedCards;
        
        // Act
        List<DriverCard> result = service.getDriverCards(driverId);
        
        // Assert
        assertEquals("Should return the correct number of cards", 2, result.size());
        assertEquals("The driver ID should be passed to the DAO", driverId, mockDao.lastFindByDriverIdDriverId);
    }
    
    @Test
    public void testGetDriverCards_NoCards() {
        // Arrange
        int driverId = 2;
        mockDao.findByDriverIdReturnValue = new ArrayList<>();
        
        // Act
        List<DriverCard> result = service.getDriverCards(driverId);
        
        // Assert
        assertTrue("Should return an empty list when no cards exist", result.isEmpty());
    }
    
    @Test
    public void testSetDefaultCard_Success() {
        // Arrange
        int driverId = 1;
        int cardId = 2;
        mockDao.setDefaultCardReturnValue = true;
        
        // Act
        boolean result = service.setDefaultCard(driverId, cardId);
        
        // Assert
        assertTrue("Setting a default card should return true", result);
        assertEquals("The driver ID should be passed to the DAO", driverId, mockDao.lastSetDefaultDriverId);
        assertEquals("The card ID should be passed to the DAO", cardId, mockDao.lastSetDefaultCardId);
    }
    
    @Test
    public void testSetDefaultCard_Failure() {
        // Arrange
        int driverId = 1;
        int cardId = 999;
        mockDao.setDefaultCardReturnValue = false;
        
        // Act
        boolean result = service.setDefaultCard(driverId, cardId);
        
        // Assert
        assertFalse("Setting a non-existent card as default should return false", result);
    }
    
    @Test
    public void testAddCard_NullCard() {

        boolean exceptionThrown = false;
        
        // Act
        try {
            service.addCard(null);
        } catch (NullPointerException e) {
            exceptionThrown = true;
        }
        
        // Assert
        assertTrue("Adding a null card should throw NullPointerException", exceptionThrown);
    }
    
    @Test
    public void testUpdateCard_NullCard() {

        boolean exceptionThrown = false;
        
        // Act
        try {
            service.updateCard(null);
        } catch (NullPointerException e) {
            exceptionThrown = true;
        }
        
        // Assert
        assertTrue("Updating a null card should throw NullPointerException", exceptionThrown);
    }
    
    @Test
    public void testGetCardById_InvalidId() {
        // Arrange
        int cardId = -1;
        mockDao.findByIdReturnValue = Optional.empty();
        
        // Act
        Optional<DriverCard> result = service.getCardById(cardId);
        
        // Assert
        assertFalse("Result should be empty for invalid card ID", result.isPresent());
    }
    
    @Test
    public void testGetDriverCards_InvalidDriverId() {
        // Arrange
        int driverId = -1;
        mockDao.findByDriverIdReturnValue = new ArrayList<>();
        
        // Act
        List<DriverCard> result = service.getDriverCards(driverId);
        
        // Assert
        assertTrue("Should return an empty list for invalid driver ID", result.isEmpty());
    }
    

    private class TestDriverCardDAO implements DriverCardDAO {

        public DriverCard lastSavedCard;
        public DriverCard lastUpdatedCard;
        public int lastDeletedCardId;
        public int lastFindByIdCardId;
        public int lastFindByDriverIdDriverId;
        public int lastSetDefaultDriverId;
        public int lastSetDefaultCardId;
        

        public boolean saveReturnValue = false;
        public boolean updateReturnValue = false;
        public boolean deleteReturnValue = false;
        public Optional<DriverCard> findByIdReturnValue = Optional.empty();
        public List<DriverCard> findByDriverIdReturnValue = new ArrayList<>();
        public int countByDriverIdReturnValue = 0;
        public boolean setDefaultCardReturnValue = false;
        
        @Override
        public boolean save(DriverCard card) {
            lastSavedCard = card;
            return saveReturnValue;
        }
        
        @Override
        public boolean update(DriverCard card) {
            lastUpdatedCard = card;
            return updateReturnValue;
        }
        
        @Override
        public boolean delete(int cardId) {
            lastDeletedCardId = cardId;
            return deleteReturnValue;
        }
        
        @Override
        public Optional<DriverCard> findById(int cardId) {
            lastFindByIdCardId = cardId;
            return findByIdReturnValue;
        }
        
        @Override
        public List<DriverCard> findByDriverId(int driverId) {
            lastFindByDriverIdDriverId = driverId;
            return findByDriverIdReturnValue;
        }
        
        @Override
        public int countByDriverId(int driverId) {
            return countByDriverIdReturnValue;
        }
        
        @Override
        public boolean setDefaultCard(int driverId, int cardId) {
            lastSetDefaultDriverId = driverId;
            lastSetDefaultCardId = cardId;
            return setDefaultCardReturnValue;
        }
    }
}