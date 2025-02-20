package com.megacitycab.config;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
 
    private static final String URL = "jdbc:sqlserver://MOHAMED-VIVOBOO:1433;databaseName=megacitycab;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "rash123";

    
    public static Connection getConnection() {
        Connection conn = null;
        try {
           
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        return conn;
    }

   
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Connected to MS SQL Server successfully!");
            } else {
                System.out.println("Connection failed!");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
