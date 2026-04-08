#  Java Banking Application

A complete, production-ready banking system built with **Java 21** demonstrating modern Java features, enterprise design patterns, and comprehensive business logic.

##  Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Installation & Setup](#installation--setup)
- [What I Learned](#what-i-learned)
- [Future Improvements](#future-improvements)
- [Author](#author)

---

## Overview

This is a **console-based banking application** that simulates real-world banking operations. Built as a practice project to master **Java 21** features, OOP design patterns, and enterprise-grade coding practices.

**Key highlights:**
- 27+ test cases passed
- Immutable domain models using Records
- Command pattern with sealed interfaces
- Stream API for data processing
- BigDecimal for precise monetary calculations
- Complete transaction history tracking

---

## Features

### Account Management
- Register new accounts (Savings, Checking, Business)
- Find account by number or holder name
- Update account type and status
- Delete account (with balance validation)

### Transactions
- Deposit money
- Withdraw money (with daily limit and minimum balance checks)
- Transfer between accounts
- Check balance

### Reporting
- Filter transactions by date
- Total deposits and withdrawals across all accounts
- Find highest transaction
- Generate detailed statement of account
- Search transactions by UUID
- Group transactions by type (DEBIT/CREDIT)

### Security & Business Rules
- No duplicate account numbers
- Cannot withdraw below minimum balance (₱1,000 Savings / ₱500 Checking / ₱5,000 Business)
- Daily withdrawal limit: ₱30,000
- Withdrawals must be multiples of ₱100
- Account status: ACTIVE, FROZEN, CLOSED (Frozen accounts cannot transact)
- Self-transfer prevention
- UUID-based transaction IDs

---

## Tech Stack

| Technology    | Version | Purpose         |
|---------------|---------|-----------------|
| Java          | 25      | Core language   |
| IntelliJ IDEA | 2026.1  | IDE             |
| Git           | Latest  | Version control |

**Java Features Used:**
- Records (`AccountHolder`, `Transaction`)
- Sealed Interfaces (`TransactionCommand`)
- Pattern Matching (`instanceof` with binding)
- Stream API (`filter`, `map`, `flatMap`, `reduce`, `groupingBy`, `max`)
- Optional (`ifPresentOrElse`, `orElseThrow`)
- Text Blocks (`"""` for multiline strings)
- BigDecimal (monetary precision)

---

## Architecture
````
src/
└── com/bank/
├── app/
│   └── Main.java
├── constants/
│   └── BankConstants.java
├── contracts/
│   ├── DepositCommand.java
│   ├── TransactionCommand.java
│   ├── TransferCommand.java
│   └── WithdrawCommand.java
├── engine/
│   └── TransactionProcessor.java
├── exceptions/
│   ├── AccountNotFoundException.java
│   ├── InvalidCredentialsException.java
│   └── InvalidTransactionException.java
├── models/
│   ├── AccountHolder.java
│   ├── AccountStatus.java
│   ├── AccountType.java
│   ├── BankAccount.java
│   ├── Transaction.java
│   ├── TransactionType.java
│   └── TransferResult.java
├── repository/
│   └── BankRepository.java
└── service/
└── BankService.java
````

### Design Patterns Used
| Pattern                  | Implementation                                          |
|--------------------------|---------------------------------------------------------|
| **Command Pattern**      | `TransactionCommand` sealed interface + command records |
| **Repository Pattern**   | `BankRepository` separates data from logic              |
| **Immutable Objects**    | Records and `withXxx()` methods                         |
| **Dependency Injection** | Manual constructor injection (no framework)             |

---
## Installation & Setup
### Prerequisites:

- **Java Development Kit (JDK)**: Must be 17 or higher for these features to work
- **Integrated Development Environment:** IntelliJ IDEA (Recommended) or Visual Studio Code and install 'Extension Pack for Java'.

### Clone & Open: 
- Clone the repository: ```git clone <your-repo-link>```
- Open IntelliJ IDEA. 
- Select Open and navigate to the root folder of this project.

### IDE Configuration
- Right-click the src folder. 
- Select Mark Directory as > Sources Root. 
- Go to File > Project Structure > Project. 
- Ensure the SDK is set to at least version 17 (to support those Records and Enums).

### Running the Application: 
- Navigate to ```src/com/bank/app/```.
- Open ```Main.java```. 
- Click the Green Play Button next to the ```public static void main``` method.
---
## What I Learned
Building this banking system provided deep insights into designing statically typed, enterprise-grade architectures in Java:

- **Command Pattern Utility**: By decoupling the "request" (Commands) from the "logic" (Processor), the system is incredibly easy to extend without modifying existing code.

- **Immutable Models**: Using Java Records for data carriers (like TransferResult) ensures thread safety and prevents accidental state mutation.

- **Domain-Driven Design**: Organizing the project by domain (Service, Repository, Engine) instead of just "Classes" makes the codebase feel like a real-world enterprise application.

- **Error Hierarchy**: Designing a custom exception tree allowed for more granular control over business rule violations.
---
## Future Improvements
While the current architecture is robust, future iterations will focus on:

- **Persistence Layer**: Transitioning from an in-memory BankRepository to a persistent database (PostgreSQL/MySQL).

- **Concurrency Support**: Implementing synchronized blocks or ReentrantLock for safe multi-threaded transactions.

- **Spring Boot Migration**: Refactoring the manual dependency injection into a managed Spring IoC container.

- **Unit Testing Framework**: Integrating JUnit 5 and Mockito for automated regression testing.
---
## Author
Cole Kenneth B. Silva

BSIT Student, 3rd year
