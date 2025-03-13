package com.megacitycab.service;

import com.megacitycab.dao.CardDAO;
import com.megacitycab.model.Card;

import java.util.List;

public class CardService {
    private CardDAO cardDAO = new CardDAO();

    public void addCard(Card card) {
        cardDAO.addCard(card);
    }

    public List<Card> getCardsByPassengerId(int passengerId) {
        return cardDAO.getCardsByPassengerId(passengerId);
    }

public void updateCard(Card card) throws Exception {
    if (!cardDAO.checkCardOwner(card.getCardId(), card.getPassengerId())) {
        throw new SecurityException("Unauthorized card update attempt");
    }
    cardDAO.updateCard(card);
}

    public void deleteCard(int cardId) {
        cardDAO.deleteCard(cardId);
    }
    
       public boolean checkCardOwner(int cardId, int passengerId) {
        return cardDAO.checkCardOwner(cardId, passengerId);
    }
}