package com.megatech.store.domain;

import com.megatech.store.exceptions.InvalidCustomerFieldException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Customer {

    private static final int CPF_MAXIMUM_SIZE = 11;
    private static final int FIRST_CPF_DIGIT_POSITION = CPF_MAXIMUM_SIZE - 2;
    private static final int LAST_CPF_DIGIT_POSITION = CPF_MAXIMUM_SIZE - 1;

    private String name;
    private String cpf;
    private LocalDate registrationDate;
    private User user;
    private final List<Address> addresses = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new InvalidCustomerFieldException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            throw new InvalidCustomerFieldException("CPF cannot be null or empty");
        }
        cpf = cpf.replaceAll("[.-]", "");
        validateCpf(cpf);
        this.cpf = cpf;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        if (registrationDate == null) {
            throw new InvalidCustomerFieldException("Registration date cannot be null");
        }
        if (registrationDate.isAfter(LocalDate.now())) {
            throw new InvalidCustomerFieldException("Registration date cannot be after current date");
        }
        this.registrationDate = registrationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new InvalidCustomerFieldException("User cannot be null");
        }
        this.user = user;
    }

    public List<Address> getAddress() {
        return addresses;
    }

    public void addAddress(Address address) {
        if (address == null) {
            throw new InvalidCustomerFieldException("Address cannot be null");
        }
        this.addresses.add(address);
    }


    private void validateCpf(String cpf) {

        String templateErrorMessage = String.format("CPF %s", cpf);


        if (cpf.length() != CPF_MAXIMUM_SIZE) {
            throw new InvalidCustomerFieldException(templateErrorMessage +" should have 11 digits");
        }

        if (cpf.chars().distinct().count() == 1) {
            throw new InvalidCustomerFieldException(templateErrorMessage + " must don`t contain the same digit in all positions");
        }

        if (Pattern.compile("[^0-9]").matcher(cpf).find()) {
            throw new InvalidCustomerFieldException(templateErrorMessage + " contains invalid characters");
        }

        int[] remainders = calculateValidationDigits(cpf);

        if (remainders[0] != Character.getNumericValue(cpf.charAt(FIRST_CPF_DIGIT_POSITION))) {
            throw new InvalidCustomerFieldException(templateErrorMessage + " first validation digit is invalid");
        }

        if (remainders[1] != Character.getNumericValue(cpf.charAt(LAST_CPF_DIGIT_POSITION))) {
            throw new InvalidCustomerFieldException(templateErrorMessage + " second validation digit is invalid");
        }

    }

    private int[] calculateValidationDigits(String cpf) {
        int[] remainders = new int[2];
        char[] validationDigits = cpf.substring(FIRST_CPF_DIGIT_POSITION).toCharArray();

        for (int i = 0; i < validationDigits.length; i++) {

            int positionFactor = cpf.lastIndexOf(validationDigits[i]) + 1;
            int sum = 0;

            for (char digit : cpf.toCharArray()) {

                sum += Character.getNumericValue(digit) * positionFactor--;

                if (positionFactor == 1) break;
            }
            remainders[i] = sum * (cpf.length() - positionFactor) % cpf.length();
            remainders[i] = remainders[i] == 10 ? 0 : remainders[i];
        }
        return remainders;
    }
}
