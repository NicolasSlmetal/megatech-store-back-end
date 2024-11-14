package com.megatech.store.domain;

import com.megatech.store.exceptions.InvalidPurchaseFieldException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


public class Purchase {

    private Long id;
    private Customer customer;
    private final Set<ProductQuantityMapping> productQuantityMappings = new HashSet<>();
    private LocalDateTime date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id == null) {
            throw new InvalidPurchaseFieldException("Id cannot be null");
        }
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        if (customer == null) {
            throw new InvalidPurchaseFieldException("Customer cannot be null");
        }
        this.customer = customer;
    }

    public Set<ProductQuantityMapping> getProductQuantityMappings() {
        return productQuantityMappings;
    }

    public void addProductQuantityMapping(ProductQuantityMapping productQuantity) {
        if (productQuantity == null) {
            throw new InvalidPurchaseFieldException("Mapping of product and their quantity cannot be null");
        }
        productQuantityMappings.add(productQuantity);
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        if (date == null) {
            throw new InvalidPurchaseFieldException("Date cannot be null");
        }
        if (date.isAfter(LocalDateTime.now())) {
            throw new InvalidPurchaseFieldException("Date cannot be after now");
        }
        this.date = date;
    }

}
