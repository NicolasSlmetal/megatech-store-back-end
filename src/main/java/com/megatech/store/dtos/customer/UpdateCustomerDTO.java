package com.megatech.store.dtos.customer;

import jakarta.validation.constraints.Email;

public record UpdateCustomerDTO(
        String name,
        @Email(message = "{value} is not a valid email") String email,
        String password
) {
}
