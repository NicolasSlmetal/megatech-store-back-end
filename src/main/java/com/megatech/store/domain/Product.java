package com.megatech.store.domain;

import com.megatech.store.dtos.products.InsertProductDTO;
import com.megatech.store.dtos.products.UpdateProductDTO;
import com.megatech.store.exceptions.InvalidProductFieldException;
import com.megatech.store.model.ProductModel;

public class Product {

    private Long id;
    private String name;
    private String manufacturer;
    private Double price;
    private Integer stockQuantity;

    public Product(InsertProductDTO productDTO) {
        setName(productDTO.name());
        setManufacturer(productDTO.manufacturer());
        setPrice(productDTO.price());
        setStockQuantity(productDTO.stockQuantity());
    }
    public Product(ProductModel productModel) {
        setId(productModel.getId());
        setName(productModel.getName());
        setManufacturer(productModel.getManufacturer());
        setPrice(productModel.getPrice());
        setStockQuantity(productModel.getStockQuantity());
    }

    public void update(UpdateProductDTO updateProductDTO) {
        if (updateProductDTO.name() != null)
            setName(updateProductDTO.name());
        if (updateProductDTO.price() != null)
            setPrice(updateProductDTO.price());
        if (updateProductDTO.stockQuantity() != null)
            setStockQuantity(updateProductDTO.stockQuantity());
        if (updateProductDTO.manufacturer() != null)
            setManufacturer(updateProductDTO.manufacturer());
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        if (price <= 0) throw new InvalidProductFieldException("Price cannot be negative or zero");
        this.price = price;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        if (manufacturer == null || manufacturer.isBlank()) throw new InvalidProductFieldException("Manufacturer cannot be null or empty");
        this.manufacturer = manufacturer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new InvalidProductFieldException("Name cannot be null or empty");
        this.name = name;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        if (stockQuantity < 0) throw new InvalidProductFieldException("Stock quantity cannot be negative");
        this.stockQuantity = stockQuantity;
    }
}
