package com.megatech.store.factory;

import com.megatech.store.domain.Address;
import com.megatech.store.dtos.address.AddressDTO;
import com.megatech.store.model.AddressModel;
import org.springframework.stereotype.Component;

@Component
public class AddressFactory implements EntityModelFactory<Address, AddressModel, AddressDTO> {
    @Override
    public Address createEntityFromDTO(AddressDTO dto) {
        Address address = new Address();
        address.setStreet(dto.street());
        address.setNumber(dto.number());
        address.setCity(dto.city());
        address.setState(dto.state());
        address.setZipCode(dto.zipcode());
        return address;
    }

    @Override
    public Address createEntityFromModel(AddressModel model) {
        Address address = new Address();
        address.setId(model.getId());
        address.setStreet(model.getStreet());
        address.setNumber(model.getNumber());
        address.setCity(model.getCity());
        address.setState(model.getState());
        address.setZipCode(model.getZipcode());
        return address;
    }

    @Override
    public AddressModel createModelFromEntity(Address entity) {
        AddressModel model = new AddressModel();
        model.setId(entity.getId());
        model.setStreet(entity.getStreet());
        model.setNumber(entity.getNumber());
        model.setCity(entity.getCity());
        model.setState(entity.getState());
        model.setZipcode(model.getZipcode());
        return model;
    }
}
