/*
 * =============================================================================
 * MediCare Oman Billing System - Database Connection Manager
 * =============================================================================
 * This class manages JDBC connections to the MedicareDB MySQL database.
 * It provides a singleton-style connection method for database operations.
 * =============================================================================
 */
package mediacareoman_billing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Database Connection Utility Class
 * 
 * This class handles the creation and management of database connections
 * to the MedicareDB MySQL database using JDBC.
 * 
 * @author muhammad-fasih
 */
public class DBConnection {
    
    // ==========================================================================
    // Database Configuration Constants
    // ==========================================================================
    
    /**
     * JDBC URL for connecting to the MedicareDB database
     * Format: jdbc:mysql://[host]:[port]/[database_name]
     */
    private static final String DB_URL = "jdbc:mysql://localhost:3306/MedicareDB";
    
    /**
     * Database username - change this according to your MySQL setup
     */
    private static final String DB_USER = "medicare_admin";
    
    /**
     * Database password - change this according to your MySQL setup
     */
    private static final String DB_PASSWORD = "admin123";
    
    /**
     * MySQL JDBC Driver class name
     */
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // ==========================================================================
    // Connection Management Methods
    // ==========================================================================
    
    /**
     * Establishes and returns a connection to the MedicareDB database.
     * 
     * This method performs the following steps:
     * 1. Loads the MySQL JDBC driver class
     * 2. Creates and returns a new database connection
     * 
     * @return Connection object to the MedicareDB database
     * @throws SQLException if database connection fails
     * @throws ClassNotFoundException if JDBC driver is not found
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // Step 1: Load the MySQL JDBC driver
        // This registers the driver with the DriverManager
        Class.forName(JDBC_DRIVER);
        
        // Step 2: Create and return a new database connection
        // DriverManager uses the URL, username, and password to establish connection
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        
        // Log successful connection (useful for debugging)
        System.out.println("[DBConnection] Successfully connected to MedicareDB");
        
        return connection;
    }
    
    /**
     * Safely closes a database connection.
     * 
     * This method checks if the connection is not null and not already closed
     * before attempting to close it, preventing NullPointerException.
     * 
     * @param connection The Connection object to close
     */
    public static void closeConnection(Connection connection) {
        try {
            // Check if connection exists and is open before closing
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DBConnection] Connection closed successfully");
            }
        } catch (SQLException e) {
            // Log any errors during connection closing
            System.err.println("[DBConnection] Error closing connection: " + e.getMessage());
        }
    }
    
    /**
     * Tests the database connection.
     * 
     * This method can be used to verify that the database configuration
     * is correct and the database is accessible.
     * 
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("[DBConnection] Connection test failed: " + e.getMessage());
            return false;
        } finally {
            closeConnection(conn);
        }
    }
    
    // ==========================================================================
    // Main method for testing database connection independently
    // ==========================================================================
    
    /**
     * Main method to test database connectivity.
     * Run this class directly to verify database setup is correct.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  MediCare Oman - Database Connection Test");
        System.out.println("===========================================");
        
        if (testConnection()) {
            System.out.println("\n[SUCCESS] Database connection is working correctly!");
        } else {
            System.out.println("\n[FAILED] Could not connect to database.");
            System.out.println("Please check:");
            System.out.println("  1. MySQL server is running");
            System.out.println("  2. MedicareDB database exists");
            System.out.println("  3. Username and password are correct");
            System.out.println("  4. MySQL JDBC driver is in classpath");
        }
    }
}
