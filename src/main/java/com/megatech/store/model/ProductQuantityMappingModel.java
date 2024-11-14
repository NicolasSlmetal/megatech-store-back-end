package com.megatech.store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_purchase_product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductQuantityMappingModel implements Model {

    @EmbeddedId
    private ProductPurchaseCompositeKey compositeKey = new ProductPurchaseCompositeKey();

    @ManyToOne(cascade = {CascadeType.DETACH})
    @MapsId("productId")
    @JoinColumn(name = "tpp_prd_id")
    private ProductModel product;

    @ManyToOne()
    @MapsId("purchaseId")
    @JoinColumn(name = "tpp_pre_id")
    private PurchaseModel purchase;

    @Column(name = "tpp_quantity")
    private Integer quantity;
}
