# 🏥 MediCare Oman - Billing System

## 📌 Short Description
A robust **Client–Server Java application** for hospital billing management using **multithreading**, **JDBC (MySQL)**, and **socket programming**. Multiple receptionist clients can connect to a central server to generate bills concurrently.

---

## 🚀 Project Overview

MediCare Oman Billing System simulates a hospital billing process where patient bills are calculated based on:

- Medical Services
- Insurance Plans
- Patient Type (Inpatient, Emergency, Outpatient)

The system follows a **client–server architecture** where multiple clients (receptionists) can connect to one server at the same time.

---

## 🛠️ Tech Stack

- **Language:** Java (JDK 8+)
- **IDE:** Apache NetBeans
- **Database:** MySQL
- **Networking:** Java Sockets (`java.net`)
- **Concurrency:** Multithreading (`java.lang.Thread`)
- **Database Connectivity:** JDBC (MySQL Connector/J)

---

## 📂 Project Structure

```

MediCare-Oman-Billing/
├── src/mediacareoman_billing/
│   ├── MediacareOman_Billing.java   # Main Menu / Launcher
│   ├── MedicareServer.java          # Multithreaded Server
│   ├── MedicareClient.java          # Client-side Input & UI
│   ├── BillingCalculator.java       # Billing Business Logic
│   ├── DBConnection.java            # JDBC Connection Manager
│   ├── MedicareUtility.java         # Collections Demo
│   └── MedicareDB.sql               # Database Script
├── libs/
│   └── mysql-connector-j-8.3.0.jar  # JDBC Driver
└── README.md

```

---

## ⚙️ Setup & Installation

### 1️⃣ Prerequisites
- Java JDK 8 or above
- NetBeans IDE (recommended)
- MySQL Server (XAMPP / WAMP / Standalone)

---

### 2️⃣ Database Configuration

1. Open **MySQL Workbench** or terminal.
2. Execute the SQL file:

```

src/mediacareoman_billing/MedicareDB.sql

````

3. Update database credentials in `DBConnection.java` if required:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/MedicareDB";
private static final String DB_USER = "medicare_admin";
private static final String DB_PASSWORD = "admin123";
````

⚠️ Make sure MySQL service is running.

---

### 3️⃣ JDBC Library Setup

Add the MySQL Connector JAR to your project:

* **NetBeans:**
  Right-click Project → Properties → Libraries → Add JAR/Folder → Select
  `mysql-connector-j-8.3.0.jar`

---

## ▶️ How to Run the Project

### 🖥️ Step 1: Start Server

1. Run `MediacareOman_Billing.java`
2. Select **Option 1 – Start Server**
3. Server listens on **Port 5000**

---

### 💻 Step 2: Start Client(s)

1. Run the same project in a new terminal or IDE instance
2. Select **Option 2 – Start Client**
3. Enter patient and service details to generate bills

✔️ Multiple clients can connect simultaneously.

---

## 📊 Billing Logic

### 🔹 Service Charges

| Code    | Service      | Cost (OMR) |
| ------- | ------------ | ---------: |
| CONS100 | Consultation |       12.0 |
| LAB210  | Lab Test     |        8.5 |
| IMG330  | X-Ray        |       25.0 |
| US400   | Ultrasound   |       35.0 |
| MRI700  | MRI          |      180.0 |

---

### 🔹 Insurance Discounts

* **Premium:** 15% discount + OMR 5 flat off
* **Standard:** 10% discount + OMR 8 flat off
* **Basic:** No percentage discount + OMR 10 flat off

---

### 🔹 Patient Type Surcharge

* Emergency
* Inpatient
* Outpatient

(Additional charges applied where applicable)

---

## 🔌 Concurrency & Networking

* Server uses **multithreading** to handle multiple clients.
* Each client connection runs in a separate thread.
* Communication via **TCP sockets** on port **5000**.

---

## 🧩 Key Files

* `MedicareServer.java` → Server logic
* `MedicareClient.java` → Client interface
* `BillingCalculator.java` → Billing rules
* `DBConnection.java` → Database connectivity
* `MedicareDB.sql` → Database schema

---

## 🛠️ Troubleshooting

* ❌ **JDBC Error:** Ensure MySQL connector JAR is added
* ❌ **DB Connection Failed:** Verify MySQL credentials
* ❌ **Port Error:** Change port in Server & Client files

---

## 👤 Author

Developed for **MediCare Oman – Java Client Server Billing System Assignment**

---

## 📜 License

This project is for **educational purposes only**.
You are free to modify and reuse it.

```
