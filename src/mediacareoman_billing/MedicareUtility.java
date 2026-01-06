/*
 * =============================================================================
 * MediCare Oman Billing System - Utility Class with Collections
 * =============================================================================
 * This class demonstrates Java Collections usage with a menu-driven CLI program.
 * It manages a list of Patient objects using ArrayList and provides CRUD operations.
 * =============================================================================
 */
package mediacareoman_billing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * MedicareUtility - Menu-driven Utility Program using Java Collections
 * 
 * This utility class demonstrates:
 * 1. ArrayList usage for storing custom Patient objects
 * 2. Menu-driven console interface
 * 3. CRUD operations (Create, Read, Update, Delete)
 * 4. Iterator pattern for traversing collections
 * 
 * @author muhammad-fasih
 */
public class MedicareUtility {
    
    // ==========================================================================
    // Instance Variables
    // ==========================================================================
    
    /**
     * ArrayList to store Patient records
     * Demonstrates use of Java Collections with custom objects
     */
    private ArrayList<PatientRecord> patientRecords;
    
    /**
     * Scanner for reading user input
     */
    private Scanner scanner;
    
    // ==========================================================================
    // Inner Class: PatientRecord
    // ==========================================================================
    
    /**
     * Inner class representing a Patient Record.
     * This is the custom object stored in the ArrayList.
     */
    public static class PatientRecord {
        // Patient attributes
        private int id;
        private String name;
        private int age;
        private String insurancePlanType;
        private String contactNumber;
        
        /**
         * Constructor for PatientRecord.
         * 
         * @param id Patient ID
         * @param name Patient name
         * @param age Patient age
         * @param insurancePlanType Insurance plan (Premium/Standard/Basic)
         * @param contactNumber Contact number
         */
        public PatientRecord(int id, String name, int age, 
                            String insurancePlanType, String contactNumber) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.insurancePlanType = insurancePlanType;
            this.contactNumber = contactNumber;
        }
        
        // Getter methods
        public int getId() { return id; }
        public String getName() { return name; }
        public int getAge() { return age; }
        public String getInsurancePlanType() { return insurancePlanType; }
        public String getContactNumber() { return contactNumber; }
        
        // Setter methods
        public void setName(String name) { this.name = name; }
        public void setAge(int age) { this.age = age; }
        public void setInsurancePlanType(String insurancePlanType) { 
            this.insurancePlanType = insurancePlanType; 
        }
        public void setContactNumber(String contactNumber) { 
            this.contactNumber = contactNumber; 
        }
        
        /**
         * Returns a formatted string representation of the patient record.
         * 
         * @return Formatted string with all patient details
         */
        @Override
        public String toString() {
            return String.format("| %-5d | %-20s | %-5d | %-10s | %-15s |",
                    id, name, age, insurancePlanType, contactNumber);
        }
        
        /**
         * Returns a header string for the table display.
         * 
         * @return Formatted header string
         */
        public static String getHeader() {
            return String.format("| %-5s | %-20s | %-5s | %-10s | %-15s |",
                    "ID", "Name", "Age", "Insurance", "Contact");
        }
        
