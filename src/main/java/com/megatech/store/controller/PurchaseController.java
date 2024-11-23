package com.megatech.store.controller;

import com.megatech.store.dtos.purchase.InsertPurchaseDTO;
import com.megatech.store.dtos.purchase.PurchaseDTO;
import com.megatech.store.projections.TotalValueSoldPerProduct;
import com.megatech.store.security.UserAuthentication;
import com.megatech.store.security.UserAuthenticationService;
import com.megatech.store.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final UserAuthenticationService userAuthenticationService;

    public PurchaseController(PurchaseService purchaseService, UserAuthenticationService userAuthenticationService) {
        this.purchaseService = purchaseService;
        this.userAuthenticationService = userAuthenticationService;
    }

    @PostMapping("/purchases")
    public ResponseEntity<PurchaseDTO> insertPurchase(@RequestBody @Valid InsertPurchaseDTO purchaseDTO, UriComponentsBuilder uriBuilder) {
        UserAuthentication authentication = userAuthenticationService.getCurrentUser();
        PurchaseDTO purchase = purchaseService.insertPurchase(purchaseDTO, authentication.getUser().getId());
        URI uri = uriBuilder.path("/customer/me/purchases").buildAndExpand(purchase).toUri();
        return ResponseEntity.created(uri).body(purchase);
    }

    @GetMapping("/customers/me/purchases")
    public ResponseEntity<List<PurchaseDTO>> findByCustomerId() {
        UserAuthentication authentication = userAuthenticationService.getCurrentUser();
        return ResponseEntity.ok(purchaseService.findByCustomerId(authentication.getUser().getId()));
    }

    @GetMapping("/purchases/total-value-by-product")
    public ResponseEntity<List<TotalValueSoldPerProduct>> getTotalValueSoldPerProduct() {
        return ResponseEntity.ok(purchaseService.getTotalValueSoldPerProduct());
    }
}
