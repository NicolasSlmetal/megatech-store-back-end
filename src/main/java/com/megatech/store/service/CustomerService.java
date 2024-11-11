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
import com.megatech.store.model.CustomerModel;
import com.megatech.store.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserService userService;
    private final AddressService addressService;

    public CustomerService(CustomerRepository customerRepository, UserService userService, AddressService addressService) {
        this.customerRepository = customerRepository;
        this.userService = userService;
        this.addressService = addressService;
    }

    public List<CustomerDTO> findAll() {
        return customerRepository.findAll().stream().map(CustomerDTO::new).toList();
    }

    public CustomerDTO findById(Long id) {
        CustomerModel customerModel = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with id " + id + " not found"));

        return new CustomerDTO(customerModel);
    }



    public CustomerDTO save(InsertCustomerDTO insertCustomerDTO) {

        Address address = new Address(insertCustomerDTO.address());
        Customer customer = new Customer(insertCustomerDTO);
        validateIfCpfIsUsed(customer.getCpf());
        User user = new User(insertCustomerDTO.user(), Role.CUSTOMER);
        customer.setUser(user);
        this.userService.validateIfEmailExists(user.getEmail());

        this.addressService.validateIfIsNotUsing(address);
        customer.addAddress(address);

        CustomerModel savedCustomer = customerRepository.save(new CustomerModel(customer));
        return new CustomerDTO(savedCustomer);
    }

    public void validateIfCpfIsUsed(String cpf){
        if (customerRepository.existsByCpf(cpf)){
            throw new InvalidCustomerFieldException("CPF already exists");
        }
    }

    public CustomerDTO update(UpdateCustomerDTO customerDTO, Long id) {
        CustomerModel savedCustomerModel = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with id " + id + " not found"));
        Customer customer = new Customer(savedCustomerModel);

        Customer beforeUpdateCustomer = customer.clone();
        customer.update(customerDTO);
        includeValidations(beforeUpdateCustomer, customer);

        return new CustomerDTO(customerRepository.save(new CustomerModel(customer)));
    }

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
