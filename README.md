# Splitwise Application

A comprehensive expense-sharing application built with Java Swing that helps groups of people track shared expenses, split bills, and settle payments efficiently.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [System Requirements](#system-requirements)
- [Installation & Setup](#installation--setup)
- [How to Run](#how-to-run)
- [User Guide](#user-guide)
- [Architecture](#architecture)
- [File Structure](#file-structure)
- [Technical Details](#technical-details)
- [Algorithms Used](#algorithms-used)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

Splitwise is a desktop application that simplifies expense management for groups. Whether you're sharing an apartment, going on a trip, or managing group activities, this application helps you:

- Track who owes whom
- Split expenses in multiple ways
- Maintain payment history
- Settle debts efficiently
- View detailed spending reports

## ✨ Features

### User Management
- **Registration**: Create new accounts with username and password
- **Login**: Secure authentication system
- **Password Management**: Encrypted password storage

### Group Management
- **Create Groups**: Generate unique group codes for new groups
- **Join Groups**: Join existing groups using invitation codes
- **Multiple Groups**: Participate in unlimited groups simultaneously

### Transaction Management
- **Add Expenses**: Record shared expenses with flexible splitting options
- **Four Split Methods**:
  1. **All Equally**: Split amount equally among all members
  2. **Equally By Some**: Split equally among selected members
  3. **Unequally**: Specify exact amounts for each person
  4. **By Percentages**: Split based on percentage shares

### Financial Tracking
- **Check Balances**: View who owes you and whom you owe
- **Amount Spent**: See detailed spending breakdown per member
- **Payment History**: Track all settled payments
- **Transaction Details**: View complete transaction history

### Payment Settlement
- **Settle Payments**: Record payments between members
- **Automatic Balance Updates**: Balances update in real-time
- **Four Settlement Scenarios**: Handles various debt relationships

### Transaction Management
- **Delete Transactions**: Remove transactions with automatic balance reversal
- **Transaction History**: Maintain complete audit trail

## 💻 System Requirements

- **Java**: JDK 9 or higher (uses Java Module System)
- **Operating System**: Windows, macOS, or Linux
- **Memory**: Minimum 512 MB RAM
- **Display**: 1024x768 minimum resolution

## 🚀 Installation & Setup

### 1. Clone or Download the Repository

```bash
git clone https://github.com/AyushLodha71/Splitwise.git
cd Splitwise
```

### 2. Verify Java Installation

```bash
java -version
```

Ensure you have JDK 9 or higher installed.

### 3. Compile the Application

```bash
javac -d bin src/module-info.java src/splitwiseapplication/*.java
```

This compiles all source files and places the compiled classes in the `bin` directory.

## ▶️ How to Run

### Method 1: Using Java Module System (Recommended)

```bash
java -p bin -m splitwiseApplication/splitwiseapplication.SplitwiseApplicationMain
```

### Method 2: Using Task Runner (if available)

If you have VS Code with tasks configured:
- Press `Cmd+Shift+P` (macOS) or `Ctrl+Shift+P` (Windows/Linux)
- Select "Tasks: Run Task"
- Choose "Run Python Script" or your configured task

## 📖 User Guide

### Getting Started

1. **First Launch**: 
   - The application opens with a "Login or Register" screen
   - Choose "Register" if you're a new user

2. **Registration**:
   - Enter a unique username (e.g., john.doe)
   - Create a password
   - Click "Register"

3. **Login**:
   - Enter your username and password
   - Click "Login"

### Creating Your First Group

1. After login, you'll see the main dashboard
2. Click **"Create Group"**
3. The system generates a unique 7-character group code (e.g., mKEILR5)
4. Share this code with group members
5. Click **"Enter"** to access the group

### Joining an Existing Group

1. From the main dashboard, click **"Join Group"**
2. Enter the 7-character group code shared by the group creator
3. Click **"Join"**
4. The group appears in your groups list

### Adding a Transaction

1. Select a group and click **"Enter"**
2. Click **"Add Transaction"**
3. Select the payer (who paid the bill)
4. Enter the total amount
5. Choose a split method:

   **All Equally**: Everyone pays equal shares
   ```
   Example: $100 bill, 4 people = $25 each
   ```

   **Equally By Some**: Selected people pay equal shares
   ```
   Example: $90 bill, 3 out of 5 people = $30 each for selected 3
   ```

   **Unequally**: Specify exact amounts
   ```
   Example: Alice $40, Bob $30, Charlie $30 = $100 total
   ```

   **By Percentages**: Distribute by percentage
   ```
   Example: Alice 50%, Bob 30%, Charlie 20% of $100
   ```

6. Click **"Add Transaction"**

### Checking Balances

1. From the group menu, click **"Check Balances"**
2. View overall balances for all members
3. Click on any member to see detailed breakdown:
   - Who they owe
   - Who owes them
   - Exact amounts

### Settling a Payment

1. Click **"Settle Payment"** from the group menu
2. Select who is paying (debtor)
3. Select who is receiving (creditor)
4. Enter the settlement amount
5. Click **"Settle Payment"**
6. The system automatically updates all balances

### Viewing Reports

- **Check Amount Spent**: See spending summary per member in a table
- **Transaction Details**: View all transactions in the group
- **Payment History**: Review all settled payments

### Deleting a Transaction

1. Click **"Delete Transaction"**
2. Select the transaction to delete
3. Confirm deletion
4. All related balances are automatically reversed

## 🏗️ Architecture

### Design Patterns

- **Singleton Pattern**: ResourceManager for efficient icon caching
- **MVC Pattern**: Separation of data (files), logic (utility classes), and UI (GUI classes)
- **File-Based Persistence**: No database required - all data stored in text files

### Component Structure

```
┌─────────────────────────────────────┐
│     GUI Layer (Swing Components)    │
│  LoginPageGUI, RegisterPageGUI,     │
│  MainPage, Groups, AddTransaction   │
└─────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│    Business Logic Layer             │
│  Validation, Calculations,          │
│  Balance Management                 │
└─────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│    Utility Classes                  │
│  UpdateFile, Exists, Searches,      │
│  Sorts, Member_Info                 │
└─────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│    Data Layer (File System)         │
│  credentials.txt, groups.txt,       │
│  Personal_Folders/, Groups/         │
└─────────────────────────────────────┘
```

## 📁 File Structure

```
Splitwise/
├── README.md
├── src/
│   ├── module-info.java                    # Java 9+ module definition
│   └── splitwiseapplication/
│       ├── SplitwiseApplicationMain.java   # Entry point
│       ├── ResourceManager.java            # Singleton for icon caching
│       │
│       ├── GUI Classes (User Interface)
│       ├── LoginOrRegister.java            # Welcome screen
│       ├── LoginPageGUI.java               # Login interface
│       ├── RegisterPageGUI.java            # Registration interface
│       ├── MainPage.java                   # User dashboard
│       ├── Groups.java                     # Group navigation hub
│       ├── EnterGroup.java                 # Group selection
│       ├── CreateGroup.java                # Group creation
│       ├── JoinGroup.java                  # Group joining
│       ├── AddTransaction.java             # Transaction creation
│       ├── SettlePayment.java              # Payment settlement
│       ├── DeleteTransaction.java          # Transaction deletion
│       ├── CheckBalances.java              # Balance viewer
│       ├── CheckAmountSpent.java           # Spending report
│       ├── AmountSettled.java              # Payment recording
│       │
│       ├── Utility Classes (Business Logic)
│       ├── UpdateFile.java                 # File writing utilities
│       ├── Exists.java                     # File reading utilities
│       ├── Searches.java                   # Binary search algorithm
│       ├── Sorts.java                      # Merge sort algorithm
│       └── Member_Info.java                # Data class for balances
│
├── bin/                                    # Compiled classes (generated)
│   └── splitwiseapplication/
│
└── Data Files (generated at runtime)
    ├── credentials.txt                     # User accounts
    ├── groups.txt                          # Group registry
    ├── Personal_Folders/                   # User-specific data
    │   ├── username1                       # Groups joined by user
    │   └── username2
    ├── Groups/                             # Group membership
    │   ├── groupcode1                      # Members in group
    │   └── groupcode2
    ├── TransactionDetails/                 # Transaction records
    │   ├── groupcode1                      # All transactions
    │   └── groupcode2
    ├── PendingAmount/                      # Outstanding balances
    │   └── groupcode/
    │       ├── username1                   # Who user owes/is owed
    │       └── username2
    ├── CheckAmountSpentFolder/             # Spending summaries
    │   └── groupcode/
    │       ├── username1                   # Total spent by user
    │       └── username2
    └── PaymentHistory/                     # Settlement records
        └── groupcode/
            ├── username1                   # Payments made/received
            └── username2
```

## 🔧 Technical Details

### Data Storage Format

All data is stored in plain text files with delimiters:

**credentials.txt**
```
username,password
alice.lodha,pass123
bob.smith,secure456
```

**groups.txt**
```
username,groupcode1,groupcode2
alice.lodha,mKEILR5,xYz789A
```

**PendingAmount/groupcode/username**
```
otherUser>amount>direction
bob>50.00>Owes
charlie>30.00>Lends
```

**TransactionDetails/groupcode**
```
payer>amount>description>date>participants
alice>100.00>Dinner>2025-10-13>alice,bob,charlie
```

### Key Algorithms

#### 1. Binary Search (Searches.java)
- **Purpose**: Fast username lookup
- **Time Complexity**: O(log n)
- **Space Complexity**: O(log n) due to recursion
- **Usage**: User authentication, membership verification

#### 2. Merge Sort (Sorts.java)
- **Purpose**: Alphabetically sort usernames and groups
- **Time Complexity**: O(n log n)
- **Space Complexity**: O(n)
- **Usage**: Prepare data for binary search, display sorted lists

#### 3. Balance Calculation
- **Purpose**: Calculate who owes whom after each transaction
- **Algorithm**: 
  1. Track total spent by each person
  2. Calculate fair share (total / participants)
  3. Compute differences (spent - fair share)
  4. Update pending amounts accordingly

#### 4. Settlement Scenarios
The application handles 4 settlement scenarios:

1. **Simple Debt**: A owes B, A pays B
2. **Reverse Debt**: B owes A, A pays B (creates new debt)
3. **Mutual Debts**: A owes B, B owes A (offset amounts)
4. **No Prior Debt**: Neither owes the other (creates new relationship)

### Security Features

- **Password Storage**: Passwords stored in plain text (Note: Consider encryption for production)
- **Input Validation**: Username and amount validation
- **Bounds Checking**: Array access protection
- **Resource Management**: Try-with-resources for file handling

### Performance Optimizations

1. **Icon Caching**: ResourceManager singleton loads icons once
2. **Binary Search**: O(log n) lookups instead of O(n) linear search
3. **Efficient Sorting**: Merge sort O(n log n) for all cases
4. **File Buffering**: BufferedReader/Writer for I/O operations

## 📊 Algorithms Used

### Binary Search Implementation

```java
public static int binarySearch(ArrayList<String> items, String target, int low, int high) {
    if (low > high) {
        return -1; // Not found
    }
    
    int middle = (low + high) / 2;
    int comparison = target.compareTo(items.get(middle));
    
    if (comparison == 0) {
        return middle; // Found
    } else if (comparison < 0) {
        return binarySearch(items, target, low, middle - 1); // Search left
    } else {
        return binarySearch(items, target, middle + 1, high); // Search right
    }
}
```

### Merge Sort Implementation

```java
public static void mergesort(ArrayList<String> items, int start, int end) {
    if (start < end) {
        int mid = (start + end) / 2;
        mergesort(items, start, mid);        // Sort left half
        mergesort(items, mid + 1, end);      // Sort right half
        merge(items, start, mid, end);       // Merge sorted halves
    }
}
```

## 🤝 Contributing

This is a personal project, but suggestions and feedback are welcome!

### Development Guidelines

1. **Code Style**: Follow Java naming conventions
2. **Documentation**: Add JavaDoc for all public methods
3. **Testing**: Test with multiple users and groups
4. **File Handling**: Always use try-with-resources

### Future Enhancements

Potential features for future versions:

- [ ] Database integration (MySQL/PostgreSQL)
- [ ] Password encryption (BCrypt/Argon2)
- [ ] Export reports to PDF/Excel
- [ ] Multi-currency support
- [ ] Email notifications
- [ ] Web/mobile version
- [ ] Receipt photo attachments
- [ ] Recurring expenses
- [ ] Budget limits and alerts
- [ ] Data backup and restore
- [ ] Dark mode theme

## 📝 License

This project is created by **Ayush Lodha** for educational purposes.

## 🐛 Known Issues

- Passwords are stored in plain text (needs encryption)
- No data backup mechanism
- Single-threaded (may freeze on large operations)
- Limited error messages for users

## 📞 Support

For questions or issues:
- **GitHub**: [AyushLodha71](https://github.com/AyushLodha71)
- **Repository**: [Splitwise](https://github.com/AyushLodha71/Splitwise)

## 🙏 Acknowledgments

- Java Swing documentation and community
- Algorithm implementations inspired by classic computer science texts
- Icon resources from open-source repositories

---

**Version**: 2.0  
**Last Updated**: October 13, 2025  
**Developed by**: Ayush Lodha

---

### Quick Start Command

```bash
# Compile
javac -d bin src/module-info.java src/splitwiseapplication/*.java

# Run
java -p bin -m splitwiseApplication/splitwiseapplication.SplitwiseApplicationMain
```

**Happy Expense Splitting! 💰✨**
