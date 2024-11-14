package com.megatech.store.factory;

import com.megatech.store.domain.Address;
import com.megatech.store.domain.Customer;
import com.megatech.store.domain.Role;
import com.megatech.store.domain.User;
import com.megatech.store.dtos.address.AddressDTO;
import com.megatech.store.dtos.customer.InsertCustomerDTO;
import com.megatech.store.dtos.user.UserDTO;
import com.megatech.store.model.AddressModel;
import com.megatech.store.model.CustomerModel;
import com.megatech.store.model.UserModel;
import org.springframework.stereotype.Component;

@Component
public class CustomerFactory implements EntityModelFactory<Customer, CustomerModel, InsertCustomerDTO> {

    private final EntityModelFactory<Address, AddressModel, AddressDTO> addressFactory;
    private final EntityModelFactory<User, UserModel, UserDTO> userFactory;

    public CustomerFactory(EntityModelFactory<Address, AddressModel, AddressDTO> addressFactory, EntityModelFactory<User, UserModel, UserDTO> userFactory) {
        this.addressFactory = addressFactory;
        this.userFactory = userFactory;
    }

    @Override
    public Customer createEntityFromDTO(InsertCustomerDTO dto) {
        Customer customer = new Customer();
        customer.setName(dto.name());
        customer.setCpf(dto.cpf());
        customer.setUser(userFactory.createEntityFromDTO(dto.user()));
        customer.getUser().setRole(Role.CUSTOMER);
        customer.addAddress(addressFactory.createEntityFromDTO(dto.address()));
        return customer;
    }

    @Override
    public Customer createEntityFromModel(CustomerModel model) {
        Customer customer = new Customer();
        customer.setName(model.getName());
        customer.setCpf(model.getCpf());
        customer.setRegistrationDate(model.getRegistrationDate());
        customer.setUser(userFactory.createEntityFromModel(model.getUser()));
        model.getAddresses()
                .forEach(address -> customer.addAddress(addressFactory.createEntityFromModel(address)));
        return customer;
    }

    @Override
    public CustomerModel createModelFromEntity(Customer entity) {
        CustomerModel model = new CustomerModel();
        if (entity.getUser().getId() != null) {
            model.setId(entity.getUser().getId());
        }
        if (entity.getRegistrationDate() != null) {
            model.setRegistrationDate(entity.getRegistrationDate());
        }
        model.setName(entity.getName());
        model.setCpf(entity.getCpf());
        model.setUser(userFactory.createModelFromEntity(entity.getUser()));
        model.setAddresses(entity.getAddresses().stream().map(AddressModel::new).toList());
        model.getAddresses().forEach(address -> address.setCustomer(model));
        return model;
    }
}
