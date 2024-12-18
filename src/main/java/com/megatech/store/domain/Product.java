package com.megatech.store.domain;

import com.megatech.store.dtos.products.InsertProductDTO;
import com.megatech.store.dtos.products.UpdateProductDTO;
import com.megatech.store.exceptions.ErrorType;
import com.megatech.store.exceptions.InvalidProductFieldException;
import com.megatech.store.model.ProductModel;

import java.time.LocalDateTime;

public class Product  implements Cloneable, Entity<UpdateProductDTO> {

    private Long id;
    private String name;
    private String image;
    private String manufacturer;
    private Double price;
    private Integer stockQuantity;
    private LocalDateTime entryDate;

    public void update(UpdateProductDTO updateProductDTO) {
        if (updateProductDTO.name() != null)
            setName(updateProductDTO.name());
        if (updateProductDTO.price() != null)
            setPrice(updateProductDTO.price());
        if (updateProductDTO.stockQuantity() != null)
            setStockQuantity(updateProductDTO.stockQuantity());
        if (updateProductDTO.manufacturer() != null)
            setManufacturer(updateProductDTO.manufacturer());
        if (updateProductDTO.image() != null)
            setImage(updateProductDTO.image());
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        if (image == null || image.isEmpty()) {
            throw new InvalidProductFieldException("Image cannot be null or empty", ErrorType.INVALID_PRODUCT_IMAGE);
        }
        this.image = image;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        if (price <= 0) {
            throw new InvalidProductFieldException("Price cannot be negative or zero", ErrorType.INVALID_PRODUCT_PRICE);
        }
        this.price = price;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        if (manufacturer == null || manufacturer.isBlank()) {
            throw new InvalidProductFieldException("Manufacturer cannot be null or empty", ErrorType.INVALID_PRODUCT_MANUFACTURER);
        }
        this.manufacturer = manufacturer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidProductFieldException("Name cannot be null or empty", ErrorType.INVALID_PRODUCT_NAME);
        }
        this.name = name;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        if (stockQuantity == null || stockQuantity < 0) {
            throw new InvalidProductFieldException("Stock quantity cannot be negative", ErrorType.INVALID_PRODUCT_STOCK_QUANTITY);
        }
        this.stockQuantity = stockQuantity;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDateTime entryDate) {
        if (entryDate == null) {
            throw new InvalidProductFieldException("Entry date cannot be null", ErrorType.INVALID_DATE_PARAMETER);
        }
        if (entryDate.isAfter(LocalDateTime.now())) {
            throw new InvalidProductFieldException("Entry date cannot be after now", ErrorType.INVALID_DATE_PARAMETER);
        }
        this.entryDate = entryDate;
    }

    @Override
    public Product clone() {
        try {
            Product clone = (Product) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
