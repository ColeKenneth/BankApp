package com.bank.engine;

import com.bank.contracts.DepositCommand;
import com.bank.contracts.TransactionCommand;
import com.bank.contracts.TransferCommand;
import com.bank.contracts.WithdrawCommand;
import com.bank.exceptions.InvalidTransactionException;
import com.bank.models.BankAccount;
import com.bank.models.TransferResult;

import java.math.BigDecimal;

public class TransactionProcessor {
    public BankAccount process(BankAccount acc, TransactionCommand cmd) {
        if (!acc.getAccountStatus().isOperational()) {
            throw new InvalidTransactionException("Account is not active!");
        }

        return switch (cmd) {
            case DepositCommand d -> acc.withBalance(acc.getBalance().add(d.amount()));
            case WithdrawCommand w -> {
                BigDecimal newBalance = acc.getBalance().subtract(w.amount());
                if (newBalance.compareTo(acc.getAccountType().getMinBalance()) < 0) {
                    throw new InvalidTransactionException("Insufficient funds! Minimum balance is ₱" + acc.getAccountType().getMinBalance());
                }
                yield acc.withBalance(newBalance);
            }
            case TransferCommand t -> throw new InvalidTransactionException("Use processTransfer() for transfers.");
        };
    }

    public TransferResult processTransfer(BankAccount sender, BankAccount receiver, TransferCommand cmd) {
        if (!sender.getAccountStatus().isOperational()) {
            throw new InvalidTransactionException("Sender account is not active!");
        }

        if (!receiver.getAccountStatus().isOperational()) {
            throw new InvalidTransactionException("Receiver account is not active!");
        }

        if (!cmd.targetAccount().equals(receiver.getAccountNumber())) {
            throw new InvalidTransactionException("Target account does not match receiver.");
        }

        BigDecimal newSenderBalance = sender.getBalance().subtract(cmd.amount());
        if (newSenderBalance.compareTo(sender.getAccountType().getMinBalance()) < 0) {
            throw new InvalidTransactionException("Insufficient funds for transferring!");
        }

        BankAccount updatedSender = sender.withBalance(newSenderBalance);
        BankAccount updatedReceiver = receiver.withBalance(receiver.getBalance().add(cmd.amount()));

        return new TransferResult(updatedSender, updatedReceiver);
    }

}
