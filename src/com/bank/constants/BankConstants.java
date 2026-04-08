package com.bank.constants;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Pattern;

public class BankConstants {
    public static final Pattern ACCOUNT_NUMBER_FORMAT = Pattern.compile("^[0-9]{4}-[0-9]{4}-[0-9]{2}$");

    public static final String ACCOUNT_FORMAT_HINT = "SAMPLE FORMAT: 1234-5678-90";

    public static final Pattern CONTACT_FORMAT = Pattern.compile("^09[0-9]{9}$");

    public static final String CONTACT_NUMBER_HINT = "SAMPLE FORMAT: 09XX-XXX-XXXX";

    public static final BigDecimal DAILY_LIMIT = new BigDecimal("30000");

    public static final BigDecimal HUNDRED_MULTIPLE = new BigDecimal("100");

    public static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.of("en", "PH"));

    private BankConstants() {}
}
