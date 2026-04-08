package com.bank.contracts;

import java.math.BigDecimal;

public sealed interface TransactionCommand permits DepositCommand, WithdrawCommand, TransferCommand {
    BigDecimal amount();
}
