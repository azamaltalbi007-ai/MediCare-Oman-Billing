/*
 * =============================================================================
 * MediCare Oman Billing System - Billing Calculator
 * =============================================================================
 * This class implements all business logic for calculating patient bills
 * including base service fees, insurance discounts, and patient type surcharges.
 * =============================================================================
 */
package mediacareoman_billing;

import java.util.HashMap;
import java.util.Map;

/**
 * BillingCalculator - Business Logic Handler for Bill Calculations
 * 
 * This class encapsulates all billing calculation rules including:
 * - Base service fees for different medical services
 * - Insurance plan discounts (Premium, Standard, Basic)
 * - Patient type surcharges (Outpatient, Inpatient, Emergency)
 * 
 * Calculation Order:
 * 1. Get base fee from service code
 * 2. Apply insurance percentage discount
 * 3. Apply additional fixed discount based on insurance plan
 * 4. Apply patient type surcharge on the discounted amount
 * 
 * @author muhammad-fasih
 */
public class BillingCalculator {
    
    // ==========================================================================
    // Service Fee Constants (in OMR - Omani Rial)
    // ==========================================================================
    
    /**
     * Map storing base service fees for each service code
     * Key: Service Code, Value: Base Fee in OMR
     */
    private static final Map<String, Double> SERVICE_FEES = new HashMap<>();
    
    // Static initializer block to populate service fees
    static {
        SERVICE_FEES.put("CONS100", 12.0);   // Consultation fee
        SERVICE_FEES.put("LAB210", 8.5);     // Lab Test fee
        SERVICE_FEES.put("IMG330", 25.0);    // X-Ray fee
        SERVICE_FEES.put("US400", 35.0);     // Ultrasound fee
        SERVICE_FEES.put("MRI700", 180.0);   // MRI fee
    }
    
    // ==========================================================================
    // Insurance Discount Constants
    // ==========================================================================
    
    // Percentage discounts for each insurance plan type
    private static final double PREMIUM_DISCOUNT_PERCENT = 0.15;   // 15% discount
    private static final double STANDARD_DISCOUNT_PERCENT = 0.10;  // 10% discount
    private static final double BASIC_DISCOUNT_PERCENT = 0.0;      // 0% discount
    
    // Additional fixed discounts per visit (in OMR)
    private static final double PREMIUM_FIXED_DISCOUNT = 5.0;      // 5.0 OMR off
    private static final double STANDARD_FIXED_DISCOUNT = 8.0;     // 8.0 OMR off
    private static final double BASIC_FIXED_DISCOUNT = 10.0;       // 10.0 OMR off
    
    // ==========================================================================
    // Patient Type Surcharge Constants
    // ==========================================================================
    
    private static final double OUTPATIENT_SURCHARGE = 0.0;   // 0% extra
    private static final double INPATIENT_SURCHARGE = 0.05;   // +5% extra
    private static final double EMERGENCY_SURCHARGE = 0.15;   // +15% extra
    
    // ==========================================================================
    // Bill Calculation Methods
    // ==========================================================================
    
    /**
     * Retrieves the base fee for a given service code.
     * 
     * @param serviceCode The service code (e.g., "CONS100", "LAB210")
     * @return The base fee in OMR, or -1 if service code is invalid
     */
    public static double getBaseFee(String serviceCode) {
        // Convert to uppercase to handle case-insensitive input
        String code = serviceCode.toUpperCase().trim();
        
        // Return the fee if service exists, otherwise return -1 to indicate error
        if (SERVICE_FEES.containsKey(code)) {
            return SERVICE_FEES.get(code);
        }
        return -1; // Invalid service code
    }
    
