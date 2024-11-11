package com.megatech.store.dtos.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressDTO(
        @NotBlank(message = "street cannot be null or blank") String street,
        @NotNull(message = "number cannot be null") Integer number,
        @NotBlank(message = "city cannot be null or blank") String city,
        @NotBlank(message = "state cannot be null or blank") String state,
        @NotBlank(message = "zipcode cannot be null or blank") String zipcode
) {
}
