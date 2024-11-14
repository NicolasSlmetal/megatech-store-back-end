package com.megatech.store.dtos.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginDTO
        (@Email(message = "email is not valid")
         @NotBlank(message = "email cannot be null or blank")
         String email,
         @NotBlank(message = "password cannot be null or blank") String password) {
}
