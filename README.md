# Splitwise - Expense Splitting Application

## Project Overview

Splitwise is a Java Swing-based desktop application designed to help users split expenses among group members, track balances, and settle payments. The application features a comprehensive GUI with multiple screens for managing groups, transactions, and viewing balances.

## Architecture

### Technology Stack
- **Frontend**: Java Swing (Desktop GUI)
- **Backend**: Spring Boot REST API (localhost:8080)
  - **Backend Repository**: [https://github.com/AyushLodha71/Database](https://github.com/AyushLodha71/Database)
  - Contains Spring Boot application with REST endpoints
  - Connects to MySQL database
- **HTTP Client**: java.net.http.HttpClient
- **Database**: MySQL with 8 separate databases (db1-db8) for different data entities

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

- **ApiCaller.java**: Comprehensive HTTP client for backend communication
  - Handles all REST API requests with specialized methods
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
src/splitwiseapplication/
├── Authentication & Entry
│   ├── SplitwiseApplicationMain.java
│   ├── LoginOrRegister.java
│   ├── LoginPageGUI.java
│   └── RegisterPageGUI.java
│
├── Group Management
│   ├── Groups.java
│   ├── CreateGroup.java
│   ├── JoinGroup.java
│   └── EnterGroup.java
│
├── Transaction Management
│   ├── AddTransaction.java
│   ├── DeleteTransaction.java
│   └── SettlePayment.java
│
├── Balance & Reports
│   ├── CheckBalances.java
│   ├── CheckAmountSpent.java
│   └── AmountSettled.java
│
├── Main Interface
│   └── MainPage.java
│
└── Utilities
    ├── ApiCaller.java
    ├── Exists.java
    ├── Member_Info.java
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

### Prerequisites
- Java 11 or higher
- MySQL database installed and running
- Spring Boot backend configured and running on localhost:8080
  - **Clone and setup backend**: [https://github.com/AyushLodha71/Database](https://github.com/AyushLodha71/Database)
  - Setup MySQL Databases with a few tables (Message me for guidance)
- Backend databases (db1-db8) configured and accessible

### Execution
1. **Start MySQL database** with required databases (db1-db8)
2. **Start Spring Boot backend** from the [Database repository](https://github.com/AyushLodha71/Database)
3. **Run this application**: Execute `SplitwiseApplicationMain.java` as main class
4. Choose Login or Register from entry screen
5. After authentication, navigate through Groups and MainPage

### Project Structure
This project consists of two repositories:
1. **Frontend (this repository)**: Java Swing desktop application
2. **Backend**: [https://github.com/AyushLodha71/Database](https://github.com/AyushLodha71/Database) - Spring Boot REST API with MySQL integration

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
- Simple GUI with Java Swing components
- Password visibility toggle with eye icons
- Comprehensive transaction history
- Group membership management

## Development History

- **May 2025**: Initial repository creation and first week's development
- **August 2025**: Fully functional code with data storage in .txt files
- **November 2025**: Fully functional code with data storage in local MySQL Database. Uses Spring Boot and REST API to interact with MySQL.
- **Future Plans**: 
  1. Migrate the database from offline to online hosting.
  2. Add Encryption to Password.

---

*For detailed method documentation, refer to inline comments in each source file.*
