package com.megatech.store.service;

import com.megatech.store.domain.Product;
import com.megatech.store.dtos.products.DetailedProductDTO;
import com.megatech.store.dtos.products.DisplayProductDTO;
import com.megatech.store.dtos.products.InsertProductDTO;
import com.megatech.store.dtos.products.UpdateProductDTO;
import com.megatech.store.exceptions.EntityNotFoundException;
import com.megatech.store.exceptions.ErrorType;
import com.megatech.store.exceptions.FieldConstraintViolationException;
import com.megatech.store.factory.EntityModelFactory;
import com.megatech.store.model.ProductModel;
import com.megatech.store.projections.TotalValueInStockPerProduct;
import com.megatech.store.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {


    private final ProductRepository productRepository;
    private final EntityModelFactory<Product, ProductModel, InsertProductDTO> productFactory;

    public ProductService(ProductRepository productRepository, EntityModelFactory<Product, ProductModel, InsertProductDTO> productFactory) {
        this.productRepository = productRepository;
        this.productFactory = productFactory;
    }

    public List<DisplayProductDTO> findAll() {
        List<ProductModel> productModels = productRepository.findAll();
        return productModels.stream().map(DisplayProductDTO::new).toList();
    }

    public DetailedProductDTO findById(Long id) {
        Optional<DetailedProductDTO> product = productRepository.findById(id).map(DetailedProductDTO::new);
        return product.orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found", ErrorType.PRODUCT_NOT_FOUND));
    }

    public void validateIfNameNotExists(String name) {
        if (productRepository.existsByName(name))
            throw new FieldConstraintViolationException("Product with name " + name + " is invalid", ErrorType.PRODUCT_ALREADY_EXISTS);
    }

    public void validateIfImageIsUsed(String imageName) {
        if (productRepository.existsByImage(imageName))
            throw new FieldConstraintViolationException("Product with image " + imageName + " is invalid", ErrorType.IMAGE_ALREADY_EXISTS);
    }

    @Transactional
    public DetailedProductDTO save(InsertProductDTO productDTO) {
        validateIfNameNotExists(productDTO.name());
        validateIfImageIsUsed(productDTO.image());
        Product product = productFactory.createEntityFromDTO(productDTO);
        return new DetailedProductDTO(productRepository.save(productFactory.createModelFromEntity(product)));
    }

    @Transactional
    public DetailedProductDTO update(UpdateProductDTO productDTO, Long id) {
        Product savedProduct = productFactory.createEntityFromModel(productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found", ErrorType.PRODUCT_NOT_FOUND)));

        Product beforeUpdateProduct = savedProduct.clone();
        savedProduct.update(productDTO);
        applyNecessaryValidations(beforeUpdateProduct, savedProduct);

        ProductModel updatedProductModel = productFactory.createModelFromEntity(savedProduct);
        updatedProductModel.setId(id);
        updatedProductModel.setEntryDate(savedProduct.getEntryDate());
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

    @Transactional
    public void delete(Long id){
        if (!productRepository.existsById(id)) throw new EntityNotFoundException("Product with id " + id + " not found", ErrorType.PRODUCT_NOT_FOUND);
        productRepository.deleteById(id);
    }

    public List<TotalValueInStockPerProduct> getTotalValueInStockPerProduct() {
        return productRepository.getTotalValueInStockPerProduct();
    }
}
