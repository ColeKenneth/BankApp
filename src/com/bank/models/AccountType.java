package com.bank.models;

import java.math.BigDecimal;

public enum AccountType {
    SAVINGS("Savings Account", new BigDecimal("1000.00")),
    CHECKING("Checking Account", new BigDecimal("500.00")),
    BUSINESS("Business Account", new BigDecimal("5000.00"));

    private final String accountType;
    private final BigDecimal minBalance;

    AccountType(String accountType, BigDecimal minBalance) {
        this.accountType = accountType;
        this.minBalance = minBalance;
    }

    public String getAccountType() {
        return accountType;
    }

    public BigDecimal getMinBalance() {
        return minBalance;
    }
}
