package com.megatech.store.service;

import com.megatech.store.domain.Customer;
import com.megatech.store.domain.User;
import com.megatech.store.dtos.address.AddressDTO;
import com.megatech.store.dtos.customer.CustomerDTO;
import com.megatech.store.dtos.customer.InsertCustomerDTO;
import com.megatech.store.dtos.customer.UpdateCustomerDTO;
import com.megatech.store.dtos.user.UserDTO;
import com.megatech.store.exceptions.EntityNotFoundException;
import com.megatech.store.exceptions.ErrorType;
import com.megatech.store.exceptions.InvalidCustomerFieldException;
import com.megatech.store.exceptions.InvalidUserFieldException;
import com.megatech.store.factory.CustomerFactory;
import com.megatech.store.model.CustomerModel;
import com.megatech.store.model.UserModel;
import com.megatech.store.repository.CustomerRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class CustomerServiceTest {

    public static final long ID = 1L;
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String HASH = "hash";
    public static final String NAME = "name";
    public static final String CPF = "123";
    public static final String STREET = "street";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String ZIPCODE = "zipcode";
    public static final int NUMBER = 1;
    public static final LocalDate REGISTRATION_DATE = LocalDate.now();
    public static final String SALT = "SALT";
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserService userService;

    @Mock
    private AddressService addressService;

    @Mock
    private CustomerFactory customerFactory;

    public static MockedStatic<BCrypt> mockedBCrypt;

    public CustomerService standAloneSpy;

    @InjectMocks
    private CustomerService customerService;

    @BeforeAll
    public static void initStaticMocks() {
        mockedBCrypt = mockStatic(BCrypt.class);
    }

    @BeforeEach
    public void initMocks() {
        mockedBCrypt.clearInvocations();
        MockitoAnnotations.openMocks(this);
        standAloneSpy = spy(customerService);
    }

    @AfterAll
    public static void unregisterStaticMocks() {
        mockedBCrypt.close();
    }

    public CustomerDTO createCustomerDTO() {
        return new CustomerDTO(NAME, CPF, REGISTRATION_DATE, EMAIL);
    }

    public Customer createCustomer() {
        return new Customer();
    }

    public InsertCustomerDTO createInsertCustomerDTO() {
        return new InsertCustomerDTO(NAME, CPF,
                new AddressDTO(STREET, NUMBER, CITY, STATE, ZIPCODE),
                new UserDTO(EMAIL, PASSWORD));
    }

    public UpdateCustomerDTO createUpdateCustomerDTO() {
        return new UpdateCustomerDTO(NAME, EMAIL, PASSWORD);
    }

    public CustomerModel createCustomerModel() {
        CustomerModel customerModel = new CustomerModel();
        customerModel.setName(NAME);
        customerModel.setCpf(CPF);
        customerModel.setRegistrationDate(REGISTRATION_DATE);
        customerModel.setUser(new UserModel());
        customerModel.getUser().setId(ID);
        customerModel.getUser().setEmail(EMAIL);
        customerModel.getUser().setPassword(PASSWORD);
        return customerModel;
    }

    private void setupMockForSave(InsertCustomerDTO insertCustomerDTO, Customer customer, CustomerModel customerModel) {
        doNothing().when(standAloneSpy).validateIfCpfIsUsed(insertCustomerDTO.cpf());
        doNothing().when(userService).validateIfEmailExists(insertCustomerDTO.user().email());
        doNothing().when(addressService).validateIfIsNotUsing(insertCustomerDTO.address());
        when(customerFactory.createEntityFromDTO(insertCustomerDTO)).thenReturn(customer);
        when(customerFactory.createModelFromEntity(customer)).thenReturn(customerModel);
        when(customerRepository.save(customerModel)).thenReturn(customerModel);
        mockedBCrypt.when(BCrypt::gensalt).thenReturn(SALT);
        mockedBCrypt.when(() -> BCrypt.hashpw(anyString(), anyString())).thenReturn(HASH);
    }

    private void setupMocksForUpdate(Customer customer, CustomerModel customerModel, UpdateCustomerDTO updateCustomerDTO, User user, CustomerModel updatedCustomerModel) {
        doNothing().when(standAloneSpy).validateForUpdatedFields(customer, customer);
        when(customerRepository.findById(ID)).thenReturn(Optional.of(customerModel));
        when(customerFactory.createEntityFromModel(customerModel)).thenReturn(customer);
        doNothing().when(customer).update(updateCustomerDTO);
        when(customer.clone()).thenReturn(customer);
        when(customer.getRegistrationDate()).thenReturn(REGISTRATION_DATE);
        when(customer.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(ID);
        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getPassword()).thenReturn(PASSWORD);
        doNothing().when(userService).validateIfEmailExists(updateCustomerDTO.email());
        when(customerFactory.createModelFromEntity(customer)).thenReturn(updatedCustomerModel);
        doReturn(HASH).when(standAloneSpy).generateHashIfNewPasswordIsSet(customer, customer);
        when(customerRepository.save(updatedCustomerModel)).thenReturn(updatedCustomerModel);
    }

    @Test
    @DisplayName("should return a list of all Customers DTOs")
    public void testShouldReturnAllCustomersDTOs() {
        List<CustomerDTO> expected = List.of(createCustomerDTO());
        when(customerRepository.findAll()).thenReturn(List.of(createCustomerModel()));

        List<CustomerDTO> customerDTOS = customerService.findAll();

        Assertions.assertEquals(expected, customerDTOS);
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("should return a customer when repository finds him by your id")
    public void testShouldReturnACustomerByIdWhenCustomerExists() {

        CustomerDTO expected = createCustomerDTO();
        when(customerRepository.findById(ID)).thenReturn(Optional.of(createCustomerModel()));

        CustomerDTO customerDTO = customerService.findById(ID);

        Assertions.assertEquals(expected, customerDTO);
        verify(customerRepository, times(1)).findById(ID);

    }

    @Test
    @DisplayName("should throw an exception when a customer is not found")
    public void testShouldThrowAnExceptionWhenCustomerDoesNotExist() {
        when(customerRepository.findById(ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> customerService.findById(ID));
        verify(customerRepository, times(NUMBER)).findById(ID);

    }

    @Test
    @DisplayName("should save a valid CustomerDTO in database")
    public void testShouldSaveCustomerDTO() {
        InsertCustomerDTO insertCustomerDTO = createInsertCustomerDTO();
        Customer customer = createCustomer();
        CustomerModel customerModel = createCustomerModel();

        CustomerDTO expected = createCustomerDTO();

        setupMockForSave(insertCustomerDTO, customer, customerModel);

        CustomerDTO customerDTO = standAloneSpy.save(insertCustomerDTO);

        Assertions.assertEquals(expected, customerDTO);

        verify(userService, times(1)).validateIfEmailExists(insertCustomerDTO.user().email());
        verify(addressService, times(1)).validateIfIsNotUsing(insertCustomerDTO.address());
        verify(customerFactory, times(1)).createEntityFromDTO(insertCustomerDTO);
        verify(customerFactory, times(1)).createModelFromEntity(customer);
        verify(customerRepository, times(1)).save(customerModel);
        mockedBCrypt.verify(BCrypt::gensalt, times(1));
        mockedBCrypt.verify(() -> BCrypt.hashpw(insertCustomerDTO.user().password(), SALT), times(NUMBER));
    }

    @Test
    @DisplayName("should throw an exception when the provided cpf exists")
    public void testShouldThrowAnExceptionWhenCpfIsUsed() {
        InsertCustomerDTO insertCustomerDTO = createInsertCustomerDTO();
        Customer customer = createCustomer();
        CustomerModel customerModel = createCustomerModel();

        setupMockForSave(insertCustomerDTO, customer, customerModel);
        doThrow(new InvalidCustomerFieldException("cpf exists", ErrorType.INVALID_CUSTOMER_CPF))
                .when(standAloneSpy).validateIfCpfIsUsed(insertCustomerDTO.cpf());

        Assertions.assertThrows(InvalidCustomerFieldException.class, () -> standAloneSpy.save(insertCustomerDTO));

        verify(standAloneSpy, times(1)).validateIfCpfIsUsed(insertCustomerDTO.cpf());
        verify(userService, never()).validateIfEmailExists(insertCustomerDTO.user().email());
        verify(addressService, never()).validateIfIsNotUsing(insertCustomerDTO.address());
        verify(customerFactory, never()).createEntityFromDTO(insertCustomerDTO);
        verify(customerFactory, never()).createModelFromEntity(customer);
        verify(customerRepository, never()).save(customerModel);
        mockedBCrypt.verify(BCrypt::gensalt, never());
        mockedBCrypt.verify(() -> BCrypt.hashpw(insertCustomerDTO.user().password(), SALT), never());
    }

    @Test
    @DisplayName("Should throw an exception when user email exists")
    public void testShouldThrowAnExceptionWhenUserEmailExists() {
        InsertCustomerDTO insertCustomerDTO = createInsertCustomerDTO();
        Customer customer = createCustomer();
        CustomerModel customerModel = createCustomerModel();

        setupMockForSave(insertCustomerDTO, customer, customerModel);
        doThrow(new InvalidUserFieldException("invalid email", ErrorType.INVALID_USER_EMAIL))
                .when(userService).validateIfEmailExists(insertCustomerDTO.user().email());

        Assertions.assertThrows(InvalidUserFieldException.class, () -> standAloneSpy.save(insertCustomerDTO));
        verify(standAloneSpy, times(1)).validateIfCpfIsUsed(insertCustomerDTO.cpf());
        verify(userService, times(1)).validateIfEmailExists(insertCustomerDTO.user().email());
        verify(addressService, never()).validateIfIsNotUsing(insertCustomerDTO.address());
        verify(customerFactory, never()).createEntityFromDTO(insertCustomerDTO);
        verify(customerFactory, never()).createModelFromEntity(customer);
        verify(customerRepository, never()).save(customerModel);
        mockedBCrypt.verify(BCrypt::gensalt, never());
        mockedBCrypt.verify(() -> BCrypt.hashpw(insertCustomerDTO.user().password(), SALT), never());
    }

    @Test
    @DisplayName("Should throw an exception when a existing address is provided")
    public void testShouldThrowAnExceptionWhenExistingAddressIsProvided() {
        InsertCustomerDTO insertCustomerDTO = createInsertCustomerDTO();
        Customer customer = createCustomer();
        CustomerModel customerModel = createCustomerModel();

        setupMockForSave(insertCustomerDTO, customer, customerModel);

        doThrow(new InvalidCustomerFieldException("exists address", ErrorType.ADDRESS_ALREADY_EXISTS))
                .when(addressService).validateIfIsNotUsing(insertCustomerDTO.address());

        Assertions.assertThrows(InvalidCustomerFieldException.class, () -> standAloneSpy.save(insertCustomerDTO));
        verify(standAloneSpy, times(1)).validateIfCpfIsUsed(insertCustomerDTO.cpf());
        verify(userService, times(1)).validateIfEmailExists(insertCustomerDTO.user().email());
        verify(addressService, times(1)).validateIfIsNotUsing(insertCustomerDTO.address());
        verify(customerFactory, never()).createEntityFromDTO(insertCustomerDTO);
        verify(customerFactory, never()).createModelFromEntity(customer);
        verify(customerRepository, never()).save(customerModel);
        mockedBCrypt.verify(BCrypt::gensalt, never());
        mockedBCrypt.verify(() -> BCrypt.hashpw(insertCustomerDTO.user().password(), SALT), never());
    }

    @Test
    @DisplayName("Should throw an exception when some domain validation in factory is thrown")
    public void testShouldThrowAnExceptionWhenSomeDomainValidationInFactoryFails() {
        InsertCustomerDTO insertCustomerDTO = createInsertCustomerDTO();
        Customer customer = createCustomer();
        CustomerModel customerModel = createCustomerModel();

        setupMockForSave(insertCustomerDTO, customer, customerModel);
        when(customerFactory.createEntityFromDTO(insertCustomerDTO))
                .thenThrow(new InvalidCustomerFieldException("some field is invalid", ErrorType.INVALID_USER_EMAIL));

        Assertions.assertThrows(InvalidCustomerFieldException.class, () -> standAloneSpy.save(insertCustomerDTO));
        verify(standAloneSpy, times(1)).validateIfCpfIsUsed(insertCustomerDTO.cpf());
        verify(userService, times(1)).validateIfEmailExists(insertCustomerDTO.user().email());
        verify(addressService, times(1)).validateIfIsNotUsing(insertCustomerDTO.address());
        verify(customerFactory, times(1)).createEntityFromDTO(insertCustomerDTO);
        verify(customerFactory, never()).createModelFromEntity(customer);
        verify(customerRepository, never()).save(customerModel);
        mockedBCrypt.verify(BCrypt::gensalt, never());
        mockedBCrypt.verify(() -> BCrypt.hashpw(insertCustomerDTO.user().password(), SALT), never());
    }

    @Test
    @DisplayName("Should update an entity")
    public void testShouldUpdateEntity() {
        UpdateCustomerDTO updateCustomerDTO = createUpdateCustomerDTO();
        Customer customer = mock(Customer.class);
        User user = mock(User.class);
        CustomerModel customerModel = createCustomerModel();
        CustomerModel updatedCustomerModel = createCustomerModel();
        CustomerDTO expectedCustomerDTO = createCustomerDTO();

        setupMocksForUpdate(customer, customerModel, updateCustomerDTO, user, updatedCustomerModel);

        CustomerDTO updatedCustomer = standAloneSpy.update(updateCustomerDTO, ID);

        Assertions.assertNotNull(updatedCustomer);
        Assertions.assertEquals(expectedCustomerDTO, updatedCustomer);

        verify(customerRepository, times(1)).findById(ID);
        verify(customerFactory, times(1)).createEntityFromModel(customerModel);
        verify(customer, times(1)).update(updateCustomerDTO);
        verify(customer, times(1)).clone();
        verify(userService, never()).validateIfEmailExists(updateCustomerDTO.email());
        verify(standAloneSpy, times(1)).validateForUpdatedFields(customer, customer);
        verify(standAloneSpy, times(1)).generateHashIfNewPasswordIsSet(customer, customer);
        verify(customerRepository, times(1)).save(updatedCustomerModel);
    }

    @Test
    @DisplayName("Should throw an exception when findById does not find a customer")
    public void testShouldThrowAnExceptionWhenFindByIdDoesNotFindCustomer() {
        UpdateCustomerDTO updateCustomerDTO = createUpdateCustomerDTO();
        Customer customer = mock(Customer.class);
        User user = mock(User.class);
        CustomerModel customerModel = createCustomerModel();
        CustomerModel updatedCustomerModel = createCustomerModel();

        setupMocksForUpdate(customer, customerModel, updateCustomerDTO, user, updatedCustomerModel);
        when(customerRepository.findById(ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> standAloneSpy.update(updateCustomerDTO, ID));
        verify(customerRepository, times(1)).findById(ID);
        verify(customerFactory, never()).createEntityFromModel(customerModel);
        verify(customer, never()).update(updateCustomerDTO);
        verify(standAloneSpy, never()).validateForUpdatedFields(customer, customer);
        verify(standAloneSpy, never()).generateHashIfNewPasswordIsSet(customer, customer);
        verify(customerRepository, never()).save(customerModel);
    }

    @Test
    @DisplayName("Should throw an exception when domain validate fails when update is called")
    public void testShouldThrowAnExceptionWhenDomainValidateFailsWhenUpdateIsCalled() {
        UpdateCustomerDTO updateCustomerDTO = createUpdateCustomerDTO();
        Customer customer = mock(Customer.class);
        User user = mock(User.class);
        CustomerModel customerModel = createCustomerModel();
        CustomerModel updatedCustomerModel = createCustomerModel();

        setupMocksForUpdate(customer, customerModel, updateCustomerDTO, user, updatedCustomerModel);
        doThrow(new InvalidCustomerFieldException("some data is invalid", null))
                .when(customer).update(updateCustomerDTO);

        Assertions.assertThrows(InvalidCustomerFieldException.class,
                () -> standAloneSpy.update(updateCustomerDTO, ID));
        verify(customerRepository, times(1)).findById(ID);
        verify(customerFactory, times(1)).createEntityFromModel(customerModel);
        verify(customer, times(1)).update(updateCustomerDTO);
        verify(standAloneSpy, never()).validateForUpdatedFields(customer, customer);
        verify(standAloneSpy, never()).generateHashIfNewPasswordIsSet(customer, customer);
        verify(customerRepository, never()).save(customerModel);
    }

    @Test
    @DisplayName("Should throw an exception when any updated unique field already exists")
    public void testShouldThrowAnExceptionWhenAnyUpdatedUniqueFieldAlreadyExists() {
        UpdateCustomerDTO updateCustomerDTO = createUpdateCustomerDTO();
        Customer customer = mock(Customer.class);
        User user = mock(User.class);
        CustomerModel customerModel = createCustomerModel();
        CustomerModel updatedCustomerModel = createCustomerModel();

        setupMocksForUpdate(customer, customerModel, updateCustomerDTO, user, updatedCustomerModel);
        doThrow(new InvalidCustomerFieldException("some data is invalid", null))
                .when(standAloneSpy).validateForUpdatedFields(customer, customer);

        Assertions.assertThrows(InvalidCustomerFieldException.class,() ->  standAloneSpy.update(updateCustomerDTO, ID));
        verify(customerRepository, times(1)).findById(ID);
        verify(customerFactory, times(1)).createEntityFromModel(customerModel);
        verify(customer, times(1)).update(updateCustomerDTO);
        verify(standAloneSpy, times(1)).validateForUpdatedFields(customer, customer);
        verify(standAloneSpy, never()).generateHashIfNewPasswordIsSet(customer, customer);
        verify(customerRepository, never()).save(customerModel);
    }

    @Test
    @DisplayName("Should delete an existent customer")
    public void testShouldDeleteCustomer() {
        when(customerRepository.existsById(ID)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> customerService.delete(ID));
    }

    @Test
    @DisplayName("Should throw an exception when customer does not exist")
    public void testShouldThrowAnException() {
        when(customerRepository.existsById(ID)).thenReturn(false);
        Assertions.assertThrows(EntityNotFoundException.class, () -> customerService.delete(ID));
    }
}
