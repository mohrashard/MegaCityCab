package com.megacitycab.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.megacitycab.dao.CardDAO;
import com.megacitycab.model.Card;
import com.megacitycab.service.CardService;

public class CardServiceTest extends TestCase {
    
    private CardService cardService;
    private TestCardDAO mockCardDAO;
    

    private class TestCardDAO extends CardDAO {
        private List<Card> cards = new ArrayList<>();
        private boolean throwErrorOnUpdate = false;
        private boolean throwErrorOnCheckCardOwner = false;
        
        @Override
        public void addCard(Card card) {
            card.setCardId(cards.size() + 1);
            cards.add(card);
        }
        
        @Override
        public List<Card> getCardsByPassengerId(int passengerId) {
            List<Card> passengerCards = new ArrayList<>();
            for (Card card : cards) {
                if (card.getPassengerId() == passengerId) {
                    passengerCards.add(card);
                }
            }
            return passengerCards;
        }
        
        @Override
        public void updateCard(Card card) {
            if (throwErrorOnUpdate) {
                throw new RuntimeException("Simulated database error");
            }
            
            for (int i = 0; i < cards.size(); i++) {
                if (cards.get(i).getCardId() == card.getCardId()) {
                    cards.set(i, card);
                    return;
                }
            }
        }
        
        @Override
        public void deleteCard(int cardId) {
            cards.removeIf(card -> card.getCardId() == cardId);
        }
        
        @Override
        public boolean checkCardOwner(int cardId, int passengerId) {
            if (throwErrorOnCheckCardOwner) {
                throw new RuntimeException("Simulated database error during ownership check");
            }
            
            for (Card card : cards) {
                if (card.getCardId() == cardId) {
                    return card.getPassengerId() == passengerId;
                }
            }
            return false;
        }

        public void setThrowErrorOnUpdate(boolean throwError) {
            this.throwErrorOnUpdate = throwError;
        }
        
        public void setThrowErrorOnCheckCardOwner(boolean throwError) {
            this.throwErrorOnCheckCardOwner = throwError;
        }
        
        public List<Card> getAllCards() {
            return this.cards;
        }
        
        public void clearCards() {
            this.cards.clear();
        }
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mockCardDAO = new TestCardDAO();
        cardService = new CardService();
        java.lang.reflect.Field daoField = CardService.class.getDeclaredField("cardDAO");
        daoField.setAccessible(true);
        daoField.set(cardService, mockCardDAO);
    }
    
    private Card createTestCard(int passengerId) {
        Card card = new Card();
        card.setPassengerId(passengerId);
        card.setCardNumber("4111111111111111");
        card.setCardholderName("Test User");
        card.setExpiryDate("12/25");
        card.setCvc("123");
        return card;
    }
    

    public void testAddCard() {
        Card card = createTestCard(1);
        cardService.addCard(card);
        
        List<Card> cards = mockCardDAO.getAllCards();
        assertEquals(1, cards.size());
        assertEquals(1, cards.get(0).getCardId());
        assertEquals("Test User", cards.get(0).getCardholderName());
    }
    
  
    public void testAddCardWithNullCard() {
        try {
            cardService.addCard(null);
            fail("Expected NullPointerException but no exception was thrown");
        } catch (NullPointerException e) {

        }
    }
    

    public void testGetCardsByPassengerIdWithExistingPassenger() {
        Card card1 = createTestCard(1);
        Card card2 = createTestCard(1);
        cardService.addCard(card1);
        cardService.addCard(card2);
        
        Card card3 = createTestCard(2);
        cardService.addCard(card3);
        
        List<Card> cards = cardService.getCardsByPassengerId(1);
        assertEquals(2, cards.size());
    }
    

    public void testGetCardsByPassengerIdWithNonExistingPassenger() {
        Card card = createTestCard(1);
        cardService.addCard(card);
        
        List<Card> cards = cardService.getCardsByPassengerId(999);
        assertEquals(0, cards.size());
    }
    

    public void testUpdateCardWithValidOwnership() throws Exception {

        Card card = createTestCard(1);
        cardService.addCard(card);
        int cardId = mockCardDAO.getAllCards().get(0).getCardId();
        

        card.setCardId(cardId);
        card.setCardholderName("Updated User");
        cardService.updateCard(card);
        

        List<Card> cards = mockCardDAO.getAllCards();
        assertEquals(1, cards.size());
        assertEquals("Updated User", cards.get(0).getCardholderName());
    }
    

    public void testUpdateCardWithInvalidOwnership() {

        Card card = createTestCard(1);
        cardService.addCard(card);
        int cardId = mockCardDAO.getAllCards().get(0).getCardId();
        
    
        Card updatedCard = createTestCard(2);
        updatedCard.setCardId(cardId);
        updatedCard.setCardholderName("Unauthorized Update");
        
        try {
            cardService.updateCard(updatedCard);
            fail("Expected SecurityException but no exception was thrown");
        } catch (SecurityException e) {

            assertEquals("Unauthorized card update attempt", e.getMessage());
        } catch (Exception e) {
            fail("Wrong exception type: " + e.getClass().getName());
        }
        
    
        List<Card> cards = mockCardDAO.getAllCards();
        assertEquals("Test User", cards.get(0).getCardholderName());
    }
    

    public void testUpdateCardWithDatabaseError() {
 
        Card card = createTestCard(1);
        cardService.addCard(card);
        int cardId = mockCardDAO.getAllCards().get(0).getCardId();
        
  
        mockCardDAO.setThrowErrorOnUpdate(true);
        
    
        card.setCardId(cardId);
        
        try {
            cardService.updateCard(card);
            fail("Expected Exception but no exception was thrown");
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    

    public void testDeleteCard() {
        Card card = createTestCard(1);
        cardService.addCard(card);
        int cardId = mockCardDAO.getAllCards().get(0).getCardId();
        
        cardService.deleteCard(cardId);
        
        List<Card> cards = mockCardDAO.getAllCards();
        assertEquals(0, cards.size());
    }
    

    public void testDeleteCardWithNonExistingCard() {
 
        Card card = createTestCard(1);
        cardService.addCard(card);

        cardService.deleteCard(999);
        
  
        List<Card> cards = mockCardDAO.getAllCards();
        assertEquals(1, cards.size());
    }
    

    public void testCheckCardOwnerWithValidOwnership() {
 
        Card card = createTestCard(1);
        cardService.addCard(card);
        int cardId = mockCardDAO.getAllCards().get(0).getCardId();
        
        boolean isOwner = cardService.checkCardOwner(cardId, 1);
        assertTrue(isOwner);
    }
    

    public void testCheckCardOwnerWithInvalidOwnership() {

        Card card = createTestCard(1);
        cardService.addCard(card);
        int cardId = mockCardDAO.getAllCards().get(0).getCardId();

        boolean isOwner = cardService.checkCardOwner(cardId, 2);
        assertFalse(isOwner);
    }
    
 
    public void testCheckCardOwnerWithNonExistingCard() {
        boolean isOwner = cardService.checkCardOwner(999, 1);
        assertFalse(isOwner);
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mockCardDAO.clearCards();
    }
}