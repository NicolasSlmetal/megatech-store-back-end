package com.megatech.store.controller;

import com.megatech.store.domain.Purchase;
import com.megatech.store.dtos.purchase.InsertPurchaseDTO;
import com.megatech.store.dtos.purchase.PurchaseDTO;
import com.megatech.store.projections.TotalValueSoldPerProduct;
import com.megatech.store.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public ResponseEntity<PurchaseDTO> insertPurchase(@RequestBody @Valid InsertPurchaseDTO purchaseDTO, UriComponentsBuilder uriBuilder) {
        PurchaseDTO purchase = purchaseService.insertPurchase(purchaseDTO);
        URI uri = uriBuilder.path("/{id}").buildAndExpand(purchase).toUri();
        return ResponseEntity.created(uri).body(purchase);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<PurchaseDTO>> findByCustomerId(@PathVariable("id") Long customerId) {
        return ResponseEntity.ok(purchaseService.findByCustomerId(customerId));
    }

    @GetMapping("/totalValue")
    public ResponseEntity<List<TotalValueSoldPerProduct>> getTotalValueSoldPerProduct() {
        return ResponseEntity.ok(purchaseService.getTotalValueSoldPerProduct());
    }
}
