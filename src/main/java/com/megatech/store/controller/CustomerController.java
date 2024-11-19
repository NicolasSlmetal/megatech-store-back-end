package com.megatech.store.controller;

import com.megatech.store.dtos.customer.CustomerDTO;
import com.megatech.store.dtos.customer.InsertCustomerDTO;
import com.megatech.store.dtos.customer.UpdateCustomerDTO;
import com.megatech.store.security.UserAuthentication;
import com.megatech.store.security.UserAuthenticationService;
import com.megatech.store.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final UserAuthenticationService userAuthenticationService;

    public CustomerController(CustomerService customerService, UserAuthenticationService userAuthenticationService) {
        this.customerService = customerService;
        this.userAuthenticationService = userAuthenticationService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> findAll() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerDTO> findMe() {
        UserAuthentication authentication = userAuthenticationService.getCurrentUser();
        return ResponseEntity.ok(customerService.findById(authentication.getUser().getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> save(@RequestBody @Valid InsertCustomerDTO customerDTO, UriComponentsBuilder uriComponentsBuilder)  {
        CustomerDTO savedCustomerDTO = customerService.save(customerDTO);
        URI uri = uriComponentsBuilder.path("/{id}").buildAndExpand(savedCustomerDTO).toUri();
        return ResponseEntity.created(uri).body(savedCustomerDTO);
    }

    @PutMapping("/me")
    public ResponseEntity<CustomerDTO> updateMe(@RequestBody @Valid UpdateCustomerDTO customerDTO) {
        UserAuthentication authentication = userAuthenticationService.getCurrentUser();
        return ResponseEntity.ok(customerService.update(customerDTO, authentication.getUser().getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> update(@PathVariable Long id, @RequestBody @Valid UpdateCustomerDTO customerDTO) {
        return ResponseEntity.ok().body(customerService.update(customerDTO, id));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
