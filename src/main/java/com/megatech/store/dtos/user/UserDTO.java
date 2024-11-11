package com.megatech.store.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        @NotBlank(message = "email cannot be null")
        @Email(message = "{value} is not a valid email")
        String email,
        @NotBlank(message = "password cannot be null")
        String password
) {
}
