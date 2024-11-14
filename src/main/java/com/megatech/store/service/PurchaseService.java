package com.megatech.store.service;

import com.megatech.store.domain.Customer;
import com.megatech.store.domain.Product;
import com.megatech.store.domain.Purchase;
import com.megatech.store.dtos.purchase.CartItemDTO;
import com.megatech.store.dtos.purchase.InsertPurchaseDTO;
import com.megatech.store.dtos.purchase.PurchaseDTO;
import com.megatech.store.exceptions.EntityNotFoundException;
import com.megatech.store.factory.PurchaseFactory;
import com.megatech.store.model.*;
import com.megatech.store.projections.TotalValueSoldPerProduct;
import com.megatech.store.repository.PurchaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseDependencyService purchaseDependencyService;
    private final PurchaseFactory purchaseFactory;

    public PurchaseService(PurchaseRepository purchaseRepository,
                           PurchaseDependencyService purchaseDependencyService,
                           PurchaseFactory purchaseFactory) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseDependencyService = purchaseDependencyService;
        this.purchaseFactory = purchaseFactory;
    }

    @Transactional
    public PurchaseDTO insertPurchase(InsertPurchaseDTO insertPurchaseDTO) {
        Customer savedCustomer = purchaseDependencyService.fetchCustomer(insertPurchaseDTO.customerId());
        List<Product> products = purchaseDependencyService.fetchProducts(insertPurchaseDTO);
        Map<Product, Integer> groupQuantityByProduct =
                insertPurchaseDTO
                .products()
                .stream()

                .collect(Collectors.toMap((cartItemDTO) -> products
                        .stream()
                        .filter(product -> cartItemDTO.productId().equals(product.getId()))
                        .findFirst().orElseThrow(() -> new EntityNotFoundException("Product with id " + cartItemDTO.productId() + " not found")), CartItemDTO::quantity));

        Purchase purchase = purchaseFactory.createEntityFromCustomerAndProductMapping(savedCustomer, groupQuantityByProduct);

        PurchaseModel purchaseModel = purchaseFactory.createModelFromEntity(purchase);

        PurchaseModel savedPurchaseModel = persistEntityAndChildren(purchaseModel);

        return purchaseFactory.createDTOFromModel(savedPurchaseModel);
    }

    public List<PurchaseDTO> findByCustomerId(Long customerId) {
        purchaseDependencyService.fetchCustomer(customerId);
        List<PurchaseModel> purchasesByCustomer = purchaseRepository.findAllByCustomerId(customerId);
        return purchasesByCustomer.stream().map(purchaseFactory::createDTOFromModel).toList();
    }


    public PurchaseModel persistEntityAndChildren(PurchaseModel purchaseModel) {
        PurchaseModel savedPurchaseModel = purchaseRepository.save(purchaseModel);
        savedPurchaseModel.getMappingProductQuantity()
                .forEach(mappingProductQuantity -> {
                    purchaseRepository.insertMappingProductsQuantities(savedPurchaseModel.getId(), mappingProductQuantity.getProduct().getId(), mappingProductQuantity.getQuantity());
                    purchaseDependencyService
                        .decreaseStockQuantity(mappingProductQuantity);
                });
        return savedPurchaseModel;
    }

    public List<TotalValueSoldPerProduct> getTotalValueSoldPerProduct(){
        return purchaseRepository.getTotalValueSoldPerProduct();
    }

}