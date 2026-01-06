/*
 * =============================================================================
 * MediCare Oman Billing System - Main Launcher
 * =============================================================================
 * This is the main entry point for the Medicare Oman Billing System.
 * It provides a menu-driven interface to launch different components:
 * 1. Server - For processing billing requests
 * 2. Client - For submitting billing requests
 * 3. Utility - For managing patient records (Collections demo)
 * 4. Database Test - For verifying database connectivity
 * =============================================================================
 */
package mediacareoman_billing;

import java.util.Scanner;

/**
 * MediacareOman_Billing - Main Launcher Class
 * 
 * This class serves as the central entry point for the billing system.
 * It provides users with options to run different modules of the application.
 * 
 * How to Use:
 * 1. First run the SQL script (MedicareDB.sql) to set up the database
 * 2. Run this main class to see the launcher menu
 * 3. Start the Server first (Option 1)
 * 4. In a separate terminal/instance, run the Client (Option 2)
 * 
 * Alternatively, you can run each component directly:
 * - MedicareServer.java - Run to start the server
 * - MedicareClient.java - Run to start a client instance
 * - MedicareUtility.java - Run to use the collections utility
 * - DBConnection.java - Run to test database connectivity
 * 
 * @author muhammad-fasih
 */
public class MediacareOman_Billing {

    /**
     * Main method - Entry point for the application.
     * Displays a launcher menu for all system components.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        // Display application banner
        printBanner();
        
        // Main menu loop
        while (running) {
            // Display menu options
            printMenu();
            
            // Get user choice
            System.out.print("Enter your choice (1-5): ");
            String choice = scanner.nextLine().trim();
            
            // Process selection
            switch (choice) {
                case "1":
                    // Start the Server
                    System.out.println("\n[INFO] Starting Medicare Server...");
                    System.out.println("[INFO] Server will run in this console.");
                    System.out.println("[INFO] Press Ctrl+C to stop the server.\n");
                    MedicareServer.main(args);
                    break;
                    
                case "2":
                    // Start the Client
                    System.out.println("\n[INFO] Starting Medicare Client...");
                    MedicareClient client = new MedicareClient();
                    Thread clientThread = new Thread(client);
                    clientThread.start();
                    try {
                        clientThread.join(); // Wait for client to complete
                    } catch (InterruptedException e) {
                        System.err.println("Client interrupted: " + e.getMessage());
                    }
                    break;
                    
                case "3":
                    // Start the Utility Program
                    System.out.println("\n[INFO] Starting Medicare Utility...\n");
                    MedicareUtility utility = new MedicareUtility();
                    utility.runMenu();
                    break;
                    
                case "4":
                    // Test Database Connection
                    System.out.println("\n[INFO] Testing database connection...\n");
                    DBConnection.main(args);
                    break;
                    
                case "5":
                    // Exit
                    running = false;
                    System.out.println("\nThank you for using Medicare Oman Billing System!");
                    System.out.println("Goodbye!");
                    break;
                    
                default:
                    System.out.println("\n[ERROR] Invalid choice. Please enter 1-5.");
            }
        }
        
        scanner.close();
    }
    
    /**
     * Prints the application banner.
     */
    private static void printBanner() {
        System.out.println("\n=====================================================");
        System.out.println("                                                     ");
        System.out.println("   __  __          _ _  ____                         ");
        System.out.println("  |  \\/  | ___  __| (_)/ ___|__ _ _ __ ___           ");
        System.out.println("  | |\\/| |/ _ \\/ _` | | |   / _` | '__/ _ \\         ");
        System.out.println("  | |  | |  __/ (_| | | |__| (_| | | |  __/          ");
        System.out.println("  |_|  |_|\\___|\\__,_|_|\\____\\__,_|_|  \\___|         ");
        System.out.println("                                                     ");
        System.out.println("         O M A N   B I L L I N G   S Y S T E M       ");
        System.out.println("                                                     ");
        System.out.println("=====================================================");
        System.out.println("  Version: 1.0  |  JDBC + Sockets + Multithreading   ");
        System.out.println("=====================================================");
    }
    
    /**
     * Prints the main menu options.
     */
    private static void printMenu() {
        System.out.println("\n-----------------------------------------------------");
        System.out.println("                   MAIN LAUNCHER                     ");
        System.out.println("-----------------------------------------------------");
        System.out.println("  1. Start Server   (MedicareServer)");
        System.out.println("  2. Start Client   (MedicareClient)");
        System.out.println("  3. Run Utility    (Collections Demo)");
        System.out.println("  4. Test Database  (JDBC Connection Test)");
        System.out.println("  5. Exit");
        System.out.println("-----------------------------------------------------");
    }
}
