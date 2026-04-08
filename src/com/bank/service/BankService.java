package com.bank.service;

import com.bank.constants.BankConstants;
import com.bank.engine.TransactionProcessor;
import com.bank.exceptions.AccountNotFoundException;
import com.bank.exceptions.InvalidCredentialsException;
import com.bank.exceptions.InvalidTransactionException;
import com.bank.models.*;
import com.bank.repository.BankRepository;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BankService {
    private final BankRepository repository = new BankRepository();
    private final TransactionProcessor processor = new TransactionProcessor();
    private final Scanner sc = new Scanner(System.in);
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void run() {
        String choice;

        do {
            showMenu();
            choice = sc.nextLine().trim().toUpperCase();
            try {
                switch (choice) {
                    case "A" -> registerAccount();
                    case "B" -> findAccByNumber();
                    case "C" -> findAccByHolderName();
                    case "D" -> findTransactionByID();
                    case "E" -> filterTransactionByDate();
                    case "F" -> totalDeposits();
                    case "G" -> totalWithdrawals();
                    case "H" -> highestTransaction();
                    case "I" -> groupTransactionsByType();
                    case "J" -> statementOfAccount();
                    case "K" -> withdraw();
                    case "L" -> deposit();
                    case "M" -> checkBalance();
                    case "N" -> deleteAcc();
                    case "O" -> updateAcc();
                    case "P" -> transferMoney();
                    case "X" -> System.out.println("Exiting...");
                    default -> System.out.println("Invalid choice.");


                }
            } catch (AccountNotFoundException | InvalidCredentialsException | InvalidTransactionException e) {
                System.out.println("ERROR: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("SYSTEM ERROR: " + e.getMessage());
            }
        } while (!choice.equals("X"));
    }

    private void showMenu() {
        System.out.println("=====BANK APP=====");
        System.out.println("""
                A. Register Account
                B. Find Account by Number
                C. Find Account by Holder Name
                D. Find Transaction by ID
                E. Filter Transactions by Date
                F. Show Total Deposits
                G. Show Total Withdrawals
                H. Look for Highest Transaction
                I. Group Accounts by Type
                J. Generate Statement of Account
                K. Withdraw
                L. Deposit
                M. Check Balance
                N. Delete Account
                O. Update Account
                P. Transfer Money
                X. Exit
                """);
        System.out.print("Select from the menu: ");
    }

    private void registerAccount() {
        System.out.print("Customer ID: ");
        String customerId = sc.nextLine().trim();

        System.out.print("Account Number: ");
        String accountNumber = sc.nextLine().trim();

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Address: ");
        String address = sc.nextLine().trim();

        System.out.print("Contact Number: ");
        String contact = sc.nextLine().trim();

        BigDecimal balance = null;
        while (balance == null) {
            try {
                System.out.print("Enter initial balance: ");
                String input = sc.nextLine().trim();
                if (input.isBlank()) {
                    System.out.println("Please enter an amount.");
                    continue;
                }
                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Amount must be positive.");
                } else {
                    balance = value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        AccountType type = null;
        while (type == null) {
            System.out.print("Account Type (SAVINGS/CHECKING/BUSINESS): ");
            try {
                type = AccountType.valueOf(sc.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid account type!");
            }
        }

        System.out.print("Account Status (ACTIVE/FROZEN/CLOSED): ");
        String statusInput = sc.nextLine().trim().toUpperCase();
        AccountStatus status = AccountStatus.ACTIVE; // default

        try {
            AccountStatus parsed = AccountStatus.valueOf(statusInput);
            if (parsed == AccountStatus.CLOSED) {
                System.out.println("WARNING: New accounts cannot be CLOSED. Defaulting to ACTIVE.");
            } else {
                status = parsed;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status! Defaulting to ACTIVE.");
        }


        List<Transaction> history = new ArrayList<>();
        AccountHolder accountHolder = new AccountHolder(name, address, contact, customerId);
        BankAccount account = new BankAccount(accountNumber, accountHolder, balance, type, status, history);

        repository.saveAccount(account);
    }

    private void findAccByNumber() {
        System.out.print("Enter account number to search: ");
        String accNumber = sc.nextLine().trim();

        if (accNumber.isBlank()) {
            System.out.println("Account number cannot be empty.");
            return;
        }

        repository.findAccountByNumber(accNumber).ifPresentOrElse(System.out::println,
                () -> System.out.println("Account number " + accNumber + " not found!"));
    }

    private void findAccByHolderName() {
        System.out.print("Enter account holder's name: ");
        String accHolder = sc.nextLine().trim();

        if (accHolder.isBlank()) {
            System.out.println("Account holder cannot be empty.");
            return;
        }

        repository.findAccountByHolderName(accHolder).ifPresentOrElse(System.out::println,
                () -> System.out.println("Account holder " + accHolder +  " not found."));
    }

    private void findTransactionByID() {
        System.out.print("Enter transaction ID (UUID format): ");
        String idInput = sc.nextLine().trim();

        if (idInput.isBlank()) {
            System.out.println("Transaction ID cannot be empty.");
            return;
        }

        try {
            UUID transactionID = UUID.fromString(idInput);
            repository.findTransactionById(transactionID).ifPresentOrElse(System.out::println,
                    () -> System.out.println("Transaction ID " + transactionID + " not found!"));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid transaction ID provided. Use UUID format.");
        }
    }

    private void filterTransactionByDate() {
        try {
            System.out.print("Enter date (FORMAT: YYYY-MM-DD): ");
            String transactionDate = sc.nextLine().trim();
            LocalDate targetDate = LocalDate.parse(transactionDate, format);

            List<Transaction> transactions = repository.filterTransactionsByDate(targetDate);

            if (transactions.isEmpty()) {
                System.out.println("No transactions found for date: " + targetDate);
            } else {
                transactions.forEach(System.out::println);
            }
        } catch (DateTimeException e) {
            System.out.println("Invalid date format! Please use YYYY-MM-DD format.");
        }
    }

    private void totalDeposits() {
        BigDecimal total = repository.totalDeposits();
        System.out.println("Total Deposits: " + BankConstants.CURRENCY_FORMAT.format(total));
    }

    private void totalWithdrawals() {
        BigDecimal total = repository.totalWithdrawals();
        System.out.println("Total Withdrawals: " + BankConstants.CURRENCY_FORMAT.format(total));
    }

    private void highestTransaction() {
        repository.highestTransaction().ifPresentOrElse(transaction -> System.out.println("Highest Transaction: " + transaction),
                () -> System.out.println("No transactions found."));
    }

    private void groupTransactionsByType() {
        System.out.print("Enter transaction type (DEBIT/CREDIT): ");
        String accType = sc.nextLine().trim().toUpperCase();

        try {
            TransactionType type = TransactionType.valueOf(accType);
            Map<TransactionType, List<Transaction>> grouped = repository.groupTransactionsByType();
            List<Transaction> transactions = grouped.getOrDefault(type, List.of());

            if (transactions.isEmpty()) {
                System.out.println("No " + type + " transactions found.");
            } else {
                System.out.println(type + " Transactions (" + transactions.size() + "):");
                transactions.forEach(System.out::println);
            }


        } catch (IllegalArgumentException e) {
            System.out.println("Invalid transaction type! Enter DEBIT or CREDIT.");
        }
    }

    private void statementOfAccount() {
        System.out.println("Enter account number: ");
        String accNumber = sc.nextLine().trim();
        repository.generateStatement(accNumber);
    }

    private void withdraw() {
        System.out.print("Enter account number: ");
        String accNumber = sc.nextLine().trim();

        BigDecimal amount = null;
        while (amount == null) {
            System.out.print("Enter amount to withdraw: ");
            try {
                String input = sc.nextLine().trim();
                if (input.isBlank()) {
                    System.out.println("Please enter an amount.");
                    continue;
                }
                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Amount must be positive.");
                } else {
                    amount = value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        repository.withdraw(accNumber, amount);
    }

    private void deposit() {
        System.out.print("Enter account number: ");
        String accNumber = sc.nextLine().trim();
        BigDecimal amount = null;
        while (amount == null) {
            System.out.print("Enter amount to deposit: ");
           try {
               String input = sc.nextLine().trim();
               if (input.isBlank()) {
                   System.out.println("Please enter an amount.");
                   continue;
               }
               BigDecimal value = new BigDecimal(input);
               if (value.compareTo(BigDecimal.ZERO) <= 0) {
                   System.out.println("Amount must be positive.");
               } else {
                   amount = value;
               }
           } catch (NumberFormatException e) {
               System.out.println("Invalid input. Please enter a number.");
           }
        }
        repository.deposit(accNumber, amount);
    }

    private void checkBalance() {
        System.out.print("Enter account number: ");
        String accNumber = sc.nextLine().trim();

        repository.checkBalance(accNumber);
    }

    private void deleteAcc() {
        System.out.print("Enter account number: ");
        String accNumber = sc.nextLine().trim();

        repository.deleteAccount(accNumber);
    }

    private void updateAcc() {
        System.out.print("Enter account number: ");
        String accNumber = sc.nextLine().trim();

        System.out.print("Select new account type (SAVINGS/CHECKING/BUSINESS): ");
        AccountType newType = null;
        while (newType == null) {
            try {
                newType = AccountType.valueOf(sc.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid account type. Select between SAVINGS, CHECKING, and BUSINESS.");
            }
        }

        System.out.print("Select new account status (ACTIVE/FROZEN/CLOSED): ");
        AccountStatus status = null;
        while (status == null) {
            try {
                status = AccountStatus.valueOf(sc.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid account status. Select between ACTIVE, FROZEN, and CLOSED.");
            }
        }
        repository.updateAccount(accNumber, newType, status);
    }

    private void transferMoney() {
        System.out.print("Enter your account number: ");
        String fromAccount = sc.nextLine().trim();

        System.out.print("Enter recipient's account number: ");
        String toAccount = sc.nextLine().trim();

        System.out.print("Enter amount to transfer: ");
        BigDecimal amount = null;
        while (amount == null) {
            if (sc.hasNextBigDecimal()) {
                amount = sc.nextBigDecimal();
                sc.nextLine();
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Amount must be positive.");
                    amount = null;
                }
            } else {
                System.out.println("Invalid amount! Please enter a number.");
                sc.nextLine();
            }
        }

        repository.transfer(fromAccount, toAccount, amount);
    }

}
