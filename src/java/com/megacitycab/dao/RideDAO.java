package com.megacitycab.dao;

import com.megacitycab.dto.RideDTO;
import java.sql.SQLException;
import java.util.List;

public interface RideDAO {
    List<RideDTO> getCurrentRides(int driverId) throws SQLException;
    List<RideDTO> getEndedRides(int driverId) throws SQLException;
    boolean acceptRide(int bookingId, int driverId) throws SQLException;
    boolean cancelRide(int bookingId, String reason) throws SQLException;
    boolean endRide(int bookingId) throws SQLException;
}
