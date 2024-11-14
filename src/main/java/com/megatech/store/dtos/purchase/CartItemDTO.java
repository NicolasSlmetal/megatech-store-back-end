package com.megatech.store.dtos.purchase;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemDTO(
        @NotNull(message = "quantity of product cannot be null")
        @Positive(message = "quantity of product must be positive")
        Integer quantity,
        @NotNull(message = "product cannot be null")
        @Positive(message = "quantity of product must be positive")
        Long productId
) {
}