    /**
     * Calculates the insurance discount amount and fixed discount.
     * 
     * Insurance Discount Rules:
     * - Premium: 15% off base + 5.0 OMR additional discount
     * - Standard: 10% off base + 8.0 OMR additional discount
     * - Basic: 0% off base + 10.0 OMR additional discount
     * 
     * @param baseFee The base service fee
     * @param insurancePlanType The insurance plan type (Premium/Standard/Basic)
     * @return Array containing [percentage_discount, fixed_discount, total_discount]
     */
    public static double[] calculateInsuranceDiscount(double baseFee, String insurancePlanType) {
        double percentageDiscount = 0.0;
        double fixedDiscount = 0.0;
        
        // Determine discount based on insurance plan type
        switch (insurancePlanType.trim()) {
            case "Premium":
                // Premium: 15% discount + 5.0 OMR fixed
                percentageDiscount = baseFee * PREMIUM_DISCOUNT_PERCENT;
                fixedDiscount = PREMIUM_FIXED_DISCOUNT;
                break;
                
            case "Standard":
                // Standard: 10% discount + 8.0 OMR fixed
                percentageDiscount = baseFee * STANDARD_DISCOUNT_PERCENT;
                fixedDiscount = STANDARD_FIXED_DISCOUNT;
                break;
                
            case "Basic":
                // Basic: 0% discount + 10.0 OMR fixed
                percentageDiscount = baseFee * BASIC_DISCOUNT_PERCENT;
                fixedDiscount = BASIC_FIXED_DISCOUNT;
                break;
                
            default:
                // Unknown insurance type - no discount applied
                System.err.println("[BillingCalculator] Unknown insurance type: " + insurancePlanType);
                break;
        }
        
        // Calculate total discount
        double totalDiscount = percentageDiscount + fixedDiscount;
        
        // Return all discount components
        return new double[]{percentageDiscount, fixedDiscount, totalDiscount};
    }
    
    /**
     * Calculates the patient type surcharge amount.
     * 
     * Surcharge Rules (applied on discounted amount):
     * - Outpatient: 0% extra
     * - Inpatient: +5% extra
     * - Emergency: +15% extra
     * 
     * @param discountedAmount The amount after insurance discounts
     * @param patientType The patient type (Outpatient/Inpatient/Emergency)
     * @return The surcharge amount in OMR
     */
    public static double calculateSurcharge(double discountedAmount, String patientType) {
        double surchargeRate = 0.0;
        
        // Determine surcharge rate based on patient type
        switch (patientType.trim()) {
            case "Outpatient":
                surchargeRate = OUTPATIENT_SURCHARGE;  // 0%
                break;
                
            case "Inpatient":
                surchargeRate = INPATIENT_SURCHARGE;   // 5%
                break;
                
            case "Emergency":
                surchargeRate = EMERGENCY_SURCHARGE;   // 15%
                break;
                
            default:
                // Unknown patient type - no surcharge
                System.err.println("[BillingCalculator] Unknown patient type: " + patientType);
                break;
        }
        
        // Calculate and return the surcharge amount
        return discountedAmount * surchargeRate;
    }
    
    /**
     * Calculates the complete bill with all components.
     * 
     * Calculation Steps:
     * 1. Get base fee from service code
     * 2. Apply insurance percentage discount
     * 3. Apply additional fixed discount based on insurance plan
     * 4. Calculate subtotal after discounts
     * 5. Apply patient type surcharge on discounted amount
     * 6. Calculate final amount
     * 
     * @param serviceCode The service code (e.g., "CONS100")
     * @param insurancePlanType The insurance plan type
     * @param patientType The patient type
     * @return BillBreakdown object containing all calculation details
     */
    public static BillBreakdown calculateBill(String serviceCode, String insurancePlanType, String patientType) {
        // Step 1: Get the base fee
        double baseFee = getBaseFee(serviceCode);
        
        // Validate service code
        if (baseFee < 0) {
            return null; // Invalid service code
        }
        
        // Step 2 & 3: Calculate insurance discounts
        double[] discounts = calculateInsuranceDiscount(baseFee, insurancePlanType);
        double percentageDiscount = discounts[0];
        double fixedDiscount = discounts[1];
        double totalDiscount = discounts[2];
        
        // Step 4: Calculate subtotal after discounts
        // Ensure discounted amount doesn't go below 0
        double discountedAmount = Math.max(0, baseFee - totalDiscount);
        
        // Step 5: Calculate patient type surcharge
        double surcharge = calculateSurcharge(discountedAmount, patientType);
        
        // Step 6: Calculate final amount
        double finalAmount = discountedAmount + surcharge;
        
        // Create and return the bill breakdown
        return new BillBreakdown(
            serviceCode,
            baseFee,
            insurancePlanType,
            percentageDiscount,
            fixedDiscount,
            totalDiscount,
            patientType,
            surcharge,
            finalAmount
        );
    }
    
