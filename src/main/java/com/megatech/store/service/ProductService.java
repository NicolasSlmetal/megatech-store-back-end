package com.megatech.store.service;

import com.megatech.store.domain.Product;
import com.megatech.store.dtos.products.InsertProductDTO;
import com.megatech.store.dtos.products.UpdateProductDTO;
import com.megatech.store.exceptions.EntityNotFoundException;
import com.megatech.store.model.ProductModel;
import com.megatech.store.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        List<ProductModel> productModels = productRepository.findAll();
        return productModels.stream().map(Product::new).toList();
    }

    public Product findById(Long id) {
        Optional<Product> product = productRepository.findById(id).map(Product::new);
        return product.orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found"));
    }

    public Product save(InsertProductDTO productDTO) {
        Product product = new Product(productDTO);
        return new Product(productRepository.save(new ProductModel(product)));
    }

    public Product update(UpdateProductDTO productDTO, Long id) {
        Product savedProduct = new Product(productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found")));
        savedProduct.update(productDTO);
        ProductModel updatedProductModel = new ProductModel(savedProduct);
        updatedProductModel.setId(id);
        return new Product(productRepository.save(updatedProductModel));
    }

    public void delete(Long id){
        if (!productRepository.existsById(id)) throw new EntityNotFoundException("Product with id " + id + " not found");
        productRepository.deleteById(id);
    }
}
