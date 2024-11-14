package com.megatech.store.service;

import com.megatech.store.domain.Address;
import com.megatech.store.domain.Customer;
import com.megatech.store.domain.Role;
import com.megatech.store.domain.User;
import com.megatech.store.dtos.customer.CustomerDTO;
import com.megatech.store.dtos.customer.InsertCustomerDTO;
import com.megatech.store.dtos.customer.UpdateCustomerDTO;
import com.megatech.store.exceptions.EntityNotFoundException;
import com.megatech.store.exceptions.InvalidCustomerFieldException;
import com.megatech.store.factory.CustomerFactory;
import com.megatech.store.factory.EntityModelFactory;
import com.megatech.store.factory.ProductFactory;
import com.megatech.store.model.CustomerModel;
import com.megatech.store.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserService userService;
    private final AddressService addressService;
    private final EntityModelFactory<Customer, CustomerModel, InsertCustomerDTO> customerFactory;

    public CustomerService(CustomerRepository customerRepository,
                           UserService userService,
                           AddressService addressService,
                           EntityModelFactory<Customer, CustomerModel, InsertCustomerDTO> customerFactory) {

        this.customerRepository = customerRepository;
        this.userService = userService;
        this.addressService = addressService;
        this.customerFactory = customerFactory;
    }

    public List<CustomerDTO> findAll() {
        return customerRepository.findAll().stream().map(CustomerDTO::new).toList();
    }

    public CustomerDTO findById(Long id) {
        CustomerModel customerModel = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with id " + id + " not found"));

        return new CustomerDTO(customerModel);
    }

    @Transactional
    public CustomerDTO save(InsertCustomerDTO insertCustomerDTO) {
        validateIfCpfIsUsed(insertCustomerDTO.cpf());
        this.userService.validateIfEmailExists(insertCustomerDTO.user().email());
        this.addressService.validateIfIsNotUsing(insertCustomerDTO.address());

        Customer customer = customerFactory.createEntityFromDTO(insertCustomerDTO);
        CustomerModel savedCustomer = customerRepository.save(customerFactory.createModelFromEntity(customer));
        return new CustomerDTO(savedCustomer);
    }

    public void validateIfCpfIsUsed(String cpf){
        if (customerRepository.existsByCpf(cpf)){
            throw new InvalidCustomerFieldException("CPF already exists");
        }
    }

    @Transactional
    public CustomerDTO update(UpdateCustomerDTO customerDTO, Long id) {
        CustomerModel savedCustomerModel = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with id " + id + " not found"));
        Customer customer = customerFactory.createEntityFromModel(savedCustomerModel);

        Customer beforeUpdateCustomer = customer.clone();
        customer.update(customerDTO);
        includeValidations(beforeUpdateCustomer, customer);

        CustomerModel updatedCustomerModel = customerFactory.createModelFromEntity(customer);
        updatedCustomerModel.setId(id);
        updatedCustomerModel.getUser().setId(id);
        updatedCustomerModel.setRegistrationDate(customer.getRegistrationDate());
        return new CustomerDTO(customerRepository.save(updatedCustomerModel));
    }

    @Transactional
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new EntityNotFoundException("Customer with id " + id + " not found");
        }
        customerRepository.deleteById(id);
    }

    private void includeValidations(Customer beforeUpdateCustomer, Customer customer) {
        if (!beforeUpdateCustomer.getUser().getEmail().equals(customer.getUser().getEmail())) {
            userService.validateIfEmailExists(customer.getUser().getEmail());
        }
    }
}
