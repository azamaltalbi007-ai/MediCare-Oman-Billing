/*
 * =============================================================================
 * MediCare Oman Billing System - Server Application
 * =============================================================================
 * This class implements the server-side logic for the billing system.
 * It uses Socket programming for client-server communication and JDBC for
 * database operations. Each client connection is handled in a separate thread.
 * =============================================================================
 */
package mediacareoman_billing;

import java.io.*;
import java.net.*;
import java.sql.*;

/**
 * MedicareServer - Multi-threaded Server for Bill Processing
 * 
 * This server:
 * 1. Listens on a specified port for client connections
 * 2. Handles each client in a separate thread
 * 3. Receives patient billing requests
 * 4. Fetches insurance type from database
 * 5. Calculates the bill using BillingCalculator
 * 6. Stores the bill in PatientBill table
 * 7. Sends bill breakdown back to client
 * 
 * @author muhammad-fasih
 */
public class MedicareServer implements Runnable {
    
    // ==========================================================================
    // Server Configuration Constants
    // ==========================================================================
    
    /**
     * Port number for server to listen on
     */
    private static final int SERVER_PORT = 5000;
    
    /**
     * Flag to control server running state
     */
    private static volatile boolean isRunning = true;
    
    // ==========================================================================
    // Instance Variables
    // ==========================================================================
    
    /**
     * Client socket for handling individual client connection
     */
    private Socket clientSocket;
    
    /**
     * Client identifier for logging purposes
     */
    private int clientId;
    
    // ==========================================================================
    // Constructor
    // ==========================================================================
    
    /**
     * Constructor for MedicareServer.
     * Called when a new client connects to create a handler thread.
     * 
     * @param clientSocket The socket for client communication
     * @param clientId Unique identifier for the client
     */
    public MedicareServer(Socket clientSocket, int clientId) {
        this.clientSocket = clientSocket;
        this.clientId = clientId;
    }
    
    // ==========================================================================
    // Main Server Loop
    // ==========================================================================
    
    /**
     * Main method - Entry point for the server application.
     * Creates a ServerSocket and accepts incoming client connections.
     * Each client is handled in a separate thread for concurrent processing.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        int clientCounter = 0;
        
        try {
            // Create server socket bound to specified port
            serverSocket = new ServerSocket(SERVER_PORT);
            
            // Print server startup banner
            System.out.println("===========================================");
            System.out.println("    MEDICARE OMAN - BILLING SERVER");
            System.out.println("===========================================");
            System.out.println("Server started on port: " + SERVER_PORT);
            System.out.println("Waiting for client connections...\n");
            
            // Test database connection before accepting clients
            if (!DBConnection.testConnection()) {
                System.err.println("[ERROR] Cannot connect to database. Server shutting down.");
                return;
            }
            System.out.println("[INFO] Database connection verified.\n");
            
            // Main server loop - accept and handle client connections
            while (isRunning) {
                try {
                    // Accept incoming client connection (blocking call)
                    Socket clientSocket = serverSocket.accept();
                    clientCounter++;
                    
                    // Log new client connection
                    System.out.println("[INFO] Client #" + clientCounter + " connected from: " 
                            + clientSocket.getInetAddress().getHostAddress());
                    
                    // Create a new handler thread for this client
                    MedicareServer handler = new MedicareServer(clientSocket, clientCounter);
                    Thread clientThread = new Thread(handler);
                    clientThread.start();
                    
                } catch (SocketException e) {
                    // Server socket was closed, likely during shutdown
                    if (!isRunning) {
                        System.out.println("[INFO] Server is shutting down...");
                    } else {
                        System.err.println("[ERROR] Socket error: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            // Handle server socket creation errors
            System.err.println("[ERROR] Could not start server on port " + SERVER_PORT);
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
            
        } finally {
            // Clean up server resources
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                    System.out.println("[INFO] Server socket closed.");
                }
            } catch (IOException e) {
                System.err.println("[ERROR] Error closing server socket: " + e.getMessage());
            }
        }
    }
    
    // ==========================================================================
    // Client Handler Implementation (Runnable)
    // ==========================================================================
    
    /**
     * Handles client connection in a separate thread.
     * This method:
     * 1. Receives billing request data from client
     * 2. Validates the patient ID in database
     * 3. Fetches insurance type
     * 4. Calculates bill using BillingCalculator
     * 5. Stores bill in database
     * 6. Sends bill breakdown back to client
     */
    @Override
    public void run() {
        BufferedReader inputReader = null;
        PrintWriter outputWriter = null;
        Connection dbConnection = null;
        
        try {
            // Set up input/output streams for client communication
            inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outputWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            
            // Send welcome message to client
            outputWriter.println("CONNECTED:MedicareServer Ready");
            
            // Read client request data
            // Expected format: patientId|visitDate|patientType|serviceCode
            String requestData = inputReader.readLine();
            
            // Log received request
            System.out.println("[Client #" + clientId + "] Received: " + requestData);
            
            // Parse request data
            String[] parts = requestData.split("\\|");
            
            // Validate request format
            if (parts.length != 4) {
                outputWriter.println("ERROR:Invalid request format. Expected: patientId|visitDate|patientType|serviceCode");
                System.out.println("[Client #" + clientId + "] Error: Invalid request format");
                return;
            }
            
            // Extract request components
            int patientId;
            try {
                patientId = Integer.parseInt(parts[0].trim());
            } catch (NumberFormatException e) {
                outputWriter.println("ERROR:Invalid patient ID. Must be a number.");
                return;
            }
            
            String visitDate = parts[1].trim();
            String patientType = parts[2].trim();
            String serviceCode = parts[3].trim().toUpperCase();
            
            // Validate service code
            if (!BillingCalculator.isValidServiceCode(serviceCode)) {
                outputWriter.println("ERROR:Invalid service code. Valid codes: CONS100, LAB210, IMG330, US400, MRI700");
                return;
            }
            
            // Validate patient type
            if (!BillingCalculator.isValidPatientType(patientType)) {
                outputWriter.println("ERROR:Invalid patient type. Valid types: Outpatient, Inpatient, Emergency");
                return;
            }
            
            // Connect to database
            dbConnection = DBConnection.getConnection();
            
            // Step 1: Fetch patient insurance type from database
            String insuranceType = fetchInsuranceType(dbConnection, patientId);
            
            if (insuranceType == null) {
                outputWriter.println("ERROR:Patient ID not found in database.");
                System.out.println("[Client #" + clientId + "] Error: Patient ID " + patientId + " not found");
                return;
            }
            
            System.out.println("[Client #" + clientId + "] Patient insurance type: " + insuranceType);
            
            // Step 2: Calculate the bill using BillingCalculator
            BillingCalculator.BillBreakdown bill = BillingCalculator.calculateBill(
                serviceCode, insuranceType, patientType);
            
            if (bill == null) {
                outputWriter.println("ERROR:Failed to calculate bill.");
                return;
            }
            
            // Step 3: Insert bill record into PatientBill table
            boolean inserted = insertBillRecord(dbConnection, patientId, visitDate, bill.finalAmount);
            
            if (!inserted) {
                outputWriter.println("ERROR:Failed to save bill to database.");
                return;
            }
            
            System.out.println("[Client #" + clientId + "] Bill saved. Amount: " + bill.finalAmount + " OMR");
            
            // Step 4: Send bill breakdown back to client
            outputWriter.println("SUCCESS:" + bill.toTransmissionString());
            
            System.out.println("[Client #" + clientId + "] Response sent successfully.\n");
            
        } catch (IOException e) {
            // Handle communication errors
            System.err.println("[Client #" + clientId + "] IO Error: " + e.getMessage());
            
        } catch (SQLException | ClassNotFoundException e) {
            // Handle database errors
            System.err.println("[Client #" + clientId + "] Database Error: " + e.getMessage());
            try {
                if (outputWriter != null) {
                    outputWriter.println("ERROR:Database connection failed.");
                }
            } catch (Exception ex) {
                // Ignore output errors during error handling
            }
            
        } finally {
            // Clean up resources - close streams and connections
            closeResources(inputReader, outputWriter, dbConnection, clientSocket);
        }
    }
    
