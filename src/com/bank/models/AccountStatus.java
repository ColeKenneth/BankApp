package com.bank.models;

public enum AccountStatus {
    ACTIVE("This account is currently active."),
    FROZEN("This account is frozen."),
    CLOSED("This account is now officially closed.");

    private final String status;

    AccountStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public boolean isOperational() {
        return this == ACTIVE;
    }
}
