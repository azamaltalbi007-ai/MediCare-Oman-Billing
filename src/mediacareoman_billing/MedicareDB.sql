-- =============================================================================
-- MediCare Oman Billing System - Database Setup Script
-- =============================================================================
-- This script creates the MedicareDB database with Patient and PatientBill tables
-- Run this script in MySQL before starting the application
-- =============================================================================

-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS MedicareDB;

-- Use the MedicareDB database
USE MedicareDB;

-- =============================================================================
-- Table: Patient
-- Description: Stores patient information including their insurance plan type
-- Columns:
--   - id: Primary key, unique identifier for each patient
--   - name: Patient's full name
--   - age: Patient's age in years
--   - insurance_plan_type: Type of insurance ('Premium', 'Standard', 'Basic')
-- =============================================================================
CREATE TABLE IF NOT EXISTS Patient (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    insurance_plan_type ENUM('Premium', 'Standard', 'Basic') NOT NULL
);

-- =============================================================================
-- Table: PatientBill
-- Description: Stores billing records for patient visits
-- Columns:
--   - bill_id: Primary key for the bill record
--   - patient_id: Foreign key referencing Patient table
--   - visit_date: Date of the patient's visit
--   - bill_amount: Final calculated bill amount in OMR
-- =============================================================================
CREATE TABLE IF NOT EXISTS PatientBill (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    visit_date DATE NOT NULL,
    bill_amount DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES Patient(id) ON DELETE CASCADE
);

-- =============================================================================
-- Insert 5 dummy patient records for testing
-- These records cover all three insurance plan types
-- =============================================================================
INSERT INTO Patient (name, age, insurance_plan_type) VALUES
    ('Ahmed Al-Rashid', 45, 'Premium'),
    ('Fatima Al-Balushi', 32, 'Standard'),
    ('Mohammed Al-Habsi', 28, 'Basic'),
    ('Aisha Al-Lawati', 55, 'Premium'),
    ('Khalid Al-Siyabi', 40, 'Standard');

-- =============================================================================
-- Verify the data insertion
-- =============================================================================
SELECT * FROM Patient;

-- End of SQL Script
