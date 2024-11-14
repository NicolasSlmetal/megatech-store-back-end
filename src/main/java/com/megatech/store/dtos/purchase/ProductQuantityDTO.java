package com.megatech.store.dtos.purchase;

import com.megatech.store.dtos.products.DisplayProductDTO;

public record ProductQuantityDTO(
        DisplayProductDTO productDTO,
        Integer quantity
) {
}
