# Splitwise - Expense Splitting Application

[![Java](https://img.shields.io/badge/Java-11%2B-orange.svg)](https://www.oracle.com/java/technologies/downloads/)
[![Railway](https://img.shields.io/badge/Deployed%20on-Railway-blueviolet.svg)](https://railway.app/)
[![Backend](https://img.shields.io/badge/Backend-Spring%20Boot-green.svg)](https://spring.io/projects/spring-boot)
[![Database](https://img.shields.io/badge/Database-MySQL-blue.svg)](https://www.mysql.com/)

## Project Overview

Splitwise is a Java Swing-based desktop application designed to help users split expenses among group members, track balances, and settle payments. The application features a comprehensive GUI with multiple screens for managing groups, transactions, and viewing balances.

### ⚡ Quick Start
Download `Splitwise.jar` and run it - no installation or setup required! The application connects to a cloud-hosted backend for seamless expense tracking.

## Architecture

### Technology Stack
- **Frontend**: Java Swing (Desktop GUI)
- **Backend**: Spring Boot REST API hosted on Railway
  - **Live API**: [https://splitwise.up.railway.app](https://splitwise.up.railway.app)
  - **Backend Repository**: [https://github.com/AyushLodha71/Database](https://github.com/AyushLodha71/Database)
  - Contains Spring Boot application with REST endpoints
  - Deployed on Railway platform with MySQL database
- **HTTPS Client**: java.net.http.HttpClient (all connections use secure HTTPS)
- **Database**: MySQL hosted on Railway with 8 separate databases (db1-db8) for different data entities
- **Deployment**: Cloud-hosted backend on Railway platform for 24/7 availability

### Design Pattern
- **Two-step initialization**: Constructor builds UI → runGUI() displays frame
- **ActionListener pattern**: Event-driven UI interactions
- **API-based architecture**: All data operations through REST endpoints

## Core Features

### 1. User Authentication
- **LoginPageGUI.java**: User login with username/password
- **RegisterPageGUI.java**: New user registration
- **LoginOrRegister.java**: Entry point for choosing login or register

### 2. Group Management
- **Groups.java**: View all groups user is part of
- **CreateGroup.java**: Create new expense-splitting groups
- **JoinGroup.java**: Join existing groups using group codes
- **EnterGroup.java**: Enter a specific group to view details

### 3. Transaction Management
- **AddTransaction.java**: Add new expenses with multiple split methods:
  - Equal split among all members
  - Equal split among selected members
  - Unequal amounts (custom values per member)
  - Percentage-based split
- **DeleteTransaction.java**: Remove transactions and reverse balance effects
- **SettlePayment.java**: Record payments between members to settle debts

### 4. Balance Tracking
- **CheckBalances.java**: View who owes whom and how much
- **CheckAmountSpent.java**: See individual member spending summary
- **AmountSettled.java**: View payment/settlement history

### 5. Main Interface
- **MainPage.java**: Central hub with access to all features
- **SplitwiseApplicationMain.java**: Application entry point

## Utility Classes

- **ApiCaller.java**: Comprehensive HTTPS client for backend communication
  - Handles all REST API requests with specialized methods
  - Communicates with Railway-hosted Spring Boot backend
  - Supports GET, POST, UPDATE, DELETE operations
  - Provides type-specific methods for different data formats
  
- **Exists.java**: Smart validation utility
  - Checks existence of groups, users, and credentials
  - Prevents duplicate entries and validates access rights
  
- **Member_Info.java**: Session management
  - Stores current user context (username, group code)
  - Maintains state across screens

## Database Structure

The application uses a sophisticated 8-database architecture for optimal data organization and performance:

- **db1**: **CheckAmountSpent** - Individual member balances and total spending per group
  - Tracks how much each member has spent or owes
  - Contains per-group tables with Name and Amount columns
  - Includes "Total" row for group-wide expenditure tracking

- **db2**: **Credentials** - User authentication system
  - Stores username and password for login validation
  - Central authentication table for all users

- **db3**: **GroupNames** - Group metadata registry
  - Maps group codes to human-readable group names
  - Used for group lookup and validation

- **db4**: **Group Members** - Member lists per group
  - Contains per-group tables listing all members
  - Validates membership before allowing group access

- **db5**: **PaymentHistory** - Transaction logs per group
  - Records all transactions with Payee, Amount, Reason
  - Includes transaction type (TType) and unique transaction ID (tID)
  - Maintains complete audit trail of group activities

- **db6**: **PendingAmount** - Balance settlements between members
  - Tracks who owes whom within each group
  - Contains Member1, Amount, Member2 relationships
  - Enables calculation of net balances

- **db7**: **User Groups** - Personal group memberships
  - Per-user tables listing all groups user belongs to
  - Stores GroupID and GroupName for quick access
  - Powers the "My Groups" view

- **db8**: **TransactionDetails** - Expense split tracking
  - Dynamic per-group tables with columns for each member
  - Tracks individual contributions to each transaction
  - Includes Creator and tID for transaction attribution

## File Structure

```
Splitwise/
├── Splitwise.jar                    # Executable JAR file (ready to run)
├── README.md                        # Project documentation
├── src/splitwiseapplication/        # Source code
│   ├── Authentication & Entry
│   │   ├── SplitwiseApplicationMain.java
│   │   ├── LoginOrRegister.java
│   │   ├── LoginPageGUI.java
│   │   └── RegisterPageGUI.java
│   │
│   ├── Group Management
│   │   ├── Groups.java
│   │   ├── CreateGroup.java
│   │   ├── JoinGroup.java
│   │   └── EnterGroup.java
│   │
│   ├── Transaction Management
│   │   ├── AddTransaction.java
│   │   ├── DeleteTransaction.java
│   │   └── SettlePayment.java
│   │
│   ├── Balance & Reports
│   │   ├── CheckBalances.java
│   │   ├── CheckAmountSpent.java
│   │   └── AmountSettled.java
│   │
│   ├── Main Interface
│   │   └── MainPage.java
│   │
│   └── Utilities
│       ├── ApiCaller.java
│       ├── Exists.java
│       └── Member_Info.java
└── bin/                             # Compiled class files
```

## Code Documentation

**Documentation Status**: ✅ **100% Complete**

All application files feature comprehensive contract-style documentation:
- Class-level purpose and functionality descriptions
- Method-level documentation with pre/post-conditions
- Parameter and return value descriptions
- Algorithm explanations for complex logic
- Database interactions clearly documented
- Usage locations and call patterns referenced

## Running the Application

### Quick Start (Easiest Method)
1. Download `Splitwise.jar`
2. Double-click to run (or use: `java -jar Splitwise.jar`)
3. Start tracking expenses!

### System Requirements
- **Java**: Version 22 or higher ([Download Java](https://www.oracle.com/java/technologies/downloads/))
- **Internet Connection**: Required to connect to Railway backend
- **Operating System**: Windows, macOS, or Linux

### Option 1: Run Executable JAR (Recommended)
The easiest way to run the application is using the pre-built JAR file:

1. **Download**: `Splitwise.jar` from this repository
2. **Run**: Double-click the JAR file, or run from terminal:
   ```bash
   java -jar Splitwise.jar
   ```
3. **Start using**: The application connects automatically to the cloud-hosted backend
4. No additional setup required - backend and database are already hosted on Railway

### Option 2: Run from Source Code

#### Prerequisites
- Java 11 or higher
- IDE (Eclipse, IntelliJ IDEA, or VS Code with Java extensions)

#### Execution
1. **Clone this repository**
   ```bash
   git clone https://github.com/AyushLodha71/Splitwise.git
   ```
2. **Open project** in your IDE
3. **Run**: Execute `SplitwiseApplicationMain.java` as main class
4. **Start using**: Choose Login or Register from entry screen
5. After authentication, navigate through Groups and MainPage

### Backend Information
- **Backend is cloud-hosted on Railway** - No local setup needed
- **Database is online** - Accessible from anywhere
- **API Endpoint**: https://splitwise.up.railway.app
- **Backend Repository**: [https://github.com/AyushLodha71/Database](https://github.com/AyushLodha71/Database)

### First Time Users
1. Launch the application
2. Click "Register" to create a new account
3. Enter username and password
4. Login with your credentials
5. Create or join expense groups
6. Start splitting expenses!

### Project Structure
This project consists of two components:
1. **Frontend (this repository)**: Java Swing desktop application
   - Available as executable JAR file (`Splitwise.jar`)
   - Source code in `src/splitwiseapplication/`
2. **Backend**: [https://github.com/AyushLodha71/Database](https://github.com/AyushLodha71/Database)
   - Spring Boot REST API deployed on Railway
   - MySQL database hosted on Railway
   - Accessible at: https://splitwise.up.railway.app

## Cloud Architecture & Deployment

### Railway Platform
The application backend and database are deployed on **Railway**, a modern cloud platform that provides:
- **24/7 Availability**: Backend runs continuously without manual intervention
- **Managed MySQL Database**: Railway handles database hosting, backups, and scaling
- **Automatic HTTPS**: Secure encrypted communication between frontend and backend
- **Zero Configuration**: No local backend setup required for end users

### Connection Flow
```
Desktop Application (Splitwise.jar)
          ↓ HTTPS
Railway Backend (splitwise.up.railway.app)
          ↓ MySQL Connection
Railway MySQL Database (8 databases: db1-db8)
```

### Benefits of Cloud Deployment
- **Easy Distribution**: Users only need Java installed - no database or backend setup
- **Cross-Platform**: Works on Windows, macOS, and Linux
- **Data Accessibility**: Access your expense data from any device running the application
- **Automatic Updates**: Backend updates deployed without requiring user action
- **Scalability**: Railway infrastructure handles multiple concurrent users

## API Endpoints

The application communicates with the Spring Boot backend through RESTful endpoints:

### Data Retrieval
- `GET /db{N}/GetRowData?table={table}` - Fetch complete table data
- `GET /db{N}/GetSpecificData?val={column}&table={table}` - Fetch specific columns
- `GET /db{N}/GetRowData?table={table}&{column}={value}` - Filtered queries

### Data Modification
- `POST /db{N}/InsertData?table={table}&params={columns}&info={values}` - Insert new records
- `POST /db{N}/UpdateData?table={table}&where={condition}&{column}={value}` - Update records
- `POST /db{N}/DeleteRowData?table={table}&{column}={value}` - Delete specific records

### Schema Operations
- `POST /db{N}/CreateTable?table={table}&columns={schema}` - Create new tables
- `POST /db{N}/AddColumn?table={table}&uname={column}` - Add columns dynamically
- `POST /db{N}/DropColumn?table={table}&uname={column}` - Remove columns

## Key Features & Highlights

### Smart Balance Tracking
- Real-time balance updates across all transactions
- Bidirectional payment tracking (who owes whom)
- Automatic balance settlement calculations

### Flexible Expense Splitting
- **Equal Split**: Divide expenses evenly among all members
- **Selective Split**: Split among chosen members only
- **Custom Amounts**: Specify exact amounts per member
- **Custom Percentages Split**: Distribute based on percentages

### Multi-Database Architecture
- Optimized data separation for performance
- Scalable design supporting unlimited groups and users
- Dynamic table creation for new groups

### User Experience
- Simple and intuitive GUI with Java Swing components
- Password visibility toggle with eye icons
- Comprehensive transaction history
- Group membership management
- **Executable JAR for easy deployment** - No installation needed
- **Cloud connectivity** - Access your data from anywhere

## Development History

- **May 2025**: Initial repository creation and first week's development
- **August 2025**: Fully functional code with data storage in .txt files
- **November 2025**: 
  - Migrated to MySQL database with Spring Boot REST API
  - Deployed backend and database to Railway cloud platform
  - Created executable JAR file for easy distribution
  - Application now fully cloud-enabled with 24/7 availability

---

*For detailed method documentation, refer to inline comments in each source file.*
