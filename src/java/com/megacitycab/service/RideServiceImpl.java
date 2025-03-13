package com.megacitycab.service;

import com.megacitycab.dao.RideDAO;
import com.megacitycab.dao.RideDAOImpl;
import com.megacitycab.dto.RideDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RideServiceImpl implements RideService {
    
    private final RideDAO rideDAO;
    private static final Logger LOGGER = Logger.getLogger(RideServiceImpl.class.getName());
    
    public RideServiceImpl() {
        this.rideDAO = new RideDAOImpl();
    }
    

    public RideServiceImpl(RideDAO rideDAO) {
        this.rideDAO = rideDAO;
    }
    
    @Override
    public List<RideDTO> getCurrentRides(int driverId) {
        try {
            return rideDAO.getCurrentRides(driverId);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error getting current rides", ex);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<RideDTO> getEndedRides(int driverId) {
        try {
            return rideDAO.getEndedRides(driverId);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error getting ended rides", ex);
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean acceptRide(int bookingId, int driverId) {
        try {
            return rideDAO.acceptRide(bookingId, driverId);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error accepting ride", ex);
            return false;
        }
    }
    
    @Override
    public boolean cancelRide(int bookingId, String reason) {
        try {
            return rideDAO.cancelRide(bookingId, reason);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error cancelling ride", ex);
            return false;
        }
    }
    
    @Override
    public boolean endRide(int bookingId) {
        try {
            return rideDAO.endRide(bookingId);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error ending ride", ex);
            return false;
        }
    }
}