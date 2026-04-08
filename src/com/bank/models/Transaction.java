package com.bank.models;

import com.bank.constants.BankConstants;
import com.bank.exceptions.InvalidTransactionException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record Transaction(UUID transactionID, BigDecimal amount, TransactionType type, LocalDateTime timestamp) {
    public Transaction {
        Objects.requireNonNull(transactionID, "Transaction ID cannot be null!");
        Objects.requireNonNull(amount, "Amount cannot be null!");
        Objects.requireNonNull(type, "Transaction type cannot be null!");
        Objects.requireNonNull(timestamp, "Timestamp cannot be null!");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Transaction amount must only be positive!");
        }

        if (amount.compareTo(BankConstants.DAILY_LIMIT) > 0) {
            throw new InvalidTransactionException("Limit exceeded. Daily limit: ₱" + BankConstants.DAILY_LIMIT);
        }

        if (amount.remainder(BankConstants.HUNDRED_MULTIPLE).compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidTransactionException("Transaction amount must be a multiple of ₱100!");
        }

        if (timestamp.isAfter(LocalDateTime.now())) {
            throw new InvalidTransactionException("Transaction cannot happen in the future!");
        }
    }

    @Override
    public String toString() {
        return String.format("""
                ===============================
                       TRANSACTIONS
                ===============================
                Transaction ID: %s
                Type: %s
                Amount: %s
                Timestamp: %s
                """, transactionID, type, BankConstants.CURRENCY_FORMAT.format(amount), timestamp);
    }
}
