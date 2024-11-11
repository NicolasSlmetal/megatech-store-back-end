package com.megatech.store.factory;

import com.megatech.store.domain.Product;
import com.megatech.store.dtos.products.InsertProductDTO;
import com.megatech.store.model.ProductModel;
import org.springframework.stereotype.Component;

@Component
public class ProductFactory implements EntityModelFactory<Product, ProductModel, InsertProductDTO> {

    @Override
    public Product createEntityFromDTO(InsertProductDTO dto) {
        Product product = new Product();
        product.setName(dto.name());
        product.setImage(dto.image());
        product.setPrice(dto.price());
        product.setManufacturer(dto.manufacturer());
        product.setStockQuantity(dto.stockQuantity());
        return product;
    }

    @Override
    public Product createEntityFromModel(ProductModel model) {
        Product product = new Product();
        product.setId(model.getId());
        product.setName(model.getName());
        product.setImage(model.getImage());
        product.setPrice(model.getPrice());
        product.setManufacturer(model.getManufacturer());
        product.setStockQuantity(model.getStockQuantity());
        product.setEntryDate(model.getEntryDate());
        return product;
    }

    @Override
    public ProductModel createModelFromEntity(Product entity) {
        ProductModel model = new ProductModel();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setImage(entity.getImage());
        model.setPrice(entity.getPrice());
        model.setManufacturer(entity.getManufacturer());
        model.setStockQuantity(entity.getStockQuantity());
        return model;
    }
}
