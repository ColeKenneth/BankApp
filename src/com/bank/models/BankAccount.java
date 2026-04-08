package com.bank.models;

import com.bank.exceptions.InvalidTransactionException;
import com.bank.constants.BankConstants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BankAccount {
    private final String accountNumber;
    private final AccountHolder accountHolder;
    private final BigDecimal balance;
    private final AccountType accountType;
    private final AccountStatus accountStatus;
    private final List<Transaction> transactions;

    public BankAccount(String accountNumber, AccountHolder accountHolder, BigDecimal balance, AccountType accountType, AccountStatus accountStatus, List<Transaction> transactions) {
        this.accountNumber = Objects.requireNonNull(accountNumber, "Account number cannot be null!");
        this.accountHolder = Objects.requireNonNull(accountHolder, "Account holder cannot be null!");
        this.balance = Objects.requireNonNull(balance, "Balance cannot be null!");
        this.accountType = Objects.requireNonNull(accountType, "Account type cannot be null!");
        this.accountStatus = Objects.requireNonNull(accountStatus, "Account status cannot be null!");
        this.transactions = List.copyOf(Objects.requireNonNull(transactions, "Transactions cannot be null!"));


        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidTransactionException("Initial balance cannot be negative!");
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getName() {
        return accountHolder.name();
    }

    public String getAddress() {
        return accountHolder.address();
    }

    public String getContact() {
        return accountHolder.contact();
    }

    public String getIDNumber() {
        return accountHolder.idNumber();
    }

    public AccountHolder getAccountHolder() {
        return accountHolder;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BankAccount withBalance(BigDecimal newBalance) {
        Objects.requireNonNull(newBalance, "Balance cannot be null!");
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidTransactionException("Balance cannot be negative!");
        }
        return new BankAccount(this.accountNumber, this.accountHolder, newBalance, accountType, accountStatus, transactions);
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BankAccount withAccountType(AccountType newType) {
        Objects.requireNonNull(newType, "Account type cannot be null!");
        return new BankAccount(this.accountNumber, this.accountHolder, this.balance, newType, this.accountStatus, transactions);
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public BankAccount withAccountStatus(AccountStatus newAccountStatus) {
        Objects.requireNonNull(newAccountStatus, "Account status cannot be null!");
        return new BankAccount(this.accountNumber, this.accountHolder, this.balance, this.accountType, newAccountStatus, transactions);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public BankAccount withTransaction(Transaction transaction) {
        List<Transaction> updated = new ArrayList<>(this.transactions);
        updated.add(transaction);
        return new BankAccount(this.accountNumber, this.accountHolder, this.balance, this.accountType, this.accountStatus, updated);
    }


    @Override
    public String toString() {
        String transactionHistory = transactions.isEmpty()
                ? "No transactions yet" : transactions.stream()
                                          .map(Transaction::toString)
                                          .collect(Collectors.joining("\n"));
        return String.format("""
                ==========================================
                        BANK ACCOUNT INFORMATION
                ==========================================
                Account ID: %s
                Account Number: %s
                Name: %s
                Address: %s
                Contact Number: %s
                Balance: %s
                Account Type: %s
                Account Status: %s
                Transactions: %s
                """, accountHolder.idNumber(), accountNumber, accountHolder.name(), accountHolder.address(),
                accountHolder.contact(), BankConstants.CURRENCY_FORMAT.format(balance), accountType, accountStatus, transactionHistory);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(accountNumber, that.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

}
