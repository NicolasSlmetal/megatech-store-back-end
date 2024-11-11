package com.megatech.store.service;

import com.megatech.store.domain.Address;
import com.megatech.store.exceptions.InvalidCustomerFieldException;
import com.megatech.store.model.AddressModel;
import com.megatech.store.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public void validateIfIsNotUsing(Address address) {
        Optional<AddressModel> existingAddress = addressRepository
                .findByStreetAndNumberAndCityAndStateAndZipcode(
                        address.getStreet(),
                        address.getNumber(),
                        address.getCity(),
                        address.getState(),
                        address.getZipcode());
        if (existingAddress.isPresent()) {
            throw new InvalidCustomerFieldException("Address cannot be used");
        }
    }
}
