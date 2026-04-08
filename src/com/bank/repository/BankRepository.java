package com.bank.repository;

import com.bank.constants.BankConstants;
import com.bank.contracts.DepositCommand;
import com.bank.contracts.TransferCommand;
import com.bank.contracts.WithdrawCommand;
import com.bank.engine.TransactionProcessor;
import com.bank.exceptions.AccountNotFoundException;
import com.bank.exceptions.InvalidTransactionException;
import com.bank.models.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class BankRepository {
    private final HashMap<String, BankAccount> bankAccounts = new HashMap<>();
    private final TransactionProcessor processor = new TransactionProcessor();

    public BankAccount saveAccount(BankAccount acc) {
        if (bankAccounts.containsKey(acc.getAccountNumber())) {
            throw new InvalidTransactionException("Account already registered!");
        }
        bankAccounts.put(acc.getAccountNumber(), acc);
        System.out.println("Account saved successfully!");
        return acc;
    }

    public Optional<BankAccount> findAccountByNumber(String accountNumber) {
        return Optional.ofNullable(bankAccounts.get(accountNumber));
    }

    public Optional<BankAccount> findAccountByHolderName(String name) {
        return bankAccounts.values().stream()
                .filter(acc -> acc.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public Optional<Transaction> findTransactionById(UUID transactionID) {
        return bankAccounts.values().stream()
                .flatMap(acc -> acc.getTransactions().stream())
                .filter(t -> t.transactionID().equals(transactionID))
                .findFirst();
    }

    public List<Transaction> filterTransactionsByDate(LocalDate date) {
        return bankAccounts.values().stream()
                .flatMap(acc -> acc.getTransactions().stream())
                .filter(t -> t.timestamp().toLocalDate().equals(date))
                .toList();
    }

    public BigDecimal totalDeposits() {
        return bankAccounts.values().stream()
                .flatMap(acc -> acc.getTransactions().stream())
                .filter(t -> t.type() == TransactionType.CREDIT)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalWithdrawals() {
        return bankAccounts.values().stream()
                .flatMap(acc -> acc.getTransactions().stream())
                .filter(t -> t.type() == TransactionType.DEBIT)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Optional<Transaction> highestTransaction() {
        return bankAccounts.values().stream()
                .flatMap(acc -> acc.getTransactions().stream())
                .max(Comparator.comparing(Transaction::amount));
    }

    public Map<TransactionType, List<Transaction>> groupTransactionsByType() {
        return bankAccounts.values().stream()
                .flatMap(acc -> acc.getTransactions().stream())
                .collect(Collectors.groupingBy(Transaction::type));
    }

    public void generateStatement(String accountNumber) {
        findAccountByNumber(accountNumber).ifPresentOrElse(acc -> {
            BigDecimal deposits = acc.getTransactions().stream()
                    .filter(t -> t.type() == TransactionType.CREDIT)
                    .map(Transaction::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal withdrawals = acc.getTransactions().stream()
                    .filter(t -> t.type() == TransactionType.DEBIT)
                    .map(Transaction::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            System.out.println("=====STATEMENT OF ACCOUNT=====");
            System.out.println("Account: " + acc.getAccountNumber());
            System.out.println("Name: " + acc.getName());
            System.out.println("-----------------------------");
            acc.getTransactions().forEach(System.out::println);
            System.out.println("Total Deposits: " + BankConstants.CURRENCY_FORMAT.format(deposits));
            System.out.println("Total Withdrawals: " + BankConstants.CURRENCY_FORMAT.format(withdrawals));
            System.out.println("Current Balance: " + BankConstants.CURRENCY_FORMAT.format(acc.getBalance()));
        }, () -> { throw new AccountNotFoundException("Account not found!"); });
    }

    public void checkBalance(String accountNumber) {
        findAccountByNumber(accountNumber).ifPresentOrElse(account -> System.out.println("Current Balance: " +
                BankConstants.CURRENCY_FORMAT.format(account.getBalance())),
                () -> System.out.println("Account not found."));
    }

    public void withdraw(String accountNumber, BigDecimal amount) {
        findAccountByNumber(accountNumber).ifPresentOrElse(account -> {

            WithdrawCommand cmd = new WithdrawCommand(amount);
            BankAccount updatedAccount = processor.process(account, cmd);

            Transaction transaction = new Transaction(
                    UUID.randomUUID(),
                    amount,
                    TransactionType.DEBIT,
                    LocalDateTime.now()
            );

            BankAccount withTransaction = updatedAccount.withTransaction(transaction);
            bankAccounts.put(accountNumber, withTransaction);

            System.out.println("Withdrawn: " + BankConstants.CURRENCY_FORMAT.format(amount));
            System.out.println("New Balance: " + BankConstants.CURRENCY_FORMAT.format(withTransaction.getBalance()));

        }, () -> { throw new AccountNotFoundException("Account not found!"); });
    }

    public void deposit(String accountNumber, BigDecimal amount) {
        findAccountByNumber(accountNumber).ifPresentOrElse(account -> {
            DepositCommand cmd = new DepositCommand(amount);

            BankAccount updatedAccount = processor.process(account, cmd);

            Transaction transaction = new Transaction(
                    UUID.randomUUID(),
                    amount,
                    TransactionType.CREDIT,
                    LocalDateTime.now()
            );

            BankAccount withTransaction = updatedAccount.withTransaction(transaction);
            bankAccounts.put(accountNumber, withTransaction);

            System.out.println("Deposited: " + BankConstants.CURRENCY_FORMAT.format(amount));
            System.out.println("New Balance: " + BankConstants.CURRENCY_FORMAT.format(withTransaction.getBalance()));

        }, () -> { throw new AccountNotFoundException("Account not found!"); });
    }

    public void transfer(String fromAccount, String toAccount, BigDecimal amount) {
        if (fromAccount.equals(toAccount)) {
            throw new InvalidTransactionException("You cannot transfer money to the same account.");
        }
        BankAccount sender = findAccountByNumber(fromAccount)
                .orElseThrow(() -> new AccountNotFoundException("Sender account not found!"));

        BankAccount receiver = findAccountByNumber(toAccount)
                .orElseThrow(() -> new AccountNotFoundException("Receiver account not found!"));

        TransferCommand cmd = new TransferCommand(amount, toAccount);
        TransferResult result = processor.processTransfer(sender, receiver, cmd);

        Transaction debitTransaction = new Transaction(
                UUID.randomUUID(),
                amount,
                TransactionType.DEBIT,
                LocalDateTime.now()
        );

        Transaction creditTransaction = new Transaction(
                UUID.randomUUID(),
                amount,
                TransactionType.CREDIT,
                LocalDateTime.now()
        );

        BankAccount updatedSender = result.updatedSender().withTransaction(debitTransaction);
        BankAccount updatedReceiver = result.updatedReceiver().withTransaction(creditTransaction);

        bankAccounts.put(fromAccount, updatedSender);
        bankAccounts.put(toAccount, updatedReceiver);

        System.out.println("Transferred: " + BankConstants.CURRENCY_FORMAT.format(amount));
        System.out.println("From: " + fromAccount + " to: " + toAccount);
    }

    public void deleteAccount(String accountNumber) {
        findAccountByNumber(accountNumber).ifPresentOrElse(acc -> {
            if (acc.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                throw new InvalidTransactionException("Account cannot be closed due to remaining balance: " +
                                BankConstants.CURRENCY_FORMAT.format(acc.getBalance()) +
                                ". Please withdraw all your money first."
                );
            }
            bankAccounts.remove(accountNumber);
            System.out.println("Account " + accountNumber + " successfully closed.");
        }, () -> { throw new AccountNotFoundException("Account not found!"); });
    }

    public void updateAccount(String accountNumber, AccountType newType, AccountStatus newStatus) {
        findAccountByNumber(accountNumber).ifPresentOrElse(account -> {
            BankAccount updated = account
                    .withAccountType(newType)
                    .withAccountStatus(newStatus);
            bankAccounts.put(accountNumber, updated);
            System.out.println("Account updated successfully!");
        }, () -> { throw new AccountNotFoundException("Account not found!"); });
    }
}