    // ==========================================================================
    // Database Operations
    // ==========================================================================
    
    /**
     * Fetches the insurance plan type for a patient from the database.
     * 
     * @param connection Active database connection
     * @param patientId The patient's ID
     * @return Insurance plan type string, or null if patient not found
     * @throws SQLException if database query fails
     */
    private String fetchInsuranceType(Connection connection, int patientId) throws SQLException {
        // SQL query to fetch insurance type by patient ID
        String sql = "SELECT insurance_plan_type FROM Patient WHERE id = ?";
        
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            // Prepare and execute the query
            statement = connection.prepareStatement(sql);
            statement.setInt(1, patientId);
            resultSet = statement.executeQuery();
            
            // Check if patient exists and return insurance type
            if (resultSet.next()) {
                return resultSet.getString("insurance_plan_type");
            }
            
            // Patient not found
            return null;
            
        } finally {
            // Close result set and statement
            if (resultSet != null) {
                try { resultSet.close(); } catch (SQLException e) { /* ignore */ }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }
    
    /**
     * Inserts a bill record into the PatientBill table.
     * 
     * @param connection Active database connection
     * @param patientId The patient's ID
     * @param visitDate The visit date in YYYY-MM-DD format
     * @param billAmount The calculated bill amount
     * @return true if insert was successful, false otherwise
     * @throws SQLException if database insert fails
     */
    private boolean insertBillRecord(Connection connection, int patientId, 
                                     String visitDate, double billAmount) throws SQLException {
        // SQL query to insert bill record
        String sql = "INSERT INTO PatientBill (patient_id, visit_date, bill_amount) VALUES (?, ?, ?)";
        
        PreparedStatement statement = null;
        
        try {
            // Prepare the insert statement
            statement = connection.prepareStatement(sql);
            statement.setInt(1, patientId);
            statement.setString(2, visitDate);
            statement.setDouble(3, billAmount);
            
            // Execute insert and check if successful
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
            
        } finally {
            // Close statement
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }
    
    // ==========================================================================
    // Resource Cleanup
    // ==========================================================================
    
    /**
     * Safely closes all resources used by the client handler.
     * 
     * @param reader Input stream reader
     * @param writer Output stream writer
     * @param connection Database connection
     * @param socket Client socket
     */
    private void closeResources(BufferedReader reader, PrintWriter writer, 
                               Connection connection, Socket socket) {
        // Close input reader
        if (reader != null) {
            try { reader.close(); } catch (IOException e) { /* ignore */ }
        }
        
        // Close output writer
        if (writer != null) {
            writer.close();
        }
        
        // Close database connection
        DBConnection.closeConnection(connection);
        
        // Close client socket
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
                System.out.println("[Client #" + clientId + "] Connection closed.");
            } catch (IOException e) {
                System.err.println("[Client #" + clientId + "] Error closing socket: " + e.getMessage());
            }
        }
    }
    
    /**
     * Shuts down the server gracefully.
     * Sets the running flag to false to stop accepting new connections.
     */
    public static void shutdown() {
        isRunning = false;
        System.out.println("[INFO] Server shutdown initiated...");
    }
}
