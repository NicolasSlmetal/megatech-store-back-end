package com.megatech.store.projections;

import java.time.LocalDateTime;

public interface TotalValueInStockPerProduct {

    Long getProductId();
    String getProductName();
    Double getProductPrice();
    LocalDateTime getEntryDate();
    Integer getQuantity();
    Double getTotalValue();
}
