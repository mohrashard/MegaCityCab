package com.megacitycab.repository;

import com.megacitycab.config.DBConnection;
import com.megacitycab.model.DriverInfo;
import com.megacitycab.repository.DriverRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DriverRepositoryImpl implements DriverRepository {

    @Override
    public List<DriverInfo> getDriversByVehicleType(String vehicleType) {
        String sql = "SELECT driver_id, full_name, email, phone, license_no, vehicle_type, status " +
                     "FROM Drivers WHERE vehicle_type = ?";
        List<DriverInfo> drivers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, vehicleType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                drivers.add(mapResultSetToDriverInfo(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving drivers by vehicle type: " + e.getMessage());
        }
        
        return drivers;
    }

    @Override
    public DriverInfo getDriverById(int id) {
        String sql = "SELECT driver_id, full_name, email, phone, license_no, vehicle_type, status " +
                     "FROM Drivers WHERE driver_id = ?";
        DriverInfo driver = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                driver = mapResultSetToDriverInfo(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving driver: " + e.getMessage());
        }
        
        return driver;
    }

    @Override
    public List<DriverInfo> getAllDrivers() {
        String sql = "SELECT driver_id, full_name, email, phone, license_no, vehicle_type, status FROM Drivers";
        List<DriverInfo> drivers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                drivers.add(mapResultSetToDriverInfo(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving all drivers: " + e.getMessage());
        }
        
        return drivers;
    }

    private DriverInfo mapResultSetToDriverInfo(ResultSet rs) throws SQLException {
        DriverInfo driver = new DriverInfo();
        driver.setDriverId(rs.getInt("driver_id"));
        driver.setFullName(rs.getString("full_name"));
        driver.setEmail(rs.getString("email"));
        driver.setPhone(rs.getString("phone"));
        driver.setLicenseNo(rs.getString("license_no"));
        driver.setVehicleType(rs.getString("vehicle_type"));
        driver.setStatus(rs.getString("status"));
        return driver;
    }
}
