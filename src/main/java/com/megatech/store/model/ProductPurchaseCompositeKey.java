package com.megatech.store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductPurchaseCompositeKey implements Serializable {

    @Column(name= "tpp_prd_id", nullable = false)
    private Long productId;

    @Column(name= "tpp_pre_id", nullable = false)
    private Long purchaseId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductPurchaseCompositeKey that = (ProductPurchaseCompositeKey) o;
        return Objects.equals(productId, that.productId) && Objects.equals(purchaseId, that.purchaseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, purchaseId);
    }
}
