package com.megatech.store.controller;

import com.megatech.store.dtos.products.DetailedProductDTO;
import com.megatech.store.dtos.products.DisplayProductDTO;
import com.megatech.store.dtos.products.InsertProductDTO;
import com.megatech.store.dtos.products.UpdateProductDTO;
import com.megatech.store.projections.TotalValueInStockPerProduct;
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
    public ResponseEntity<List<DisplayProductDTO>> findAll(){
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/zero")
    public ResponseEntity<List<DetailedProductDTO>> findAllWhereStockIsZero() {
        return ResponseEntity.ok(productService.findAllWhereStockIsZero());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailedProductDTO> findById(@PathVariable("id") Long id){
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/totalValue")
    public ResponseEntity<List<TotalValueInStockPerProduct>> getTotalValueInStockPerProduct(){
        return ResponseEntity.ok(productService.getTotalValueInStockPerProduct());
    }

    @PostMapping
    public ResponseEntity<DetailedProductDTO> save(@RequestBody @Valid InsertProductDTO insertProductDTO, UriComponentsBuilder uriComponentsBuilder) {
        DetailedProductDTO savedProduct = productService.save(insertProductDTO);
        URI uri = uriComponentsBuilder.path("/products/{id}").buildAndExpand(savedProduct.id()).toUri();
        return ResponseEntity.created(uri).body(savedProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetailedProductDTO> update(@PathVariable Long id, @RequestBody @Valid UpdateProductDTO updateProductDTO) {
        return ResponseEntity.ok(productService.update(updateProductDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> emptyStockQuantity(@PathVariable Long id) {
        productService.emptyStockQuantity(id);
        return ResponseEntity.noContent().build();
    }
}
