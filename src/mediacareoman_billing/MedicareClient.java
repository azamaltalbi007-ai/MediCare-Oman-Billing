/*
 * =============================================================================
 * MediCare Oman Billing System - Client Application
 * =============================================================================
 * This class implements the client-side interface for the billing system.
 * It uses Socket programming to communicate with the MedicareServer and
 * provides a user-friendly console interface for bill processing.
 * =============================================================================
 */
package mediacareoman_billing;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * MedicareClient - Console Client for Bill Requests
 * 
 * This client:
 * 1. Connects to the MedicareServer via Socket
 * 2. Accepts user input for billing details
 * 3. Validates all input before sending
 * 4. Sends billing request to server
 * 5. Receives and displays the bill breakdown
 * 
 * @author muhammad-fasih
 */
public class MedicareClient implements Runnable {
    
    // ==========================================================================
    // Client Configuration Constants
    // ==========================================================================
    
    /**
     * Server hostname/IP address
     */
    private static final String SERVER_HOST = "localhost";
    
    /**
     * Server port number (must match MedicareServer)
     */
    private static final int SERVER_PORT = 5000;
    
    // ==========================================================================
    // Main Method - Entry Point
    // ==========================================================================
    
    /**
     * Main method - Entry point for the client application.
     * Creates and starts the client in a new thread.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Create and start client thread
        MedicareClient client = new MedicareClient();
        Thread clientThread = new Thread(client);
        clientThread.start();
    }
    
    // ==========================================================================
    // Client Implementation (Runnable)
    // ==========================================================================
    
    /**
     * Runs the client logic in a separate thread.
     * This method handles the complete client workflow:
     * 1. Display welcome message and menu
     * 2. Accept and validate user inputs
     * 3. Connect to server
     * 4. Send request and receive response
     * 5. Display formatted bill receipt
     */
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        Socket socket = null;
        BufferedReader serverReader = null;
        PrintWriter serverWriter = null;
        
