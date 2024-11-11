package com.megatech.store.dtos.customer;

import com.megatech.store.model.CustomerModel;

import java.time.LocalDate;

public record CustomerDTO(
        String name,
        String cpf,
        LocalDate registrationDate,
        String email
) {

    public CustomerDTO(CustomerModel customerModel) {
        this(customerModel.getName(), customerModel.getCpf(), customerModel.getRegistrationDate(), customerModel.getUser().getEmail());
    }

}
