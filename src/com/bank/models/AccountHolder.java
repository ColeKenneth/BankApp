package com.bank.models;

import com.bank.constants.BankConstants;
import com.bank.exceptions.InvalidCredentialsException;

import java.util.Objects;

public record AccountHolder(String name, String address, String contact, String idNumber) {
    public AccountHolder {
        Objects.requireNonNull(name, "Name cannot be null!");
        Objects.requireNonNull(address, "Address cannot be null!");
        Objects.requireNonNull(contact, "Contact number cannot be null!");
        Objects.requireNonNull(idNumber, "ID Number cannot be null!");

        if (name.isBlank()) {
            throw new InvalidCredentialsException("Your name is required!");
        }

        if (address.isBlank()) {
            throw new InvalidCredentialsException("Your address is required!");
        }

        if (contact.isBlank()) {
            throw new InvalidCredentialsException("Your contact number is required!");
        }

        if (idNumber.isBlank()) {
            throw new InvalidCredentialsException("Your ID number is required!");
        }

        if (!BankConstants.CONTACT_FORMAT.matcher(contact).matches()) {
            throw new InvalidCredentialsException("Invalid contact number! " + BankConstants.CONTACT_NUMBER_HINT);
        }
    }
}