        try {
            // Display welcome banner
            printWelcomeBanner();
            
            // Collect and validate user inputs
            // -----------------------------------------------------------------
            
            // Input 1: Patient ID
            int patientId = getValidPatientId(scanner);
            
            // Input 2: Visit Date
            String visitDate = getValidVisitDate(scanner);
            
            // Input 3: Patient Type
            String patientType = getValidPatientType(scanner);
            
            // Input 4: Service Code
            String serviceCode = getValidServiceCode(scanner);
            
            // Display summary of inputs
            printInputSummary(patientId, visitDate, patientType, serviceCode);
            
            // Confirm before processing
            System.out.print("\nProceed with billing? (Y/N): ");
            String confirm = scanner.nextLine().trim().toUpperCase();
            
            if (!confirm.equals("Y")) {
                System.out.println("\nBilling cancelled by user.");
                return;
            }
            
            // Connect to server
            // -----------------------------------------------------------------
            System.out.println("\nConnecting to server...");
            
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverWriter = new PrintWriter(socket.getOutputStream(), true);
            
            // Read server welcome message
            String welcomeMessage = serverReader.readLine();
            System.out.println("Server: " + welcomeMessage);
            
            // Send billing request
            // -----------------------------------------------------------------
            // Format: patientId|visitDate|patientType|serviceCode
            String request = String.format("%d|%s|%s|%s", 
                    patientId, visitDate, patientType, serviceCode);
            
            System.out.println("Sending billing request...");
            serverWriter.println(request);
            
            // Receive server response
            // -----------------------------------------------------------------
            String response = serverReader.readLine();
            
            // Process and display response
            processServerResponse(response);
            
        } catch (ConnectException e) {
            // Server is not running
            System.err.println("\n[ERROR] Could not connect to server.");
            System.err.println("Please ensure MedicareServer is running on port " + SERVER_PORT);
            
        } catch (IOException e) {
            // Communication error
            System.err.println("\n[ERROR] Communication error: " + e.getMessage());
            
        } finally {
            // Clean up resources
            closeResources(serverReader, serverWriter, socket, scanner);
        }
    }
    
    // ==========================================================================
    // Input Validation Methods
    // ==========================================================================
    
    /**
     * Prompts user for a valid patient ID.
     * Validates that input is a positive integer.
     * 
     * @param scanner Scanner object for user input
     * @return Valid patient ID as integer
     */
    private int getValidPatientId(Scanner scanner) {
        int patientId = -1;
        
        while (patientId <= 0) {
            System.out.print("Enter Patient ID (positive number): ");
            String input = scanner.nextLine().trim();
            
            // Check if input is empty
            if (input.isEmpty()) {
                System.out.println("[ERROR] Patient ID cannot be empty. Please try again.");
                continue;
            }
            
            // Try to parse as integer
            try {
                patientId = Integer.parseInt(input);
                if (patientId <= 0) {
                    System.out.println("[ERROR] Patient ID must be a positive number. Please try again.");
                    patientId = -1;
                }
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid input. Please enter a numeric ID.");
            }
        }
        
        return patientId;
    }
    
    /**
     * Prompts user for a valid visit date.
     * Validates format as YYYY-MM-DD.
     * 
     * @param scanner Scanner object for user input
     * @return Valid date string in YYYY-MM-DD format
     */
    private String getValidVisitDate(Scanner scanner) {
        String visitDate = "";
        
        while (visitDate.isEmpty()) {
            System.out.print("Enter Visit Date (YYYY-MM-DD): ");
            visitDate = scanner.nextLine().trim();
            
            // Check if input is empty
            if (visitDate.isEmpty()) {
                System.out.println("[ERROR] Visit date cannot be empty. Please try again.");
                continue;
            }
            
            // Validate date format using regex
            if (!visitDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                System.out.println("[ERROR] Invalid date format. Use YYYY-MM-DD (e.g., 2024-01-15)");
                visitDate = "";
                continue;
            }
            
            // Additional validation for valid date values
            try {
                String[] parts = visitDate.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);
                
                if (year < 2000 || year > 2100) {
                    System.out.println("[ERROR] Year must be between 2000 and 2100.");
                    visitDate = "";
                } else if (month < 1 || month > 12) {
                    System.out.println("[ERROR] Month must be between 01 and 12.");
                    visitDate = "";
                } else if (day < 1 || day > 31) {
                    System.out.println("[ERROR] Day must be between 01 and 31.");
                    visitDate = "";
                }
            } catch (Exception e) {
                System.out.println("[ERROR] Invalid date values.");
                visitDate = "";
            }
        }
        
        return visitDate;
    }
    
    /**
     * Prompts user for a valid patient type.
     * Displays menu and validates selection.
     * 
     * @param scanner Scanner object for user input
     * @return Valid patient type string
     */
    private String getValidPatientType(Scanner scanner) {
        String patientType = "";
        
        while (patientType.isEmpty()) {
            System.out.println("\nPatient Type Options:");
            System.out.println("  1. Outpatient (0% surcharge)");
            System.out.println("  2. Inpatient  (+5% surcharge)");
            System.out.println("  3. Emergency  (+15% surcharge)");
            System.out.print("Enter choice (1-3): ");
            
            String input = scanner.nextLine().trim();
            
            // Check if input is empty
            if (input.isEmpty()) {
                System.out.println("[ERROR] Please select a patient type.");
                continue;
            }
            
            // Map selection to patient type
            switch (input) {
                case "1":
                    patientType = "Outpatient";
                    break;
                case "2":
                    patientType = "Inpatient";
                    break;
                case "3":
                    patientType = "Emergency";
                    break;
                default:
                    System.out.println("[ERROR] Invalid choice. Enter 1, 2, or 3.");
            }
        }
        
        return patientType;
    }
    
    /**
     * Prompts user for a valid service code.
     * Displays available services and validates selection.
     * 
     * @param scanner Scanner object for user input
     * @return Valid service code string
     */
    private String getValidServiceCode(Scanner scanner) {
        String serviceCode = "";
        
        while (serviceCode.isEmpty()) {
            System.out.println("\nAvailable Medical Services:");
            System.out.println("  CONS100 - Consultation:  12.0 OMR");
            System.out.println("  LAB210  - Lab Test:       8.5 OMR");
            System.out.println("  IMG330  - X-Ray:         25.0 OMR");
            System.out.println("  US400   - Ultrasound:    35.0 OMR");
            System.out.println("  MRI700  - MRI:          180.0 OMR");
            System.out.print("Enter Service Code: ");
            
            serviceCode = scanner.nextLine().trim().toUpperCase();
            
            // Check if input is empty
            if (serviceCode.isEmpty()) {
                System.out.println("[ERROR] Service code cannot be empty.");
                continue;
            }
            
            // Validate service code
            if (!BillingCalculator.isValidServiceCode(serviceCode)) {
                System.out.println("[ERROR] Invalid service code. Please enter a valid code from the list.");
                serviceCode = "";
            }
        }
        
        return serviceCode;
    }
    
    // ==========================================================================
    // Display Methods
    // ==========================================================================
    
    /**
     * Prints the welcome banner for the client application.
     */
    private void printWelcomeBanner() {
        System.out.println("\n===========================================");
        System.out.println("      MEDICARE OMAN - BILLING CLIENT       ");
        System.out.println("===========================================");
        System.out.println("Please enter the billing details below.\n");
    }
    
    /**
     * Prints a summary of the user inputs before processing.
     * 
     * @param patientId Patient ID
     * @param visitDate Visit date
     * @param patientType Patient type
     * @param serviceCode Service code
     */
    private void printInputSummary(int patientId, String visitDate, 
                                   String patientType, String serviceCode) {
        System.out.println("\n-------------------------------------------");
        System.out.println("           BILLING REQUEST SUMMARY");
        System.out.println("-------------------------------------------");
        System.out.println("Patient ID:   " + patientId);
        System.out.println("Visit Date:   " + visitDate);
        System.out.println("Patient Type: " + patientType);
        System.out.println("Service Code: " + serviceCode);
        System.out.println("-------------------------------------------");
    }
    
    /**
     * Processes and displays the server response.
     * Handles both success and error responses.
     * 
     * @param response The response string from server
     */
    private void processServerResponse(String response) {
        if (response == null) {
            System.err.println("\n[ERROR] No response received from server.");
            return;
        }
        
        // Check if response indicates success or error
        if (response.startsWith("SUCCESS:")) {
            // Extract bill data and display formatted receipt
            String billData = response.substring(8); // Remove "SUCCESS:" prefix
            BillingCalculator.BillBreakdown bill = 
                    BillingCalculator.BillBreakdown.fromTransmissionString(billData);
            
            if (bill != null) {
                System.out.println(bill.getFormattedReceipt());
                System.out.println("Bill has been saved to database.");
            } else {
                System.err.println("\n[ERROR] Failed to parse bill data.");
            }
            
        } else if (response.startsWith("ERROR:")) {
            // Display error message from server
            String errorMessage = response.substring(6); // Remove "ERROR:" prefix
            System.err.println("\n[SERVER ERROR] " + errorMessage);
            
        } else {
            // Unknown response format
            System.out.println("\nServer Response: " + response);
        }
    }
    
    // ==========================================================================
    // Resource Cleanup
    // ==========================================================================
    
    /**
     * Safely closes all resources used by the client.
     * 
     * @param reader Server input reader
     * @param writer Server output writer
     * @param socket Server socket connection
     * @param scanner User input scanner
     */
    private void closeResources(BufferedReader reader, PrintWriter writer, 
                               Socket socket, Scanner scanner) {
        // Close server input reader
        if (reader != null) {
            try { reader.close(); } catch (IOException e) { /* ignore */ }
        }
        
        // Close server output writer
        if (writer != null) {
            writer.close();
        }
        
        // Close socket connection
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
                System.out.println("\nConnection closed.");
            } catch (IOException e) {
                System.err.println("[ERROR] Error closing socket: " + e.getMessage());
            }
        }
        
        // Note: Do NOT close the scanner here when reading from System.in
        // Closing it would close System.in and prevent further input in the main launcher
        
        System.out.println("Thank you for using Medicare Oman Billing System!");
    }
}
