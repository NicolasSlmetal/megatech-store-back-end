package com.megatech.store.service;

import com.megatech.store.domain.Customer;
import com.megatech.store.domain.Product;
import com.megatech.store.domain.ProductQuantityMapping;
import com.megatech.store.domain.Purchase;
import com.megatech.store.dtos.products.DisplayProductDTO;
import com.megatech.store.dtos.purchase.CartItemDTO;
import com.megatech.store.dtos.purchase.InsertPurchaseDTO;
import com.megatech.store.dtos.purchase.ProductQuantityDTO;
import com.megatech.store.dtos.purchase.PurchaseDTO;
import com.megatech.store.exceptions.EntityNotFoundException;
import com.megatech.store.exceptions.ErrorType;
import com.megatech.store.exceptions.InvalidPurchaseFieldException;
import com.megatech.store.factory.PurchaseFactory;
import com.megatech.store.model.*;
import com.megatech.store.projections.TotalValueSoldPerProduct;
import com.megatech.store.repository.PurchaseRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ActiveProfiles("test")
public class PurchaseServiceTest {

    public static final long ID = 1L;
    public static final LocalDateTime DATE = LocalDateTime.of(2020, 1, 1, 1, 1);
    public static final double TOTAL_VALUE = 1.00;
    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private PurchaseDependencyService purchaseDependencyService;

    @Mock
    private PurchaseFactory purchaseFactory;

    private PurchaseService standAloneSpy;

