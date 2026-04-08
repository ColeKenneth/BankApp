package com.bank.contracts;

import com.bank.constants.BankConstants;
import com.bank.exceptions.InvalidTransactionException;

import java.math.BigDecimal;
import java.util.Objects;

public record WithdrawCommand(BigDecimal amount) implements TransactionCommand {
    public WithdrawCommand {
        Objects.requireNonNull(amount, "Amount cannot be null!");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount must only be positive!");
        }

        if (amount.remainder(BankConstants.HUNDRED_MULTIPLE).compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidTransactionException("Withdrawals must only be multiples of ₱100!");
        }

        if (amount.compareTo(BankConstants.DAILY_LIMIT) > 0) {
            throw new InvalidTransactionException("Limit exceeded. Daily limit: ₱" + BankConstants.DAILY_LIMIT);
        }
    }
}
