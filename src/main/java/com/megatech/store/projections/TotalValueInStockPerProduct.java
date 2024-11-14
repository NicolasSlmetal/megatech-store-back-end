package com.megatech.store.projections;

public interface TotalValueInStockPerProduct {

    Long getProductId();
    String getProductName();
    Double getProductPrice();
    Integer getQuantity();
    Double getTotalValue();
}
