package com.megatech.store.dtos.purchase;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Objects;

public record CartItemDTO(
        @NotNull(message = "quantity of product cannot be null")
        @Positive(message = "quantity of product must be positive")
        Integer quantity,
        @NotNull(message = "product cannot be null")
        @Positive(message = "quantity of product must be positive")
        Long productId
) {
        @Override
        public boolean equals(Object o) {
                if (o == null || getClass() != o.getClass()) return false;
                CartItemDTO that = (CartItemDTO) o;
                return Objects.equals(productId, that.productId);
        }

        @Override
        public int hashCode() {
                return Objects.hashCode(productId);
        }
}
