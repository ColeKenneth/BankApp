package com.bank.contracts;

import com.bank.constants.BankConstants;
import com.bank.exceptions.InvalidTransactionException;

import java.math.BigDecimal;
import java.util.Objects;

public record DepositCommand(BigDecimal amount) implements TransactionCommand {
    public DepositCommand {
        Objects.requireNonNull(amount, "Amount cannot be null!");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount must only be positive!");
        }

        if (amount.remainder(BankConstants.HUNDRED_MULTIPLE).compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidTransactionException("Transaction amount must be a multiple of ₱100!");
        }
    }
}
