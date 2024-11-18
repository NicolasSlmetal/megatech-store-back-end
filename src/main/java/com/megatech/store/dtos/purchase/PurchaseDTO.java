package com.megatech.store.dtos.purchase;

import java.time.LocalDateTime;
import java.util.Set;

public record PurchaseDTO(
        Set<ProductQuantityDTO> productQuantities,
        String customerName,
        LocalDateTime date,
        Double totalValue
) {
}
