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

### Active Utilities
- **ApiCaller.java**: Handles all HTTP requests to backend API
- **Exists.java**: Validates group existence
- **Member_Info.java**: Stores user session data (username, group code)

### Legacy/Unused Utilities
- **Sorts.java**: Merge sort implementation (unused)
- **Searches.java**: Binary search implementation (unused)
- **UpdateFile.java**: Legacy file I/O operations (replaced by API)

## Database Structure

The application uses 8 databases for data separation:
- **db1**: Individual member balances and total spending
- **db2**: Transaction history records
- **db3**: Payment/settlement records
- **db4**: Pending amounts between members
- **db5-db8**: Additional data entities

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
├── Utilities
│   ├── ApiCaller.java
│   ├── Exists.java
│   ├── Member_Info.java
│   ├── Sorts.java (unused)
│   ├── Searches.java (unused)
│   └── UpdateFile.java (unused)
│
└── Data Storage
    ├── credentials.txt
    ├── groups.txt
    └── [User/Group Folders]
```

## Code Documentation

**Documentation Status**: ✅ **100% Complete**

All 19 main application files have comprehensive contract-style documentation including:
- Class-level purpose and functionality descriptions
- Method-level documentation with pre/post-conditions
- Parameter and return value descriptions
- Algorithm explanations for complex logic
- Known bugs and limitations documented
- Unused code marked with ⚠️ warnings
- Usage locations referenced

## Known Issues

### Bugs
1. **DeleteTransaction.java**: ArrayIndexOutOfBoundsException risk in Repay() method when API returns empty arrays
2. **LoginPageGUI.java**: Eye icons for password visibility may not display correctly

### Unused Code
- `containsValue()` method appears in multiple files but never called
- `Sorts.java` - entire class unused
- `Searches.java` - entire class unused
- `UpdateFile.java` - entire legacy class unused
- Multiple unused variables (options, fileLoc, mmbrl, rowData, etc.)

## Running the Application

### Prerequisites
- Java 11 or higher
- MySQL database installed and running
- Spring Boot backend configured and running on localhost:8080
  - **Clone and setup backend**: [https://github.com/AyushLodha71/Database](https://github.com/AyushLodha71/Database)
  - Follow backend repository instructions to set up MySQL databases (db1-db8)
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

The application communicates with REST endpoints:
- `GET /db{N}/GetRowData?table={table}` - Fetch table data
- `POST /db{N}/PostData?table={table}` - Insert data
- `POST /db{N}/DeleteData?table={table}&id={id}` - Delete records
- `POST /db{N}/UpdateData?table={table}&id={id}` - Update records

## Development History

**May 18th**: Initial repository creation with first week's work
**Latest Update**: Complete code documentation with contract-style comments for all classes and methods

## Future Improvements

1. Fix ArrayIndexOutOfBoundsException in DeleteTransaction
2. Remove unused code (Sorts, Searches, UpdateFile classes)
3. Clean up unused variables flagged in documentation
4. Fix icon visibility issues in LoginPageGUI
5. Add input validation and error handling
6. Implement proper logging instead of printStackTrace()
7. Consider consolidating databases or using JPA/Hibernate

---

*For detailed method documentation, refer to inline comments in each source file.*
