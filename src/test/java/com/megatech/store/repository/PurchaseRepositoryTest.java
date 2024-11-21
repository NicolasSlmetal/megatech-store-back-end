package com.megatech.store.repository;

import com.megatech.store.model.*;
import com.megatech.store.projections.TotalValueSoldPerProduct;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
@AllArgsConstructor
class TotalValueSoldTest implements TotalValueSoldPerProduct {

    private Long productId;
    private String productName;
    private Double totalValue;
    private Integer quantitySold;

    @Override
    public Long getProductId() {
        return productId;
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public Double getTotalValue() {
        return totalValue;
    }

    @Override
    public Integer getQuantitySold() {
        return quantitySold;
    }
}

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PurchaseRepositoryTest {

    public static final int STOCK_QUANTITY = 100;
    public static final double PRICE = 1.00;
    public static final String PRODUCT_NAME = "TestProduct";
    public static final int QUANTITY_SOLD = 1;
    public static final long ID = 1L;
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    public void insertDefaultData() {
        ProductModel productModel = new ProductModel();
        productModel.setName(PRODUCT_NAME);
        productModel.setStockQuantity(STOCK_QUANTITY);
        productModel.setPrice(PRICE);
        ProductModel persistedProduct = entityManager.persist(productModel);
        UserModel userModel = new UserModel();

        CustomerModel customerModel = new CustomerModel();
        customerModel.setUser(entityManager.persist(userModel));
        CustomerModel persistedCustomerModel = entityManager.persistAndFlush(customerModel);
        PurchaseModel purchaseModel = new PurchaseModel();
        purchaseModel.setCustomer(persistedCustomerModel);
        purchaseModel.setTotalValue(PRICE * QUANTITY_SOLD);
        ProductQuantityMappingModel productQuantityMappingModel = new ProductQuantityMappingModel();
        productQuantityMappingModel.setProduct(persistedProduct);
        productQuantityMappingModel.setCompositeKey(new ProductPurchaseCompositeKey());
        productQuantityMappingModel.getCompositeKey().setProductId(persistedProduct.getId());
        productQuantityMappingModel.setQuantity(QUANTITY_SOLD);
        productQuantityMappingModel.setPurchase(purchaseModel);
        purchaseModel.setMappingProductQuantity(Set.of(productQuantityMappingModel));
        entityManager.persist(purchaseModel);
    }

    @Test
    @DisplayName("Should insert product quantity model in intermediate table")
    public void testShouldInsertProductQuantityModelInIntermediateTable() {
        //Arrange
        PurchaseModel purchaseModel = entityManager.find(PurchaseModel.class, ID);

        ProductQuantityMappingModel mapping = purchaseModel.getMappingProductQuantity().stream().toList().getFirst();
        mapping.getCompositeKey().setPurchaseId(purchaseModel.getId());

        //Act
        purchaseRepository.insertMappingProductsQuantities(purchaseModel.getId(), mapping.getProduct().getId(), mapping.getQuantity());
        ProductQuantityMappingModel result = entityManager.find(ProductQuantityMappingModel.class, mapping.getCompositeKey());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(QUANTITY_SOLD, result.getQuantity());
        Assertions.assertEquals(purchaseModel.getId(), result.getPurchase().getId());
        Assertions.assertEquals(purchaseModel.getId(), result.getCompositeKey().getPurchaseId());
        Assertions.assertEquals(ID, result.getProduct().getId());
    }

    @Test
    @DisplayName("Should throw an error when passing a non existing foreign key")
    public void testShouldThrowAnErrorWhenPassingNonExistingForeignKey() {

        Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> purchaseRepository.insertMappingProductsQuantities(null, null, null));
    }

    @Test
    @DisplayName("Should return a projection with the total value by product")
    public void testShouldReturnProjectionWithTotalValueByProduct() {
        PurchaseModel persistedPurchase = entityManager.find(PurchaseModel.class, ID);
        persistedPurchase.getMappingProductQuantity().forEach(
                prePersist -> {
                    prePersist.getCompositeKey().setPurchaseId(persistedPurchase.getId());
                    prePersist.setPurchase(persistedPurchase);
                    purchaseRepository.insertMappingProductsQuantities(persistedPurchase.getId(), prePersist.getProduct().getId(), prePersist.getQuantity());
                }
        );

        TotalValueSoldTest expected = new TotalValueSoldTest(ID, PRODUCT_NAME, PRICE, QUANTITY_SOLD);

        List<TotalValueSoldPerProduct> result = purchaseRepository.getTotalValueSoldPerProduct();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        TotalValueSoldPerProduct totalValueSoldPerProduct = result.getFirst();

        Assertions.assertNotNull(totalValueSoldPerProduct);
        Assertions.assertEquals(expected.getProductId(), totalValueSoldPerProduct.getProductId());
        Assertions.assertEquals(expected.getProductName(), totalValueSoldPerProduct.getProductName());
        Assertions.assertEquals(expected.getTotalValue(), totalValueSoldPerProduct.getTotalValue());
        Assertions.assertEquals(expected.getQuantitySold(), totalValueSoldPerProduct.getQuantitySold());
    }

    @Test
    @DisplayName("Should return all purchases by a customer")
    public void testShouldReturnAllPurchasesByCustomer() {
        PurchaseModel expected = entityManager.find(PurchaseModel.class, ID);

        List<PurchaseModel> result = purchaseRepository.findAllByCustomerId(expected.getCustomer().getId());

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertTrue(result.contains(expected));
    }

    @Test
    @DisplayName("Should return a empty list when customer does not exist")
    public void testShouldReturnEmptyListWhenCustomerDoesNotExist() {

        List<PurchaseModel> result = purchaseRepository.findAllByCustomerId(ID + 1);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }
}
