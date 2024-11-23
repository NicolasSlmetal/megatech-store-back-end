package com.megatech.store.dtos.user;

import com.megatech.store.dtos.InputDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        @NotBlank(message = "email cannot be null")
        @Email(message = "the provided email is not a valid email")
        String email,
        @NotBlank(message = "password cannot be null")
        String password
) implements InputDTO {
}