    /**
     * Validates if a service code is valid.
     * 
     * @param serviceCode The service code to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidServiceCode(String serviceCode) {
        return SERVICE_FEES.containsKey(serviceCode.toUpperCase().trim());
    }
    
    /**
     * Validates if an insurance plan type is valid.
     * 
     * @param insuranceType The insurance type to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidInsuranceType(String insuranceType) {
        String type = insuranceType.trim();
        return type.equals("Premium") || type.equals("Standard") || type.equals("Basic");
    }
    
    /**
     * Validates if a patient type is valid.
     * 
     * @param patientType The patient type to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPatientType(String patientType) {
        String type = patientType.trim();
        return type.equals("Outpatient") || type.equals("Inpatient") || type.equals("Emergency");
    }
    
    /**
     * Gets a formatted string of all available service codes and fees.
     * 
     * @return Formatted string listing all services
     */
    public static String getAvailableServices() {
        StringBuilder sb = new StringBuilder();
        sb.append("Available Service Codes:\n");
        sb.append("  CONS100 - Consultation: 12.0 OMR\n");
        sb.append("  LAB210  - Lab Test:     8.5 OMR\n");
        sb.append("  IMG330  - X-Ray:        25.0 OMR\n");
        sb.append("  US400   - Ultrasound:   35.0 OMR\n");
        sb.append("  MRI700  - MRI:          180.0 OMR\n");
        return sb.toString();
    }
    
    // ==========================================================================
    // Inner Class: BillBreakdown
    // ==========================================================================
    
    /**
     * Inner class to hold all components of a calculated bill.
     * This provides a structured way to return and display bill details.
     */
    public static class BillBreakdown {
        // Bill components
        public final String serviceCode;
        public final double baseFee;
        public final String insuranceType;
        public final double percentageDiscount;
        public final double fixedDiscount;
        public final double totalDiscount;
        public final String patientType;
        public final double surcharge;
        public final double finalAmount;
        
        /**
         * Constructor for BillBreakdown.
         */
        public BillBreakdown(String serviceCode, double baseFee, String insuranceType,
                            double percentageDiscount, double fixedDiscount, double totalDiscount,
                            String patientType, double surcharge, double finalAmount) {
            this.serviceCode = serviceCode;
            this.baseFee = baseFee;
            this.insuranceType = insuranceType;
            this.percentageDiscount = percentageDiscount;
            this.fixedDiscount = fixedDiscount;
            this.totalDiscount = totalDiscount;
            this.patientType = patientType;
            this.surcharge = surcharge;
            this.finalAmount = finalAmount;
        }
        
