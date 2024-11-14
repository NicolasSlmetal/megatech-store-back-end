package com.megatech.store.projections;

public interface TotalValueSoldPerProduct {

    Long getProductId();
    String getProductName();
    Double getTotalValue();
    Integer getQuantitySold();
}
