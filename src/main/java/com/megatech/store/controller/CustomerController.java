package com.megatech.store.controller;

import com.megatech.store.dtos.customer.CustomerDTO;
import com.megatech.store.dtos.customer.InsertCustomerDTO;
import com.megatech.store.dtos.customer.UpdateCustomerDTO;
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

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> findAll() {
        return ResponseEntity.ok(customerService.findAll());
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
