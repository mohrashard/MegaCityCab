
package com.megacitycab.service;

import com.megacitycab.dao.DriverCardDAO;
import com.megacitycab.dao.DriverCardDAOImpl;

import com.megacitycab.model.DriverCard;
import java.util.List;
import java.util.Optional;

public class DriverCardsService {
    private final DriverCardDAO cardDao = new DriverCardDAOImpl();

    public boolean addCard(DriverCard card) {
        return cardDao.save(card);
    }

    public boolean updateCard(DriverCard card) {
        return cardDao.update(card);
    }

    public boolean deleteCard(int cardId) {
        return cardDao.delete(cardId);
    }

    public Optional<DriverCard> getCardById(int cardId) {
        return cardDao.findById(cardId);
    }

    public List<DriverCard> getDriverCards(int driverId) {
        return cardDao.findByDriverId(driverId);
    }

    public boolean setDefaultCard(int driverId, int cardId) {
        return cardDao.setDefaultCard(driverId, cardId);
    }
}