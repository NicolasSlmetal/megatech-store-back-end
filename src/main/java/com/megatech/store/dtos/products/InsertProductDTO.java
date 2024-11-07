package com.megatech.store.dtos.products;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record InsertProductDTO(
        @NotBlank(message = "name cannot be null or blank") String name,
        @NotBlank(message = "image cannot be null or blank") String image,
        @NotBlank(message = "manufacturer cannot be null or blank") String manufacturer,
        @NotNull(message = "price cannot be null") @Positive(message = "price must be positive") Double price,
        @NotNull(message = "stockQuantity cannot be null")
        @PositiveOrZero(message = "stockQuantity must be zero or higher") Integer stockQuantity
        ) {
}
