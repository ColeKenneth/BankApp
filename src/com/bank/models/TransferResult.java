package com.bank.models;

import java.util.Objects;

public record TransferResult(BankAccount updatedSender, BankAccount updatedReceiver) {
    public TransferResult {
        Objects.requireNonNull(updatedSender, "Sender cannot be null!");
        Objects.requireNonNull(updatedReceiver, "Receiver cannot be null!");
    }
}
