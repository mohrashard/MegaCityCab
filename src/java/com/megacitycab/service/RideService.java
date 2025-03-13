
package com.megacitycab.service;
import com.megacitycab.dto.RideDTO;
import java.util.List;

public interface RideService {
    List<RideDTO> getCurrentRides(int driverId);
    List<RideDTO> getEndedRides(int driverId);
    boolean acceptRide(int bookingId, int driverId);
    boolean cancelRide(int bookingId, String reason);
    boolean endRide(int bookingId);
}