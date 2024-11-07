package com.megatech.store.service;

import com.megatech.store.domain.Product;
import com.megatech.store.dtos.products.DetailedProductDTO;
import com.megatech.store.dtos.products.DisplayProductDTO;
import com.megatech.store.dtos.products.InsertProductDTO;
import com.megatech.store.dtos.products.UpdateProductDTO;
import com.megatech.store.exceptions.EntityNotFoundException;
import com.megatech.store.exceptions.FieldConstraintViolationException;
import com.megatech.store.model.ProductModel;
import com.megatech.store.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class ProductService {


    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<DisplayProductDTO> findAll() {
        List<ProductModel> productModels = productRepository.findAll();
        return productModels.stream().map(DisplayProductDTO::new).toList();
    }

    public DetailedProductDTO findById(Long id) {
        Optional<DetailedProductDTO> product = productRepository.findById(id).map(DetailedProductDTO::new);
        return product.orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found"));
    }

    public void validateIfNameNotExists(String name) {
        if (productRepository.existsByName(name))
            throw new FieldConstraintViolationException("Product with name " + name + " is invalid");
    }

    public void validateIfImageIsUsed(String imageName) {
        if (productRepository.existsByImage(imageName))
            throw new FieldConstraintViolationException("Product with image " + imageName + " is invalid");
    }

    public DetailedProductDTO save(InsertProductDTO productDTO) {
        validateIfNameNotExists(productDTO.name());
        validateIfImageIsUsed(productDTO.image());
        Product product = new Product(productDTO);
        return new DetailedProductDTO(productRepository.save(new ProductModel(product)));
    }

    public DetailedProductDTO update(UpdateProductDTO productDTO, Long id) {
        Product savedProduct = new Product(productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found")));

        Product beforeUpdateProduct = savedProduct.clone();
        savedProduct.update(productDTO);
        applyNecessaryValidations(beforeUpdateProduct, savedProduct);

        ProductModel updatedProductModel = new ProductModel(savedProduct);
        updatedProductModel.setId(id);
        return new DetailedProductDTO(productRepository.save(updatedProductModel));
    }

    private void applyNecessaryValidations(Product beforeUpdate, Product afterUpdate) {
        if (!beforeUpdate.getName().equals(afterUpdate.getName())) {
            validateIfNameNotExists(afterUpdate.getName());
        }
        if (!beforeUpdate.getImage().equals(afterUpdate.getImage())) {
            validateIfImageIsUsed(afterUpdate.getImage());
        }
    }

    public void delete(Long id){
        if (!productRepository.existsById(id)) throw new EntityNotFoundException("Product with id " + id + " not found");
        productRepository.deleteById(id);
    }
}
