package com.megatech.store.dtos.purchase;

import com.megatech.store.dtos.InputDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Set;

public record InsertPurchaseDTO(

        @NotNull(message = "product list cannot be null")
        @NotEmpty(message = "product list cannot be empty")
        Set<@Valid CartItemDTO> products
) implements InputDTO {
}
