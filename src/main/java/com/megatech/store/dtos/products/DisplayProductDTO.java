package com.megatech.store.dtos.products;

import com.megatech.store.model.ProductModel;

public record DisplayProductDTO(
        Long id,
        String name,
        String image,
        Double price
) {

    public DisplayProductDTO(ProductModel productModel){
        this(productModel.getId(), productModel.getName(), productModel.getImage(), productModel.getPrice());
    }
}