        /**
         * Generates a formatted bill receipt string.
         * 
         * @return Formatted bill receipt
         */
        public String getFormattedReceipt() {
            StringBuilder receipt = new StringBuilder();
            receipt.append("\n============================================\n");
            receipt.append("       MEDICARE OMAN - BILL RECEIPT         \n");
            receipt.append("============================================\n");
            receipt.append(String.format("Service Code:        %s\n", serviceCode));
            receipt.append(String.format("Base Fee:            %.2f OMR\n", baseFee));
            receipt.append("--------------------------------------------\n");
            receipt.append(String.format("Insurance Plan:      %s\n", insuranceType));
            receipt.append(String.format("Percentage Discount: -%.2f OMR\n", percentageDiscount));
            receipt.append(String.format("Fixed Discount:      -%.2f OMR\n", fixedDiscount));
            receipt.append(String.format("Total Discount:      -%.2f OMR\n", totalDiscount));
            receipt.append("--------------------------------------------\n");
            receipt.append(String.format("Subtotal:            %.2f OMR\n", (baseFee - totalDiscount)));
            receipt.append("--------------------------------------------\n");
            receipt.append(String.format("Patient Type:        %s\n", patientType));
            receipt.append(String.format("Surcharge:           +%.2f OMR\n", surcharge));
            receipt.append("============================================\n");
            receipt.append(String.format("FINAL AMOUNT:        %.2f OMR\n", finalAmount));
            receipt.append("============================================\n");
            return receipt.toString();
        }
        
        /**
         * Converts bill breakdown to a string for network transmission.
         * Format: baseFee|percentageDiscount|fixedDiscount|totalDiscount|surcharge|finalAmount
         * 
         * @return Pipe-delimited string representation
         */
        public String toTransmissionString() {
            return String.format("%s|%.2f|%s|%.2f|%.2f|%.2f|%s|%.2f|%.2f",
                serviceCode, baseFee, insuranceType,
                percentageDiscount, fixedDiscount, totalDiscount,
                patientType, surcharge, finalAmount);
        }
        
        /**
         * Creates a BillBreakdown from a transmission string.
         * 
         * @param data The pipe-delimited string
         * @return BillBreakdown object or null if parsing fails
         */
        public static BillBreakdown fromTransmissionString(String data) {
            try {
                String[] parts = data.split("\\|");
                if (parts.length >= 9) {
                    return new BillBreakdown(
                        parts[0],                      // serviceCode
                        Double.parseDouble(parts[1]),  // baseFee
                        parts[2],                      // insuranceType
                        Double.parseDouble(parts[3]),  // percentageDiscount
                        Double.parseDouble(parts[4]),  // fixedDiscount
                        Double.parseDouble(parts[5]),  // totalDiscount
                        parts[6],                      // patientType
                        Double.parseDouble(parts[7]),  // surcharge
                        Double.parseDouble(parts[8])   // finalAmount
                    );
                }
            } catch (Exception e) {
                System.err.println("Error parsing bill breakdown: " + e.getMessage());
            }
            return null;
        }
    }
    
    // ==========================================================================
    // Main method for testing the BillingCalculator
    // ==========================================================================
    
    /**
     * Main method to demonstrate and test the billing calculator.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  MediCare Oman - Billing Calculator Test");
        System.out.println("===========================================\n");
        
        // Print available services
        System.out.println(getAvailableServices());
        
        // Test case 1: Premium patient, Outpatient, MRI
        System.out.println("\n--- Test Case 1: Premium, Outpatient, MRI ---");
        BillBreakdown bill1 = calculateBill("MRI700", "Premium", "Outpatient");
        if (bill1 != null) {
            System.out.println(bill1.getFormattedReceipt());
        }
        
        // Test case 2: Standard patient, Inpatient, Lab Test
        System.out.println("\n--- Test Case 2: Standard, Inpatient, Lab Test ---");
        BillBreakdown bill2 = calculateBill("LAB210", "Standard", "Inpatient");
        if (bill2 != null) {
            System.out.println(bill2.getFormattedReceipt());
        }
        
        // Test case 3: Basic patient, Emergency, X-Ray
        System.out.println("\n--- Test Case 3: Basic, Emergency, X-Ray ---");
        BillBreakdown bill3 = calculateBill("IMG330", "Basic", "Emergency");
        if (bill3 != null) {
            System.out.println(bill3.getFormattedReceipt());
        }
    }
}
