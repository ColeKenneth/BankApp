package com.bank.contracts;

import com.bank.constants.BankConstants;
import com.bank.exceptions.InvalidTransactionException;

import java.math.BigDecimal;
import java.util.Objects;


public record TransferCommand(BigDecimal amount, String targetAccount) implements TransactionCommand {
    public TransferCommand {
        Objects.requireNonNull(amount, "Amount cannot be null!");
        Objects.requireNonNull(targetAccount, "Target account cannot be null!");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount must only be positive!");
        }

        if (targetAccount.isBlank()) {
            throw new InvalidTransactionException("Target account cannot be blank!");
        }

        if (!BankConstants.ACCOUNT_NUMBER_FORMAT.matcher(targetAccount).matches()) {
            throw new InvalidTransactionException("Invalid account number format! " + BankConstants.ACCOUNT_FORMAT_HINT);
        }

        if (amount.compareTo(BankConstants.DAILY_LIMIT) > 0) {
            throw new InvalidTransactionException("Limit exceeded. Daily Limit: ₱" + BankConstants.DAILY_LIMIT);
        }
    }
}
