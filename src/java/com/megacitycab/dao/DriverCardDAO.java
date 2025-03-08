package com.megacitycab.dao;

import com.megacitycab.model.DriverCard;

import java.util.List;
import java.util.Optional;

public interface DriverCardDAO {
    boolean save(DriverCard card);
    boolean update(DriverCard card);
    boolean delete(int cardId);
    Optional<DriverCard> findById(int cardId);
    List<DriverCard> findByDriverId(int driverId);
    int countByDriverId(int driverId);
    boolean setDefaultCard(int driverId, int cardId);
}
