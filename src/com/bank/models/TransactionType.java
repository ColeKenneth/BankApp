package com.bank.models;

public enum TransactionType {
    DEBIT("Money credited to account"),
    CREDIT("Money debited from account");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
