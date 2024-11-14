package com.megatech.store.factory;

import com.megatech.store.domain.Customer;
import com.megatech.store.domain.ProductQuantityMapping;
import com.megatech.store.domain.Product;
import com.megatech.store.domain.Purchase;
import com.megatech.store.dtos.customer.InsertCustomerDTO;
import com.megatech.store.dtos.products.DisplayProductDTO;
import com.megatech.store.dtos.products.InsertProductDTO;
import com.megatech.store.dtos.purchase.ProductQuantityDTO;
import com.megatech.store.dtos.purchase.PurchaseDTO;
import com.megatech.store.model.CustomerModel;
import com.megatech.store.model.ProductQuantityMappingModel;
import com.megatech.store.model.ProductModel;
import com.megatech.store.model.PurchaseModel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PurchaseFactory {

    private final EntityModelFactory<Product, ProductModel, InsertProductDTO> productFactory;
    private final EntityModelFactory<Customer, CustomerModel, InsertCustomerDTO> customerFactory;

    public PurchaseFactory(EntityModelFactory<Product, ProductModel, InsertProductDTO> productFactory, EntityModelFactory<Customer, CustomerModel, InsertCustomerDTO> customerFactory) {
        this.productFactory = productFactory;
        this.customerFactory = customerFactory;
    }

    public Purchase createEntityFromCustomerAndProductMapping(Customer customer, Map<Product, Integer> mappingProductsQuantities) {
        Purchase purchase = new Purchase();
        purchase.setCustomer(customer);
        mappingProductsQuantities.keySet()
                .stream()
                .map(product -> new ProductQuantityMapping(product, mappingProductsQuantities.get(product)))
                .forEach(purchase::addProductQuantityMapping);
        return purchase;
    }

    public PurchaseModel createModelFromEntity(Purchase purchase) {
        PurchaseModel purchaseModel = new PurchaseModel();
        if (purchase.getId() != null) {
            purchaseModel.setId(purchase.getId());
        }
        if (purchase.getDate() != null) {
            purchaseModel.setDate(purchase.getDate());
        }
        purchaseModel.setCustomer(customerFactory.createModelFromEntity(purchase.getCustomer()));
        purchaseModel
                .setMappingProductQuantity(purchase.getProductQuantityMappings().stream().map(mappingProductQuantity -> {
                    ProductQuantityMappingModel model = new ProductQuantityMappingModel();
                    model.getCompositeKey().setProductId(mappingProductQuantity.getProduct().getId());
                    model.setPurchase(purchaseModel);
                    model.setQuantity(mappingProductQuantity.getQuantity());
                    model.setProduct(productFactory.createModelFromEntity(mappingProductQuantity.getProduct()));
                    return model;
                }).collect(Collectors.toSet()));
        return purchaseModel;
    }

    public Purchase createEntityFromModel(PurchaseModel purchaseModel) {
        Purchase purchase = new Purchase();
        purchase.setId(purchaseModel.getId());
        purchase.setDate(purchaseModel.getDate());
        purchase.setCustomer(customerFactory.createEntityFromModel(purchaseModel.getCustomer()));
        purchaseModel
                .getMappingProductQuantity().forEach(productQuantityModel ->
                    purchase.addProductQuantityMapping(new ProductQuantityMapping(productFactory.createEntityFromModel(productQuantityModel.getProduct()),
                            productQuantityModel.getQuantity()))
                );
        return purchase;
    }

    public PurchaseDTO createDTOFromModel(PurchaseModel purchaseModel) {
        Set<ProductQuantityDTO> productQuantityDTOS = purchaseModel
                .getMappingProductQuantity()
                .stream()
                .map(mappingProductQuantityModel ->
                        new ProductQuantityDTO(new DisplayProductDTO(mappingProductQuantityModel.getProduct()), mappingProductQuantityModel.getQuantity())).collect(Collectors.toSet());
        return new PurchaseDTO(productQuantityDTOS, purchaseModel.getCustomer().getName(), purchaseModel.getDate());
    }


}