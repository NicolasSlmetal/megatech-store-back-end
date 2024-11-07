package com.megatech.store.controller;

import com.megatech.store.domain.Product;
import com.megatech.store.dtos.products.InsertProductDTO;
import com.megatech.store.dtos.products.UpdateProductDTO;
import com.megatech.store.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> findAll(){
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable("id") Long id){
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody @Valid InsertProductDTO insertProductDTO, UriComponentsBuilder uriComponentsBuilder) {
        Product savedProduct = productService.save(insertProductDTO);
        URI uri = uriComponentsBuilder.path("/products/{id}").buildAndExpand(savedProduct.getId()).toUri();
        return ResponseEntity.created(uri).body(savedProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody @Valid UpdateProductDTO updateProductDTO) {
        return ResponseEntity.ok(productService.update(updateProductDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
