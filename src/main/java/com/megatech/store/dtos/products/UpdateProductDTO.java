package com.megatech.store.dtos.products;

import com.megatech.store.dtos.InputDTO;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateProductDTO(
        String name,
        String image,
        String manufacturer,
        @Positive(message = "price must be positive") Double price,
        @PositiveOrZero(message = "stockQuantity must be zero or higher") Integer stockQuantity
) implements InputDTO {
}
