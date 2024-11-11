package com.megatech.store.dtos.customer;

import com.megatech.store.dtos.address.AddressDTO;
import com.megatech.store.dtos.user.UserDTO;
import jakarta.validation.constraints.NotBlank;

public record InsertCustomerDTO(
        @NotBlank(message = "name cannot be null or blank") String name,

        @NotBlank(message = "cpf cannot be null or blank") String cpf,
        AddressDTO address,
        UserDTO user
) {
}
