package com.megatech.store.service;

import com.megatech.store.dtos.address.AddressDTO;
import com.megatech.store.exceptions.ErrorType;
import com.megatech.store.exceptions.InvalidCustomerFieldException;
import com.megatech.store.model.AddressModel;
import com.megatech.store.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public void validateIfIsNotUsing(AddressDTO address) {
        List<AddressModel> existingAddress = addressRepository
                .findByStreetAndNumberAndCityAndStateAndZipcode(
                        address.street(),
                        address.number(),
                        address.city(),
                        address.state(),
                        address.zipcode());
        if (!existingAddress.isEmpty()) {
            throw new InvalidCustomerFieldException("Address cannot be used", ErrorType.INVALID_ADDRESS_DATA);
        }
    }
}
