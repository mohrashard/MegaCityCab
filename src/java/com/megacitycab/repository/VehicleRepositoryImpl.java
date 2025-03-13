package com.megacitycab.repository;

import com.megacitycab.config.DBConnection;
import com.megacitycab.model.Vehicle;
import com.megacitycab.repository.VehicleRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleRepositoryImpl implements VehicleRepository {

    @Override
    public void createVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO Vehicles (plate_number, vehicle_type, passenger_capacity, brand, model, status, driver_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, vehicle.getPlateNumber());
            pstmt.setString(2, vehicle.getVehicleType());
            pstmt.setInt(3, vehicle.getPassengerCapacity());
            pstmt.setString(4, vehicle.getBrand());
            pstmt.setString(5, vehicle.getModel());
            pstmt.setString(6, vehicle.getStatus());
            if (vehicle.getDriverId() != null) {
                pstmt.setInt(7, vehicle.getDriverId());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vehicle.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error creating vehicle: " + e.getMessage());
        }
    }

    @Override
    public Vehicle getVehicleById(int id) {
        String sql = "SELECT * FROM Vehicles WHERE id = ?";
        Vehicle vehicle = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                vehicle = mapResultSetToVehicle(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving vehicle: " + e.getMessage());
        }
        
        return vehicle;
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        String sql = "SELECT * FROM Vehicles";
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving vehicles: " + e.getMessage());
        }
        
        return vehicles;
    }

    @Override
    public List<Vehicle> getVehiclesByType(String type) {
        String sql = "SELECT * FROM Vehicles WHERE vehicle_type = ?";
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving vehicles by type: " + e.getMessage());
        }
        
        return vehicles;
    }

    @Override
    public List<Vehicle> getVehiclesByStatus(String status) {
        String sql = "SELECT * FROM Vehicles WHERE status = ?";
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving vehicles by status: " + e.getMessage());
        }
        
        return vehicles;
    }

    @Override
    public List<Vehicle> searchVehicles(String keyword) {
        String sql = "SELECT * FROM Vehicles WHERE " +
                     "plate_number LIKE ? OR " +
                     "model LIKE ? OR " +
                     "brand LIKE ? OR " +
                     "status LIKE ?";
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchParam = "%" + keyword + "%";
            pstmt.setString(1, searchParam);
            pstmt.setString(2, searchParam);
            pstmt.setString(3, searchParam);
            pstmt.setString(4, searchParam);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error searching vehicles: " + e.getMessage());
        }
        
        return vehicles;
    }

    @Override
    public boolean updateVehicle(Vehicle vehicle) {
        String sql = "UPDATE Vehicles SET plate_number = ?, vehicle_type = ?, passenger_capacity = ?, " +
                     "brand = ?, model = ?, status = ?, driver_id = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, vehicle.getPlateNumber());
            pstmt.setString(2, vehicle.getVehicleType());
            pstmt.setInt(3, vehicle.getPassengerCapacity());
            pstmt.setString(4, vehicle.getBrand());
            pstmt.setString(5, vehicle.getModel());
            pstmt.setString(6, vehicle.getStatus());
            
            if (vehicle.getDriverId() != null) {
                pstmt.setInt(7, vehicle.getDriverId());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            
            pstmt.setInt(8, vehicle.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating vehicle: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteVehicle(int id) {
        String sql = "DELETE FROM Vehicles WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting vehicle: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean assignDriverToVehicle(int vehicleId, int driverId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

    
            String vehicleSql = "UPDATE Vehicles SET driver_id = ?, status = 'Assigned' WHERE id = ?";
            try (PreparedStatement vehicleStmt = conn.prepareStatement(vehicleSql)) {
                vehicleStmt.setInt(1, driverId);
                vehicleStmt.setInt(2, vehicleId);
                vehicleStmt.executeUpdate();
            }

          
            String vehicleNumber = getVehicleNumber(conn, vehicleId);
            

            String driverSql = "UPDATE Drivers SET vehicle_number = ? WHERE driver_id = ?";
            try (PreparedStatement driverStmt = conn.prepareStatement(driverSql)) {
                driverStmt.setString(1, vehicleNumber);
                driverStmt.setInt(2, driverId);
                driverStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Rollback failed: " + ex.getMessage());
            }
            System.out.println("Assignment error: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Connection close error: " + e.getMessage());
            }
        }
    }

    private String getVehicleNumber(Connection conn, int vehicleId) throws SQLException {
        String sql = "SELECT plate_number FROM Vehicles WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("plate_number");
            }
        }
        return null;
    }


    @Override
public boolean unassignVehicle(int id) {
    Connection conn = null;
    PreparedStatement selectStmt = null;
    PreparedStatement updateVehicleStmt = null;
    PreparedStatement updateDriverStmt = null;
    ResultSet rs = null;
    boolean success = false;

    try {
        conn = DBConnection.getConnection();
        conn.setAutoCommit(false); 

     
        String selectSql = "SELECT driver_id FROM Vehicles WHERE id = ?";
        selectStmt = conn.prepareStatement(selectSql);
        selectStmt.setInt(1, id);
        rs = selectStmt.executeQuery();
        Integer driverId = null;
        if (rs.next()) {
            driverId = rs.getInt("driver_id");
            if (rs.wasNull()) driverId = null; 
        }

        String updateVehicleSql = "UPDATE Vehicles SET driver_id = NULL, status = 'Unassigned' WHERE id = ?";
        updateVehicleStmt = conn.prepareStatement(updateVehicleSql);
        updateVehicleStmt.setInt(1, id);
        int vehicleUpdateCount = updateVehicleStmt.executeUpdate();
        success = vehicleUpdateCount > 0;

        
        if (success && driverId != null) {
            String updateDriverSql = "UPDATE Drivers SET vehicle_number = NULL WHERE driver_id = ?";
            updateDriverStmt = conn.prepareStatement(updateDriverSql);
            updateDriverStmt.setInt(1, driverId);
            updateDriverStmt.executeUpdate();
        }

        conn.commit();

    } catch (SQLException e) {
 
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Rollback error: " + ex.getMessage());
            }
        }
        System.out.println("Error unassigning vehicle: " + e.getMessage());
        return false;
    } finally {
    
        try {
            if (rs != null) rs.close();
            if (selectStmt != null) selectStmt.close();
            if (updateVehicleStmt != null) updateVehicleStmt.close();
            if (updateDriverStmt != null) updateDriverStmt.close();
            if (conn != null) {
                conn.setAutoCommit(true); 
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }
    return success;
}
 
@Override
public Vehicle getVehicleByPlate(String plateNumber) {
    String sql = "SELECT * FROM Vehicles WHERE LOWER(plate_number) = LOWER(?)";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, plateNumber.trim());
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? mapResultSetToVehicle(rs) : null;
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error fetching vehicle by plate", e);
    }
}


    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(rs.getInt("id"));
        vehicle.setPlateNumber(rs.getString("plate_number"));
        vehicle.setVehicleType(rs.getString("vehicle_type"));
        vehicle.setPassengerCapacity(rs.getInt("passenger_capacity"));
        vehicle.setBrand(rs.getString("brand"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setStatus(rs.getString("status"));
        
        int driverId = rs.getInt("driver_id");
        if (!rs.wasNull()) {
            vehicle.setDriverId(driverId);
        }
        
        return vehicle;
    }
    
    
    
    
}