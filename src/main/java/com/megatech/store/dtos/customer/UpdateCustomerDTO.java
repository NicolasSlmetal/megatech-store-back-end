package com.megatech.store.dtos.customer;

import com.megatech.store.dtos.InputDTO;
import jakarta.validation.constraints.Email;

public record UpdateCustomerDTO(
        String name,
        @Email(message = "the provided email is not a valid email") String email,
        String password
) implements InputDTO {
}
