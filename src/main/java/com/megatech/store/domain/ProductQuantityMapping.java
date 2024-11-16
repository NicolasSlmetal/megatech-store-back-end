package com.megatech.store.domain;


import com.megatech.store.exceptions.ErrorType;
import com.megatech.store.exceptions.InvalidPurchaseFieldException;

public class ProductQuantityMapping {

    private Product product;
    private Integer quantity;

    public ProductQuantityMapping(Product product, Integer quantity) {
        setProduct(product);
        setQuantity(quantity);
    }

    public Integer getQuantity() {
        return quantity;
    }

    private void setQuantity(Integer quantity) {
        if (quantity == null) {
            throw new InvalidPurchaseFieldException("quantity cannot be null", ErrorType.INVALID_PURCHASE_QUANTITY);
        }
        if (quantity < 0) {
            throw new InvalidPurchaseFieldException("quantity cannot be negative", ErrorType.INVALID_PURCHASE_QUANTITY);
        }
        if (product == null || quantity > product.getStockQuantity()) {
            throw new InvalidPurchaseFieldException("quantity cannot be greater than product", ErrorType.INVALID_PURCHASE_QUANTITY);
        }
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        if (product == null) {
            throw new InvalidPurchaseFieldException("product cannot be null", ErrorType.INVALID_NULL_ATTRIBUTE);
        }
        this.product = product;
    }
}
