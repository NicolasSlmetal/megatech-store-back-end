package com.megatech.store.domain;

import com.megatech.store.dtos.customer.InsertCustomerDTO;
import com.megatech.store.dtos.customer.UpdateCustomerDTO;
import com.megatech.store.dtos.user.UserDTO;
import com.megatech.store.exceptions.ErrorType;
import com.megatech.store.exceptions.InvalidCPFException;
import com.megatech.store.exceptions.InvalidCustomerFieldException;
import com.megatech.store.model.CustomerModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Customer implements Cloneable, Entity<UpdateCustomerDTO> {

    private static final int CPF_MAXIMUM_SIZE = 11;
    private static final int FIRST_CPF_DIGIT_POSITION = CPF_MAXIMUM_SIZE - 2;
    private static final int LAST_CPF_DIGIT_POSITION = CPF_MAXIMUM_SIZE - 1;

    private String name;
    private String cpf;
    private LocalDate registrationDate;
    private User user;
    private final List<Address> addresses = new ArrayList<>();

    public Customer() {

    }

    public Customer(InsertCustomerDTO insertCustomerDTO) {
        setName(insertCustomerDTO.name());
        setCpf(insertCustomerDTO.cpf());
    }

    public Customer(CustomerModel customerModel) {
        setUser(new User(customerModel.getUser()));
        setName(customerModel.getName());
        setCpf(customerModel.getCpf());
        setRegistrationDate(customerModel.getRegistrationDate());
    }

    public void update(UpdateCustomerDTO customerDTO) {
        if (customerDTO.name() != null) {
            setName(customerDTO.name());
        }
        getUser().update(new UserDTO(customerDTO.email(), customerDTO.password()));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new InvalidCustomerFieldException("Name cannot be null or empty", ErrorType.INVALID_CUSTOMER_NAME);
        }
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            throw new InvalidCPFException("CPF cannot be null or empty");
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
            throw new InvalidCustomerFieldException("Registration date cannot be null", ErrorType.INVALID_DATE_PARAMETER);
        }
        if (registrationDate.isAfter(LocalDate.now())) {
            throw new InvalidCustomerFieldException("Registration date cannot be after current date", ErrorType.INVALID_DATE_PARAMETER);
        }
        this.registrationDate = registrationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new InvalidCustomerFieldException("User cannot be null", ErrorType.INVALID_NULL_ATTRIBUTE);
        }
        this.user = user;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void addAddress(Address address) {
        if (address == null) {
            throw new InvalidCustomerFieldException("Address cannot be null", ErrorType.INVALID_NULL_ATTRIBUTE);
        }
        this.addresses.add(address);
    }


    private void validateCpf(String cpf) {

        String templateErrorMessage = "CPF ";


        if (cpf.length() != CPF_MAXIMUM_SIZE) {
            throw new InvalidCPFException(templateErrorMessage +"should have 11 digits");
        }

        if (cpf.chars().distinct().count() == 1) {
            throw new InvalidCPFException(templateErrorMessage + "must don`t contain the same digit in all positions");
        }

        if (Pattern.compile("[^0-9]").matcher(cpf).find()) {
            throw new InvalidCPFException(templateErrorMessage + "contains invalid characters");
        }

        int[] remainders = calculateValidationDigits(cpf);

        if (remainders[0] != Character.getNumericValue(cpf.charAt(FIRST_CPF_DIGIT_POSITION))) {
            throw new InvalidCPFException(templateErrorMessage + "is invalid");
        }

        if (remainders[1] != Character.getNumericValue(cpf.charAt(LAST_CPF_DIGIT_POSITION))) {
            throw new InvalidCPFException(templateErrorMessage + "is invalid");
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

    @Override
    public Customer clone() {
        try {
            Customer clone = (Customer) super.clone();

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
