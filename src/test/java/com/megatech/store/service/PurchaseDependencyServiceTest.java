package com.megatech.store.service;

import com.megatech.store.domain.Customer;
import com.megatech.store.domain.Product;
import com.megatech.store.dtos.customer.InsertCustomerDTO;
import com.megatech.store.dtos.products.InsertProductDTO;
import com.megatech.store.dtos.purchase.CartItemDTO;
import com.megatech.store.dtos.purchase.InsertPurchaseDTO;
import com.megatech.store.exceptions.EntityNotFoundException;
import com.megatech.store.factory.CustomerFactory;
import com.megatech.store.factory.EntityModelFactory;
import com.megatech.store.factory.ProductFactory;
import com.megatech.store.model.CustomerModel;
import com.megatech.store.model.ProductModel;
import com.megatech.store.model.ProductQuantityMappingModel;
import com.megatech.store.repository.CustomerRepository;
import com.megatech.store.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;


@ActiveProfiles("test")
public class PurchaseDependencyServiceTest {

    public static final long CUSTOMER_ID = 1L;
    @Mock
    private EntityModelFactory<Customer, CustomerModel, InsertCustomerDTO> customerFactory;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private EntityModelFactory<Product, ProductModel, InsertProductDTO> productFactory = mock(ProductFactory.class);
    @InjectMocks
    private PurchaseDependencyService purchaseDependencyService;

    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() {
        reset(customerFactory, customerRepository);
    }

    @Test
    @DisplayName("Should return a customer by your id")
    public void testShouldReturnCustomerById() {
        Customer expected = new Customer();
        CustomerModel expectedModel = new CustomerModel();
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(expectedModel));
        when(customerFactory.createEntityFromModel(expectedModel)).thenReturn(expected);

        Customer result = purchaseDependencyService.fetchCustomer(CUSTOMER_ID);

        Assertions.assertEquals(expected, result);
        verify(customerFactory, times(1)).createEntityFromModel(expectedModel);
        verify(customerRepository, times(1)).findById(CUSTOMER_ID);
    }

    @Test
    @DisplayName("Should throw an exception when customer does not exists")
    public void testShouldThrowExceptionWhenCustomerDoesNotExist() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());
        when(customerFactory.createEntityFromModel(any())).thenReturn(any());
        String errorMessage = "Customer not found";
        Exception ex = Assertions
                .assertThrows(EntityNotFoundException.class, () -> purchaseDependencyService.fetchCustomer(CUSTOMER_ID));
        Assertions.assertEquals(errorMessage, ex.getMessage());
        verify(customerRepository, times(1)).findById(CUSTOMER_ID);
        verify(customerFactory, never()).createEntityFromModel(any());
    }

    @Test
    @DisplayName("Should return a list of products")
    public void testShouldReturnProducts() {
        Product expectedItem = new Product();
        String productName = "Test Product";
        expectedItem.setName(productName);
        long productId = 1L;
        expectedItem.setId(productId);
        ProductModel expectedModel = new ProductModel();
        expectedModel.setName(productName);
        expectedModel.setId(productId);
        List<Product> expected = List.of(expectedItem);
        InsertPurchaseDTO purchaseDTO = new InsertPurchaseDTO(Set.of(new CartItemDTO(1, 1L)));

        when(productRepository.findAllById(anyList())).thenReturn(List.of(expectedModel));
        when(productFactory.createEntityFromModel(expectedModel)).thenReturn(expectedItem);

        List<Product> result = purchaseDependencyService.fetchProducts(purchaseDTO);
        Assertions.assertFalse(result.isEmpty());
        Product first = result.getFirst();
        Assertions.assertEquals(expected, result);
        Assertions.assertEquals(expectedItem, first);
        verify(productRepository, times(1)).findAllById(anyList());
        verify(productFactory, times(1)).createEntityFromModel(any());
    }

    @Test
    @DisplayName("Should throw an exception when some product does not exist")
    public void testShouldThrowExceptionWhenSomeProductDoesNotExist() {
        InsertPurchaseDTO purchaseDTO = new InsertPurchaseDTO(Set.of(new CartItemDTO(1, 1L)));
        String errorMessage = "Some product was not found";
        when(productRepository.findAllById(anyList())).thenReturn(List.of());
        when(productFactory.createEntityFromModel(any())).thenReturn(any());

        Exception ex = Assertions.assertThrows(EntityNotFoundException.class,
                () -> purchaseDependencyService.fetchProducts(purchaseDTO));

        Assertions.assertEquals(errorMessage, ex.getMessage());
        verify(productRepository, times(1)).findAllById(anyList());
        verify(productFactory, never()).createEntityFromModel(any());
    }

    @Test
    @DisplayName("Should call repository method to decrease stock quantity")
    public void testShouldCallRepositoryMethodToDecreaseStock() {
        ProductQuantityMappingModel productQuantityMappingModel = mock(ProductQuantityMappingModel.class);
        ProductModel productModel = mock(ProductModel.class);
        when(productQuantityMappingModel.getProduct()).thenReturn(productModel);
        when(productModel.getId()).thenReturn(1L);
        doNothing().when(productRepository).decreaseStockQuantity(any(), anyLong());

        purchaseDependencyService.decreaseStockQuantity(productQuantityMappingModel);

        verify(productRepository, times(1)).decreaseStockQuantity(any(), anyLong());
    }
}
