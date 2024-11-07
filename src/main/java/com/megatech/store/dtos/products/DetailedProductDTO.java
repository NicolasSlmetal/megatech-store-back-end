package com.megatech.store.dtos.products;

import com.megatech.store.model.ProductModel;

public record DetailedProductDTO(
        Long id,
        String name,
        String image,
        String manufacturer,
        Double price,
        Integer stockQuantity
) {

    public DetailedProductDTO(ProductModel productModel){
        this(productModel.getId(), productModel.getName(), productModel.getImage(), productModel.getManufacturer(),
                productModel.getPrice(), productModel.getStockQuantity());
    }
}
