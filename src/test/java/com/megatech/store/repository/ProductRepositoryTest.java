package com.megatech.store.repository;

import com.megatech.store.model.ProductModel;
import com.megatech.store.projections.TotalValueInStockPerProduct;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
class TotalValueInStockTest implements TotalValueInStockPerProduct {
    private Long productId;
    private String productName;
    private Double price;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TotalValueInStockTest that)) return false;
        return Objects.equals(productId, that.productId) && Objects.equals(productName, that.productName) && Objects.equals(price, that.price) && Objects.equals(entryDate, that.entryDate) && Objects.equals(quantity, that.quantity) && Objects.equals(totalValue, that.totalValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName, price, entryDate, quantity, totalValue);
    }

    private LocalDateTime entryDate;
    private Integer quantity;
    private Double totalValue;

    @Override
    public Long getProductId() {
        return productId;
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public Double getProductPrice() {
        return price;
    }

    @Override
    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    @Override
    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public Double getTotalValue() {
        return totalValue;
    }
}

@ActiveProfiles("test")
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductRepositoryTest {

    public static final String NAME = "product";
    public static final double PRICE = 1.00;
    public static final String IMAGE = "image";
    public static final String MANUFACTURER = "manufacturer";
    public static final int STOCK_QUANTITY = 10;
    public static final String NAME2 = "name2";
    public static final String IMAGE2 = "image2";
    public static final long FIRST_ID = 1L;
    public static final long SECOND_ID = 2L;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    public void insertDefaultData() {
        ProductModel product = new ProductModel();
        product.setName(NAME);
        product.setPrice(PRICE);
        product.setImage(IMAGE);
        product.setManufacturer(MANUFACTURER);
        product.setStockQuantity(STOCK_QUANTITY);
        entityManager.persist(product);

        ProductModel product2 = new ProductModel();
        product2.setName(NAME2);
        product2.setPrice(PRICE);
        product2.setImage(IMAGE2);
        product2.setManufacturer(MANUFACTURER);
        product2.setStockQuantity(0);
        entityManager.persist(product2);
        entityManager.flush();
        entityManager.refresh(product2);
        entityManager.refresh(product);
    }

    @Test
    @DisplayName("Should return true when an existing name is provided")
    public void shouldReturnTrueWhenExistingNameIsProvided() {
        insertDefaultData();

        boolean result = productRepository.existsByName(NAME);

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when is provided a non existing name")
    public void shouldReturnFalseWhenNonExistingNameIsProvided() {
        insertDefaultData();

        boolean result = productRepository.existsByName("NewName");

        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Should return true when an existing image is provided")
    public void shouldReturnTrueWhenExistingImageIsProvided() {
        insertDefaultData();

        boolean result = productRepository.existsByImage(IMAGE);

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when is provided a non existing image")
    public void shouldReturnFalseWhenNonExistingImageIsProvided() {
        insertDefaultData();

        boolean result = productRepository.existsByImage("newImage");

        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Should return all products with positive stock")
    public void shouldReturnAllProductsWithPositiveStock() {
        insertDefaultData();
        ProductModel expected = entityManager.find(ProductModel.class, FIRST_ID);

        List<ProductModel> result = productRepository.findAllByStockQuantityGreaterThanZero();

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
        ProductModel product = result.getFirst();
        Assertions.assertNotNull(product);
        Assertions.assertEquals(expected, product);
    }

    @Test
    @DisplayName("Should return all products with empty stock")
    public void shouldReturnAllProductsWithEmptyStock() {
        insertDefaultData();
        ProductModel expected = entityManager.find(ProductModel.class, SECOND_ID);

        List<ProductModel> result = productRepository.findAllByStockQuantityEqualZero();

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
        ProductModel product = result.getFirst();
        Assertions.assertNotNull(product);
        Assertions.assertEquals(expected, product);
    }

    @Test()
    @DisplayName("Should empty the stock quantity of a product")
    @Transactional
    public void testShouldEmptyStockOfAProduct() {
        insertDefaultData();

        productRepository.emptyStockQuantity(FIRST_ID);
        ProductModel product = entityManager.find(ProductModel.class, FIRST_ID);
        entityManager.refresh(product);

        Assertions.assertNotNull(product);
        Assertions.assertEquals(0, product.getStockQuantity());
    }

    @Test
    @DisplayName("Should return a projections about the stock")
    public void testShouldReturnTwoProjections() {
        insertDefaultData();
        ProductModel firstProduct = entityManager.find(ProductModel.class, FIRST_ID);
        TotalValueInStockPerProduct expected =
                new TotalValueInStockTest(firstProduct.getId(), firstProduct.getName(), firstProduct.getPrice(), firstProduct.getEntryDate(), firstProduct.getStockQuantity(), firstProduct.getPrice() * firstProduct.getStockQuantity());

        List<TotalValueInStockPerProduct> result = productRepository.getTotalValueInStockPerProduct();
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
        TotalValueInStockPerProduct firstItemResult = result.getFirst();
        Assertions.assertNotNull(firstItemResult);
        Assertions.assertEquals(expected.getProductId(), firstItemResult.getProductId());
        Assertions.assertEquals(expected.getEntryDate(), firstItemResult.getEntryDate());
        Assertions.assertEquals(expected.getTotalValue(), firstItemResult.getTotalValue());
        Assertions.assertEquals(expected.getProductName(), firstItemResult.getProductName());
        Assertions.assertEquals(expected.getQuantity(), firstItemResult.getQuantity());
    }

    @Test
    @DisplayName("Should decrease stock quantity")
    public void testShouldDecreaseStockQuantity() {
        insertDefaultData();
        int quantityToReduce = 2;

        productRepository.decreaseStockQuantity(quantityToReduce, FIRST_ID);
        ProductModel product = entityManager.find(ProductModel.class, FIRST_ID);
        entityManager.refresh(product);

        Assertions.assertNotNull(product);
        Assertions.assertEquals(STOCK_QUANTITY - quantityToReduce, product.getStockQuantity());
    }

}