        /**
         * Returns a separator line for the table display.
         * 
         * @return Separator line string
         */
        public static String getSeparator() {
            return "+-------+----------------------+-------+------------+-----------------+";
        }
    }
    
    // ==========================================================================
    // Constructor
    // ==========================================================================
    
    /**
     * Constructor for MedicareUtility.
     * Initializes the ArrayList and Scanner, and adds some sample data.
     */
    public MedicareUtility() {
        // Initialize the ArrayList to store patient records
        patientRecords = new ArrayList<>();
        
        // Initialize scanner for user input
        scanner = new Scanner(System.in);
        
        // Add some sample patient records for demonstration
        addSampleData();
    }
    
    /**
     * Adds sample patient records to the ArrayList.
     * This provides initial data for testing the utility functions.
     */
    private void addSampleData() {
        patientRecords.add(new PatientRecord(1, "Ahmed Al-Rashid", 45, "Premium", "96871234567"));
        patientRecords.add(new PatientRecord(2, "Fatima Al-Balushi", 32, "Standard", "96879876543"));
        patientRecords.add(new PatientRecord(3, "Mohammed Al-Habsi", 28, "Basic", "96871112222"));
    }
    
    // ==========================================================================
    // Main Method - Entry Point
    // ==========================================================================
    
    /**
     * Main method - Entry point for the utility program.
     * Creates an instance and runs the menu-driven interface.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        MedicareUtility utility = new MedicareUtility();
        utility.runMenu();
    }
    
    // ==========================================================================
    // Menu-Driven Interface
    // ==========================================================================
    
    /**
     * Runs the main menu loop.
     * Displays options and processes user selections until exit.
     */
    public void runMenu() {
        boolean running = true;
        
        // Display welcome banner
        printWelcomeBanner();
        
        // Main menu loop
        while (running) {
            // Display menu options
            printMenu();
            
            // Get user choice
            System.out.print("Enter your choice (1-5): ");
            String input = scanner.nextLine().trim();
            
            // Process user selection
            switch (input) {
                case "1":
                    // Option 1: Add new element
                    addElement();
                    break;
                    
                case "2":
                    // Option 2: Display all elements
                    displayElements();
                    break;
                    
                case "3":
                    // Option 3: Remove an element
                    removeElement();
                    break;
                    
                case "4":
                    // Option 4: Iterate/List all elements
                    iterateElements();
                    break;
                    
                case "5":
                    // Option 5: Exit the program
                    running = false;
                    System.out.println("\nExiting Medicare Utility. Goodbye!");
                    break;
                    
                default:
                    // Invalid option
                    System.out.println("\n[ERROR] Invalid choice. Please enter 1-5.");
            }
        }
        
        // Close scanner
        scanner.close();
    }
    
    /**
     * Prints the welcome banner for the utility program.
     */
    private void printWelcomeBanner() {
        System.out.println("\n===========================================");
        System.out.println("    MEDICARE OMAN - UTILITY PROGRAM        ");
        System.out.println("    (Java Collections Demonstration)       ");
        System.out.println("===========================================");
    }
    
    /**
     * Prints the menu options.
     */
    private void printMenu() {
        System.out.println("\n-------------------------------------------");
        System.out.println("              MAIN MENU                    ");
        System.out.println("-------------------------------------------");
        System.out.println("  1. Add Element (Add new patient record)");
        System.out.println("  2. Display Elements (Show all records)");
        System.out.println("  3. Remove Element (Delete a record)");
        System.out.println("  4. Iterate/List All (Using Iterator)");
        System.out.println("  5. Exit");
        System.out.println("-------------------------------------------");
    }
    
    // ==========================================================================
    // CRUD Operations
    // ==========================================================================
    
    /**
     * Option 1: Add a new patient record to the ArrayList.
     * 
     * This method demonstrates:
     * - Creating a new custom object
     * - Adding the object to an ArrayList
     * - Input validation
     */
    private void addElement() {
        System.out.println("\n--- ADD NEW PATIENT RECORD ---");
        
        try {
            // Get patient ID
            System.out.print("Enter Patient ID: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            
            // Check if ID already exists
            for (PatientRecord record : patientRecords) {
                if (record.getId() == id) {
                    System.out.println("[ERROR] Patient ID already exists. Please use a unique ID.");
                    return;
                }
            }
            
            // Get patient name
            System.out.print("Enter Patient Name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("[ERROR] Name cannot be empty.");
                return;
            }
            
            // Get patient age
            System.out.print("Enter Patient Age: ");
            int age = Integer.parseInt(scanner.nextLine().trim());
            if (age <= 0 || age > 150) {
                System.out.println("[ERROR] Invalid age.");
                return;
            }
            
            // Get insurance plan type
            System.out.println("Insurance Plan Types: 1. Premium  2. Standard  3. Basic");
            System.out.print("Enter choice (1-3): ");
            String insuranceChoice = scanner.nextLine().trim();
            String insuranceType;
            switch (insuranceChoice) {
                case "1": insuranceType = "Premium"; break;
                case "2": insuranceType = "Standard"; break;
                case "3": insuranceType = "Basic"; break;
                default:
                    System.out.println("[ERROR] Invalid insurance choice.");
                    return;
            }
            
            // Get contact number
            System.out.print("Enter Contact Number: ");
            String contactNumber = scanner.nextLine().trim();
            
            // Create new PatientRecord object and add to ArrayList
            PatientRecord newRecord = new PatientRecord(id, name, age, insuranceType, contactNumber);
            patientRecords.add(newRecord);
            
            // Confirm addition
            System.out.println("\n[SUCCESS] Patient record added successfully!");
            System.out.println("Total records in collection: " + patientRecords.size());
            
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid numeric input. Please enter valid numbers.");
        }
    }
    
    /**
     * Option 2: Display all patient records in the ArrayList.
     * 
     * This method demonstrates:
     * - Checking if ArrayList is empty
     * - Using enhanced for-loop to traverse ArrayList
     * - Formatting output in a table format
     */
    private void displayElements() {
        System.out.println("\n--- DISPLAY ALL PATIENT RECORDS ---");
        
        // Check if ArrayList is empty
        if (patientRecords.isEmpty()) {
            System.out.println("No patient records found. The collection is empty.");
            return;
        }
        
        // Display total count
        System.out.println("Total records: " + patientRecords.size());
        System.out.println();
        
        // Print table header
        System.out.println(PatientRecord.getSeparator());
        System.out.println(PatientRecord.getHeader());
        System.out.println(PatientRecord.getSeparator());
        
        // Use enhanced for-loop to display all records
        for (PatientRecord record : patientRecords) {
            System.out.println(record.toString());
        }
        
        // Print table footer
        System.out.println(PatientRecord.getSeparator());
    }
    
    /**
     * Option 3: Remove a patient record from the ArrayList.
     * 
     * This method demonstrates:
     * - Searching for an element by ID
     * - Removing an element from ArrayList
     * - Using ArrayList's remove() method
     */
    private void removeElement() {
        System.out.println("\n--- REMOVE PATIENT RECORD ---");
        
        // Check if ArrayList is empty
        if (patientRecords.isEmpty()) {
            System.out.println("No patient records to remove. The collection is empty.");
            return;
        }
        
        // Display current records first
        displayElements();
        
        try {
            // Get ID to remove
            System.out.print("\nEnter Patient ID to remove: ");
            int idToRemove = Integer.parseInt(scanner.nextLine().trim());
            
            // Search for the record
            PatientRecord recordToRemove = null;
            for (PatientRecord record : patientRecords) {
                if (record.getId() == idToRemove) {
                    recordToRemove = record;
                    break;
                }
            }
            
            // Remove if found
            if (recordToRemove != null) {
                // Confirm deletion
                System.out.print("Are you sure you want to remove '" + 
                        recordToRemove.getName() + "'? (Y/N): ");
                String confirm = scanner.nextLine().trim().toUpperCase();
                
                if (confirm.equals("Y")) {
                    // Remove the record from ArrayList
                    patientRecords.remove(recordToRemove);
                    System.out.println("\n[SUCCESS] Patient record removed successfully!");
                    System.out.println("Remaining records: " + patientRecords.size());
                } else {
                    System.out.println("Removal cancelled.");
                }
            } else {
                System.out.println("[ERROR] Patient ID not found in the collection.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid ID format. Please enter a numeric ID.");
        }
    }
    
    /**
     * Option 4: Iterate through all elements using Iterator.
     * 
     * This method demonstrates:
     * - Using Iterator to traverse ArrayList
     * - Iterator's hasNext() and next() methods
     * - Alternative iteration approach to enhanced for-loop
     */
    private void iterateElements() {
        System.out.println("\n--- ITERATE USING ITERATOR PATTERN ---");
        
        // Check if ArrayList is empty
        if (patientRecords.isEmpty()) {
            System.out.println("No patient records to iterate. The collection is empty.");
            return;
        }
        
        System.out.println("Demonstrating Iterator pattern to traverse ArrayList:\n");
        
        // Create an Iterator for the ArrayList
        Iterator<PatientRecord> iterator = patientRecords.iterator();
        
        // Counter for display
        int count = 1;
        
        // Print header
        System.out.println("Iterating through " + patientRecords.size() + " patient records:");
        System.out.println(PatientRecord.getSeparator());
        
        // Use Iterator's hasNext() to check for more elements
        // Use Iterator's next() to get the next element
        while (iterator.hasNext()) {
            // Get next element
            PatientRecord record = iterator.next();
            
            // Display with iteration number
            System.out.println("Record #" + count + ":");
            System.out.println("  ID:            " + record.getId());
            System.out.println("  Name:          " + record.getName());
            System.out.println("  Age:           " + record.getAge());
            System.out.println("  Insurance:     " + record.getInsurancePlanType());
            System.out.println("  Contact:       " + record.getContactNumber());
            System.out.println(PatientRecord.getSeparator());
            
            count++;
        }
        
        // Summary
        System.out.println("\nIteration complete. Total records processed: " + (count - 1));
        
        // Additional demonstration: ArrayList methods
        System.out.println("\n--- ADDITIONAL ARRAYLIST INFORMATION ---");
        System.out.println("ArrayList size():     " + patientRecords.size());
        System.out.println("ArrayList isEmpty():  " + patientRecords.isEmpty());
        
        if (!patientRecords.isEmpty()) {
            System.out.println("First element (get(0)): ID=" + patientRecords.get(0).getId() + 
                    ", Name=" + patientRecords.get(0).getName());
            System.out.println("Last element (get(size-1)): ID=" + 
                    patientRecords.get(patientRecords.size() - 1).getId() + 
                    ", Name=" + patientRecords.get(patientRecords.size() - 1).getName());
        }
    }
    
    // ==========================================================================
    // Additional Utility Methods
    // ==========================================================================
    
    /**
     * Gets the current size of the patient records collection.
     * 
     * @return Number of patient records in the ArrayList
     */
    public int getCollectionSize() {
        return patientRecords.size();
    }
    
    /**
     * Checks if the patient records collection is empty.
     * 
     * @return true if empty, false otherwise
     */
    public boolean isCollectionEmpty() {
        return patientRecords.isEmpty();
    }
    
    /**
     * Clears all patient records from the collection.
     */
    public void clearCollection() {
        patientRecords.clear();
        System.out.println("[INFO] All patient records have been cleared.");
    }
}