    @InjectMocks
    private PurchaseService purchaseService;

    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
        standAloneSpy = spy(purchaseService);
    }

    private Customer createCustomer() {
        return mock(Customer.class);
    }

    private Product createProduct() {
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(ID);
        return product;
    }

    private Purchase createPurchase() {
        Purchase purchase = mock(Purchase.class);
        when(purchase.getId()).thenReturn(ID);
        when(purchase.getDate()).thenReturn(DATE);
        when(purchase.getTotalValue()).thenReturn(TOTAL_VALUE);
        when(purchase.getCustomer()).thenReturn(mock(Customer.class));
        when(purchase.getProductQuantityMappings()).thenReturn(Set.of(mock(ProductQuantityMapping.class)));
        return purchase;
    }

    private ProductQuantityMappingModel createProductQuantityModel() {
        ProductQuantityMappingModel productQuantityMappingModel = mock(ProductQuantityMappingModel.class);
        when(productQuantityMappingModel.getCompositeKey()).thenReturn(mock(ProductPurchaseCompositeKey.class));
        when(productQuantityMappingModel.getProduct()).thenReturn(mock(ProductModel.class));
        when(productQuantityMappingModel.getQuantity()).thenReturn(2);
        return productQuantityMappingModel;
    }

    private PurchaseModel createPurchaseModel() {
        ProductQuantityMappingModel productQuantityMappingModel = createProductQuantityModel();
        PurchaseModel purchaseModel = mock(PurchaseModel.class);
        when(purchaseModel.getId()).thenReturn(ID);
        when(purchaseModel.getDate()).thenReturn(DATE);
        when(purchaseModel.getTotalValue()).thenReturn(TOTAL_VALUE);
        when(purchaseModel.getCustomer()).thenReturn(mock(CustomerModel.class));
        when(purchaseModel.getMappingProductQuantity()).thenReturn(Set.of(productQuantityMappingModel));
        return purchaseModel;
    }

    private Set<ProductQuantityDTO> createProductQuantityMappings() {
        Set<ProductQuantityDTO> productQuantityMappings = new HashSet<>();
        productQuantityMappings
                .add(new ProductQuantityDTO(new DisplayProductDTO(1L, "name", "image", 0.5), 2));
        return productQuantityMappings;
    }

    private PurchaseDTO createPurchaseDTO() {
        return new PurchaseDTO(createProductQuantityMappings(), "name", DATE, TOTAL_VALUE);
    }

    private InsertPurchaseDTO createInsertPurchaseDTO() {
        return new InsertPurchaseDTO(Set.of(new CartItemDTO(2, 1L)));
    }

    private TotalValueSoldPerProduct createTotalValueSoldPerProduct() {
        return new TotalValueSoldPerProduct() {
            @Override
            public Long getProductId() {
                return ID;
            }

            @Override
            public String getProductName() {
                return "name";
            }

            @Override
            public Double getTotalValue() {
                return 1.0;
            }

            @Override
            public Integer getQuantitySold() {
                return 2;
            }
        };
    }

    private void setupMocksForInsertPurchase(Customer customer, InsertPurchaseDTO insertPurchaseDTO, Product product, Purchase purchase, PurchaseModel purchaseModel, PurchaseDTO expected) {
        when(purchaseDependencyService.fetchCustomer(ID)).thenReturn(customer);
        when(purchaseDependencyService.fetchProducts(insertPurchaseDTO)).thenReturn(List.of(product));
        when(purchaseFactory.createEntityFromCustomerAndProductMapping(any(), anyMap())).thenReturn(purchase);
        when(purchaseFactory.createModelFromEntity(purchase)).thenReturn(purchaseModel);
        doReturn(purchaseModel).when(standAloneSpy).persistEntityAndChildren(purchaseModel);
        when(purchaseFactory.createDTOFromModel(purchaseModel)).thenReturn(expected);
    }

    @Test
    @DisplayName("Should make a purchase")
    public void testShouldMakeAPurchase() {
        Customer customer = createCustomer();
        Product product = createProduct();
        PurchaseModel purchaseModel = createPurchaseModel();
        Purchase purchase = createPurchase();
        InsertPurchaseDTO insertPurchaseDTO = createInsertPurchaseDTO();
        PurchaseDTO expected = createPurchaseDTO();

        setupMocksForInsertPurchase(customer, insertPurchaseDTO, product, purchase, purchaseModel, expected);

        PurchaseDTO result = standAloneSpy.insertPurchase(insertPurchaseDTO, 1L);

        Assertions.assertEquals(expected, result);
        verify(purchaseDependencyService, times(1)).fetchCustomer(1L);
        verify(purchaseDependencyService, times(1)).fetchProducts(insertPurchaseDTO);
        verify(purchaseFactory, times(1)).createEntityFromCustomerAndProductMapping(any(), anyMap());
        verify(purchaseFactory, times(1)).createModelFromEntity(purchase);
        verify(standAloneSpy, times(1)).persistEntityAndChildren(purchaseModel);
        verify(purchaseFactory, times(1)).createDTOFromModel(purchaseModel);
    }

    @Test
    @DisplayName("Should throw an exception when customer doesn't exist")
    public void testShouldThrowAnExceptionWhenCustomerDoesNotExist() {
        InsertPurchaseDTO insertPurchaseDTO = createInsertPurchaseDTO();
        String errorMessage = "Customer not found";

        setupMocksForInsertPurchase(createCustomer(), insertPurchaseDTO, createProduct(), createPurchase(),
                createPurchaseModel(), null);
        when(purchaseDependencyService.fetchCustomer(ID)).thenThrow(new EntityNotFoundException(errorMessage, ErrorType.CUSTOMER_NOT_FOUND));

        Exception ex = Assertions.assertThrows(EntityNotFoundException.class,() ->
                standAloneSpy.insertPurchase(insertPurchaseDTO, ID));

        Assertions.assertEquals(errorMessage, ex.getMessage());
        verify(purchaseDependencyService, times(1)).fetchCustomer(ID);
        verify(purchaseFactory, never()).createEntityFromCustomerAndProductMapping(any(), anyMap());
        verify(purchaseFactory, never()).createModelFromEntity(any());
        verify(standAloneSpy, never()).persistEntityAndChildren(any());
        verify(purchaseFactory, never()).createDTOFromModel(any());
    }

    @Test
    @DisplayName("Should throw an exception when products are not found")
    public void testShouldThrowAnExceptionWhenProductsAreNotFound() {
        Customer customer = createCustomer();
        Product product = createProduct();
        PurchaseModel purchaseModel = createPurchaseModel();
        Purchase purchase = createPurchase();
        InsertPurchaseDTO insertPurchaseDTO = createInsertPurchaseDTO();
        String errorMessage = "Some product was not found";

        setupMocksForInsertPurchase(customer, insertPurchaseDTO, product, purchase, purchaseModel, createPurchaseDTO());
        when(purchaseDependencyService.fetchProducts(insertPurchaseDTO))
                .thenThrow(new EntityNotFoundException(errorMessage, ErrorType.PRODUCT_NOT_FOUND));

        Exception ex = Assertions.assertThrows(EntityNotFoundException.class,() ->
                standAloneSpy.insertPurchase(insertPurchaseDTO, ID));
        Assertions.assertEquals(errorMessage, ex.getMessage());
        verify(purchaseDependencyService, times(1)).fetchCustomer(ID);
        verify(purchaseDependencyService, times(1)).fetchProducts(insertPurchaseDTO);
        verify(purchaseFactory, never()).createModelFromEntity(any());
        verify(standAloneSpy, never()).persistEntityAndChildren(any());
        verify(purchaseFactory, never()).createDTOFromModel(any());
    }

    @Test
    @DisplayName("Should throw an exception when domain validations fails")
    public void testShouldThrowAnExceptionWhenDomainValidationsFails() {
        Customer customer = createCustomer();
        Product product = createProduct();
        PurchaseModel purchaseModel = createPurchaseModel();
        Purchase purchase = createPurchase();
        InsertPurchaseDTO insertPurchaseDTO = createInsertPurchaseDTO();
        String errorMessage = "Some invalid field was detected";

        setupMocksForInsertPurchase(customer, insertPurchaseDTO, product, purchase, purchaseModel, createPurchaseDTO());
        when(purchaseFactory.createEntityFromCustomerAndProductMapping(any(), anyMap()))
                .thenThrow(new InvalidPurchaseFieldException(errorMessage, null));

        Exception ex = Assertions.assertThrows(InvalidPurchaseFieldException.class,
                () -> standAloneSpy.insertPurchase(insertPurchaseDTO, ID));
        Assertions.assertEquals(errorMessage, ex.getMessage());
        verify(purchaseDependencyService, times(1)).fetchCustomer(ID);
        verify(purchaseDependencyService, times(1)).fetchProducts(insertPurchaseDTO);
        verify(purchaseFactory, times(1))
                .createEntityFromCustomerAndProductMapping(any(), anyMap());
        verify(purchaseFactory, never()).createModelFromEntity(purchase);
        verify(standAloneSpy, never()).persistEntityAndChildren(any());
        verify(purchaseFactory, never()).createDTOFromModel(any());
    }

    @Test
    @DisplayName("Should persist purchases and products mappings and decrease stock quantity")
    public void testShouldPersistPurchasesAndProductsMappingsAndDecreaseStockQuantity() {
        PurchaseModel expected = createPurchaseModel();

        when(purchaseRepository.save(expected)).thenReturn(expected);
        doNothing().when(purchaseRepository).insertMappingProductsQuantities(any(), any(), any());
        doNothing().when(purchaseDependencyService).decreaseStockQuantity(any());

        PurchaseModel result = purchaseService.persistEntityAndChildren(expected);

        Assertions.assertEquals(expected, result);
        verify(purchaseRepository, times(1)).save(expected);
        verify(purchaseRepository, times(1)).insertMappingProductsQuantities(any(), any(), any());
        verify(purchaseDependencyService, times(1)).decreaseStockQuantity(any());
    }

    @Test
    @DisplayName("Should return a list of purchases by a customer")
    public void testShouldReturnAListOfPurchasesByCustomer() {
        Customer customer = createCustomer();
        PurchaseModel purchaseModel = createPurchaseModel();
        PurchaseDTO purchaseDTO = createPurchaseDTO();
        List<PurchaseDTO> expected = List.of(purchaseDTO);

        when(purchaseDependencyService.fetchCustomer(ID)).thenReturn(customer);
        when(purchaseRepository.findAllByCustomerId(ID)).thenReturn(List.of(purchaseModel));
        when(purchaseFactory.createDTOFromModel(purchaseModel)).thenReturn(purchaseDTO);

        List<PurchaseDTO> result = purchaseService.findByCustomerId(ID);

        Assertions.assertEquals(expected, result);
        verify(purchaseDependencyService, times(1)).fetchCustomer(ID);
        verify(purchaseRepository, times(1)).findAllByCustomerId(ID);
        verify(purchaseFactory, times(1)).createDTOFromModel(purchaseModel);
    }

    @Test
    @DisplayName("Should throw an exception when customer doesn't exist")
    public void testShouldThrowAnExceptionWhenCustomerDoesntExist() {
        PurchaseModel purchaseModel = createPurchaseModel();
        PurchaseDTO purchaseDTO = createPurchaseDTO();
        String errorMessage = "Customer not found";

        when(purchaseDependencyService.fetchCustomer(ID))
                .thenThrow(new EntityNotFoundException(errorMessage, ErrorType.CUSTOMER_NOT_FOUND));
        when(purchaseRepository.findAllByCustomerId(ID)).thenReturn(List.of(purchaseModel));
        when(purchaseFactory.createDTOFromModel(purchaseModel)).thenReturn(purchaseDTO);

        Exception ex = Assertions.assertThrows(EntityNotFoundException.class,
                () -> purchaseService.findByCustomerId(ID));

        Assertions.assertEquals(errorMessage, ex.getMessage());
        verify(purchaseDependencyService, times(1)).fetchCustomer(ID);
        verify(purchaseRepository, never()).findAllByCustomerId(ID);
        verify(purchaseFactory, never()).createDTOFromModel(purchaseModel);
    }

    @Test
    @DisplayName("Should return a projection of total value sold grouping per product")
    public void testShouldReturnAProjectionOfTotalValueSoldGroupingPerProduct() {
        List<TotalValueSoldPerProduct> expected = List.of(createTotalValueSoldPerProduct());
        when(purchaseRepository.getTotalValueSoldPerProduct()).thenReturn(expected);

        List<TotalValueSoldPerProduct> result = purchaseService.getTotalValueSoldPerProduct();
        Assertions.assertEquals(expected, result);
        verify(purchaseRepository, times(1)).getTotalValueSoldPerProduct();
    }

}
