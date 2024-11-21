package com.megatech.store.service;

import com.megatech.store.domain.Product;
import com.megatech.store.dtos.products.DetailedProductDTO;
import com.megatech.store.dtos.products.DisplayProductDTO;
import com.megatech.store.dtos.products.InsertProductDTO;
import com.megatech.store.dtos.products.UpdateProductDTO;
import com.megatech.store.exceptions.EntityNotFoundException;
import com.megatech.store.exceptions.ErrorType;
import com.megatech.store.exceptions.InvalidProductFieldException;
import com.megatech.store.factory.ProductFactory;
import com.megatech.store.model.ProductModel;
import com.megatech.store.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class ProductServiceTest {

    public static final long ID = 1L;
    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final double PRICE = 1.0;
    public static final String MANUFACTURER = "manufacturer";
    public static final int STOCK_QUANTITY = 100;
    @Mock
    private ProductFactory productFactory;
    @Mock
    private ProductRepository productRepository;
    private ProductService standAloneSpy;
    @InjectMocks
    private ProductService productService;

    public DisplayProductDTO createDisplayProductDTO() {
        return new DisplayProductDTO(ID, NAME, IMAGE, PRICE);
    }

    public DetailedProductDTO createDetailedProductDTO() {
        return new DetailedProductDTO(ID, NAME, IMAGE, MANUFACTURER, PRICE, STOCK_QUANTITY);
    }

    private InsertProductDTO createInsertProductDTO() {
        return new InsertProductDTO(NAME, IMAGE, MANUFACTURER, PRICE, STOCK_QUANTITY);
    }

    private UpdateProductDTO createUpdateProductDTO() {
        return new UpdateProductDTO("newName", IMAGE, MANUFACTURER, PRICE, STOCK_QUANTITY);
    }

    public Product createProduct() {
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(ID);
        when(product.getName()).thenReturn(NAME);
        when(product.getImage()).thenReturn(IMAGE);
        when(product.getPrice()).thenReturn(PRICE);
        when(product.getManufacturer()).thenReturn(MANUFACTURER);
        when(product.getStockQuantity()).thenReturn(STOCK_QUANTITY);
        return product;
    }

    public ProductModel createProductModel() {
        ProductModel productModel = mock(ProductModel.class);
        when(productModel.getId()).thenReturn(ID);
        when(productModel.getName()).thenReturn(NAME);
        when(productModel.getImage()).thenReturn(IMAGE);
        when(productModel.getManufacturer()).thenReturn(MANUFACTURER);
        when(productModel.getPrice()).thenReturn(PRICE);
        when(productModel.getStockQuantity()).thenReturn(STOCK_QUANTITY);
        return productModel;
    }

    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
        standAloneSpy = spy(productService);
    }

    @Test
    @DisplayName("Should return a list of products with positive stock quantity")
    public void testShouldReturnAListOfProductsWithPositiveStockQuantity() {
        ProductModel productModel = createProductModel();
        List<DisplayProductDTO> expected = List.of(createDisplayProductDTO());

        when(productRepository.findAllByStockQuantityGreaterThanZero())
                .thenReturn(List.of(productModel));

        List<DisplayProductDTO> actual = productService.findAll();

        Assertions.assertEquals(expected, actual);
        verify(productRepository, times(1)).findAllByStockQuantityGreaterThanZero();
    }

    @Test
    @DisplayName("Should return a list of products with empty stock quantity")
    public void testShouldReturnAListOfProductsWithEmptyStockQuantity() {
        ProductModel productModel = createProductModel();
        List<DetailedProductDTO> expected = List.of(createDetailedProductDTO());

        when(productRepository.findAllByStockQuantityEqualZero()).thenReturn(List.of(productModel));

        List<DetailedProductDTO> actual = productService.findAllWhereStockIsZero();

        Assertions.assertEquals(expected, actual);
        verify(productRepository, times(1)).findAllByStockQuantityEqualZero();
    }

    private void setupMocksForSave(InsertProductDTO insertProductDTO, Product product, ProductModel productModel) {
        doNothing().when(standAloneSpy).validateIfNameNotExists(insertProductDTO.name());
        doNothing().when(standAloneSpy).validateIfImageIsUsed(insertProductDTO.image());
        when(productFactory.createEntityFromDTO(insertProductDTO)).thenReturn(product);
        when(productFactory.createModelFromEntity(product)).thenReturn(productModel);
        when(productRepository.save(productModel)).thenReturn(productModel);
    }

    private void setupMocksForUpdate(ProductModel productModel, Product product, UpdateProductDTO updateProductDTO) {
        when(productRepository.findById(ID)).thenReturn(Optional.of(productModel));
        when(productFactory.createEntityFromModel(productModel)).thenReturn(product);
        when(product.clone()).thenReturn(product);
        doNothing().when(product).update(updateProductDTO);
        doNothing().when(standAloneSpy).applyNecessaryValidations(product, product);
        when(productFactory.createModelFromEntity(product)).thenReturn(productModel);
        when(productRepository.save(productModel)).thenReturn(productModel);
    }

    @Test
    @DisplayName("Should return a product by your id")
    public void testShouldReturnAProductByYourId() {
        ProductModel productModel = createProductModel();
        DetailedProductDTO expected = createDetailedProductDTO();
        when(productRepository.findById(ID)).thenReturn(Optional.of(productModel));

        DetailedProductDTO actual = productService.findById(ID);
        Assertions.assertEquals(expected, actual);
        verify(productRepository, times(1)).findById(ID);
    }

    @Test
    @DisplayName("Should throw an exception when a product is not found")
    public void testShouldThrowAnExceptionWhenAProductIsNotFound() {
        when(productRepository.findById(ID)).thenReturn(Optional.empty());

        Exception ex = Assertions.assertThrows(EntityNotFoundException.class, () -> productService.findById(ID));

        Assertions.assertEquals("Product with id " + ID + " not found", ex.getMessage());
        verify(productRepository, times(1)).findById(ID);
    }

    @Test
    @DisplayName("Should save a product")
    public void testShouldSaveAProduct() {
        ProductModel productModel = createProductModel();
        Product product = createProduct();
        DetailedProductDTO expected = createDetailedProductDTO();
        InsertProductDTO insertProductDTO = createInsertProductDTO();

        setupMocksForSave(insertProductDTO, product, productModel);

        DetailedProductDTO actual = standAloneSpy.save(insertProductDTO);

        Assertions.assertEquals(expected, actual);
        verify(standAloneSpy, times(1)).validateIfNameNotExists(insertProductDTO.name());
        verify(standAloneSpy, times(1)).validateIfImageIsUsed(insertProductDTO.image());
        verify(productFactory, times(1)).createEntityFromDTO(insertProductDTO);
        verify(productFactory, times(1)).createModelFromEntity(product);
        verify(productRepository, times(1)).save(productModel);
    }

    @Test
    @DisplayName("Should throw an error when name exists")
    public void testShouldThrowAnErrorWhenNameExists() {
        ProductModel productModel = createProductModel();
        Product product = createProduct();
        InsertProductDTO insertProductDTO = createInsertProductDTO();
        String errorMessage = "invalid name";

        setupMocksForSave(insertProductDTO, product, productModel);
        doThrow(new InvalidProductFieldException(errorMessage, ErrorType.INVALID_PRODUCT_NAME))
                .when(standAloneSpy).validateIfNameNotExists(insertProductDTO.name());

        Exception ex = Assertions.assertThrows(InvalidProductFieldException.class, () -> standAloneSpy.save(insertProductDTO));
        Assertions.assertEquals(errorMessage, ex.getMessage());
        verify(standAloneSpy, times(1)).validateIfNameNotExists(insertProductDTO.name());
        verify(standAloneSpy, never()).validateIfImageIsUsed(insertProductDTO.image());
        verify(productFactory, never()).createEntityFromDTO(insertProductDTO);
        verify(productFactory, never()).createModelFromEntity(product);
        verify(productRepository, never()).save(productModel);
    }

    @Test
    @DisplayName("Should throw an exception when image exists")
    public void testShouldThrowAnErrorWhenImageExists() {
        ProductModel productModel = createProductModel();
        Product product = createProduct();
        InsertProductDTO insertProductDTO = createInsertProductDTO();
        String errorMessage = "invalid image";

        setupMocksForSave(insertProductDTO, product, productModel);
        doThrow(new InvalidProductFieldException(errorMessage, ErrorType.INVALID_PRODUCT_IMAGE))
                .when(standAloneSpy).validateIfImageIsUsed(insertProductDTO.image());

        Exception ex = Assertions.assertThrows(InvalidProductFieldException.class, () -> standAloneSpy.save(insertProductDTO));
        Assertions.assertEquals("invalid image", ex.getMessage());
        verify(standAloneSpy, times(1)).validateIfNameNotExists(insertProductDTO.name());
        verify(standAloneSpy, times(1)).validateIfImageIsUsed(insertProductDTO.image());
        verify(productFactory, never()).createEntityFromDTO(insertProductDTO);
        verify(productFactory, never()).createModelFromEntity(product);
        verify(productRepository, never()).save(productModel);
    }

    @Test
    @DisplayName("Should throw an exception when domain validation error occurs")
    public void testShouldThrowAnErrorWhenDomainValidationError() {
        ProductModel productModel = createProductModel();
        Product product = createProduct();
        InsertProductDTO insertProductDTO = createInsertProductDTO();
        String errorMessage = "invalid field";

        setupMocksForSave(insertProductDTO, product, productModel);
        doThrow(new InvalidProductFieldException(errorMessage, null))
                .when(productFactory).createEntityFromDTO(insertProductDTO);

        Exception ex = Assertions.assertThrows(InvalidProductFieldException.class, () -> standAloneSpy.save(insertProductDTO));

        Assertions.assertEquals(errorMessage, ex.getMessage());
        verify(standAloneSpy, times(1)).validateIfNameNotExists(insertProductDTO.name());
        verify(standAloneSpy, times(1)).validateIfImageIsUsed(insertProductDTO.image());
        verify(productFactory, times(1)).createEntityFromDTO(insertProductDTO);
        verify(productFactory, never()).createModelFromEntity(product);
        verify(productRepository, never()).save(productModel);
    }

    @Test
    @DisplayName("Should update a product")
    public void testShouldUpdateAProduct() {
        ProductModel productModel = createProductModel();
        Product product = createProduct();
        DetailedProductDTO expected = createDetailedProductDTO();
        UpdateProductDTO updateProductDTO = createUpdateProductDTO();
        product.setName(updateProductDTO.name());

        setupMocksForUpdate(productModel, product, updateProductDTO);

        DetailedProductDTO actual = standAloneSpy.update(updateProductDTO, ID);

        Assertions.assertEquals(expected, actual);
        verify(productRepository, times(1)).findById(ID);
        verify(productFactory, times(1)).createEntityFromModel(productModel);
        verify(product, times(1)).clone();
        verify(product, times(1)).update(updateProductDTO);
        verify(productFactory, times(1)).createModelFromEntity(product);
        verify(standAloneSpy, times(1)).applyNecessaryValidations(product, product);
        verify(productRepository, times(1)).save(productModel);
    }

    @Test
    @DisplayName("Should throw an exception when a product is not found")
    public void testShouldThrowAnErrorWhenAProductIsNotFound() {
        ProductModel productModel = createProductModel();
        Product product = createProduct();
        UpdateProductDTO updateProductDTO = createUpdateProductDTO();

        setupMocksForUpdate(productModel, product, updateProductDTO);
        when(productRepository.findById(ID)).thenReturn(Optional.empty());

        Exception ex = Assertions.assertThrows(EntityNotFoundException.class, () -> standAloneSpy.update(updateProductDTO, ID));

        Assertions.assertEquals("Product with id " + ID + " not found", ex.getMessage());
        verify(productRepository, times(1)).findById(ID);
        verify(productFactory, never()).createEntityFromModel(productModel);
        verify(product, never()).clone();
        verify(product, never()).update(updateProductDTO);
        verify(productFactory, never()).createModelFromEntity(product);
        verify(standAloneSpy, never()).applyNecessaryValidations(product, product);
        verify(productRepository, never()).save(productModel);
    }

    @Test
    @DisplayName("Should throw an exception when update cause domain validations errors")
    public void testShouldThrowAnErrorWhenUpdateCauseDomainValidationErrors() {
        ProductModel productModel = createProductModel();
        Product product = createProduct();
        UpdateProductDTO updateProductDTO = createUpdateProductDTO();
        String errorMessage = "invalid field";

        setupMocksForUpdate(productModel, product, updateProductDTO);
        doThrow(new InvalidProductFieldException(errorMessage, ErrorType.INVALID_PRODUCT_NAME))
                .when(product).update(updateProductDTO);

        Exception ex = Assertions.assertThrows(InvalidProductFieldException.class, () -> standAloneSpy.update(updateProductDTO, ID));

        Assertions.assertEquals(errorMessage, ex.getMessage());
        verify(productRepository, times(1)).findById(ID);
        verify(productFactory, times(1)).createEntityFromModel(productModel);
        verify(product, times(1)).clone();
        verify(product, times(1)).update(updateProductDTO);
        verify(productFactory, never()).createModelFromEntity(product);
        verify(standAloneSpy, never()).applyNecessaryValidations(product, product);
        verify(productRepository, never()).save(productModel);
    }

    @Test
    @DisplayName("Should throw an exception when applyNecessaryValidations get errors")
    public void testShouldThrowAnErrorWhenApplyNecessaryValidationsGetErrors() {
        ProductModel productModel = createProductModel();
        Product product = createProduct();
        UpdateProductDTO updateProductDTO = createUpdateProductDTO();
        String errorMessage = "invalid field";

        setupMocksForUpdate(productModel, product, updateProductDTO);
        doThrow(new InvalidProductFieldException(errorMessage, ErrorType.INVALID_PRODUCT_NAME))
                .when(standAloneSpy).applyNecessaryValidations(product, product);

        Exception ex = Assertions.assertThrows(InvalidProductFieldException.class, () -> standAloneSpy.update(updateProductDTO, ID));
        Assertions.assertEquals(errorMessage, ex.getMessage());
        verify(productRepository, times(1)).findById(ID);
        verify(productFactory, times(1)).createEntityFromModel(productModel);
        verify(product, times(1)).clone();
        verify(product, times(1)).update(updateProductDTO);
        verify(standAloneSpy, times(1)).applyNecessaryValidations(product, product);
        verify(productFactory, never()).createModelFromEntity(product);
        verify(productRepository, never()).save(productModel);
    }

    @Test
    @DisplayName("Should empty the stock quantity of a product")
    public void testShouldEmptyTheStockQuantityOfAProduct() {

        when(productRepository.existsById(ID)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> productService.emptyStockQuantity(ID));
        verify(productRepository, times(1)).existsById(ID);
        verify(productRepository, times(1)).emptyStockQuantity(ID);
    }

    @Test
    @DisplayName("Should throw an exception when product does not exist")
    public void testShouldThrowAnErrorWhenProductDoesNotExist() {
        when(productRepository.existsById(ID)).thenReturn(false);
        Exception exception = Assertions
                .assertThrows(EntityNotFoundException.class, () -> productService.emptyStockQuantity(ID));
        Assertions.assertEquals("Product with id " + ID + " not found", exception.getMessage());
        verify(productRepository, times(1)).existsById(ID);
        verify(productRepository, never()).emptyStockQuantity(ID);
    }
}
