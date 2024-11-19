package com.megatech.store.service;

import com.megatech.store.dtos.address.AddressDTO;
import com.megatech.store.exceptions.InvalidCustomerFieldException;
import com.megatech.store.model.AddressModel;
import com.megatech.store.repository.AddressRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    public AddressModel buildAddressModel() {
        AddressModel addressModel = new AddressModel();
        addressModel.setId(1L);
        addressModel.setStreet("street");
        addressModel.setCity("city");
        addressModel.setState("state");
        addressModel.setZipcode("zipcode");
        return addressModel;
    }

    @Test
    @DisplayName("should not throw an exception if a non existing address is provided")
    public void testValidateIfNotUsingShouldNotThrowException() {
        AddressDTO addressDTO = new AddressDTO("street", 1, "city", "state", "zip");
        when(addressRepository
                .findByStreetAndNumberAndCityAndStateAndZipcode("street", 1, "city", "state", "zip"))
                .thenReturn(new ArrayList<>());

        Assertions.assertDoesNotThrow(() -> addressService.validateIfIsNotUsing(addressDTO));
        verify(addressRepository, times(1)).findByStreetAndNumberAndCityAndStateAndZipcode(
                "street", 1, "city", "state", "zip");
    }

    @Test
    @DisplayName("should throw an exception when repository returns a not empty list")
    public void testValidateIfNotUsingShouldThrowException() {
        AddressDTO addressDTO = new AddressDTO("street", 1, "city", "state", "zip");
        when(addressRepository.findByStreetAndNumberAndCityAndStateAndZipcode("street", 1, "city", "state", "zip"))
                .thenReturn(List.of(buildAddressModel()));

        Assertions.assertThrows(InvalidCustomerFieldException.class, () -> addressService.validateIfIsNotUsing(addressDTO));
        verify(addressRepository, times(1))
                .findByStreetAndNumberAndCityAndStateAndZipcode("street", 1, "city", "state", "zip");
    }
}
