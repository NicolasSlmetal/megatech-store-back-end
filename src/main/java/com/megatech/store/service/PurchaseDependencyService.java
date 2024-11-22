package com.megatech.store.service;

import com.megatech.store.domain.Customer;
import com.megatech.store.domain.Product;
import com.megatech.store.dtos.customer.InsertCustomerDTO;
import com.megatech.store.dtos.products.InsertProductDTO;
import com.megatech.store.dtos.purchase.CartItemDTO;
import com.megatech.store.dtos.purchase.InsertPurchaseDTO;
import com.megatech.store.exceptions.EntityNotFoundException;
import com.megatech.store.exceptions.ErrorType;
import com.megatech.store.factory.EntityModelFactory;
import com.megatech.store.model.CustomerModel;
import com.megatech.store.model.ProductQuantityMappingModel;
import com.megatech.store.model.ProductModel;
import com.megatech.store.repository.CustomerRepository;
import com.megatech.store.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseDependencyService {

    @Autowired
    private  EntityModelFactory<Customer, CustomerModel, InsertCustomerDTO> customerFactory;
    @Autowired
    private  CustomerRepository customerRepository;
    @Autowired
    private  EntityModelFactory<Product, ProductModel, InsertProductDTO> productFactory;
    @Autowired
    private  ProductRepository productRepository;


    public Customer fetchCustomer(Long customerId) {
        return customerFactory
                .createEntityFromModel(customerRepository.findById(customerId)
                        .orElseThrow(() -> new EntityNotFoundException("Customer not found", ErrorType.CUSTOMER_NOT_FOUND)));
    }

    public List<Product> fetchProducts(InsertPurchaseDTO insertPurchaseDTO) {
        List<Long> productsIds = insertPurchaseDTO.products().stream().map(CartItemDTO::productId).toList();

        List<Product> products = productRepository
                .findAllById(productsIds).stream()
                .map(productFactory::createEntityFromModel).toList();

        if (products.isEmpty() || products.size() != productsIds.size()) {
            throw new EntityNotFoundException("Some product was not found", ErrorType.PRODUCT_NOT_FOUND);
        }

        return products;
    }

    public void decreaseStockQuantity(ProductQuantityMappingModel mappingProductQuantityModel) {
        productRepository
                .decreaseStockQuantity(mappingProductQuantityModel.getQuantity(), mappingProductQuantityModel.getProduct().getId());
    }

}
