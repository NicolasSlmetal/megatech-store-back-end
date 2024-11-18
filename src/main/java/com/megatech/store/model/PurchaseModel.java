package com.megatech.store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity(name = "purchase")
@Table(name ="tb_purchase")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseModel implements Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pre_id")
    private Long id;

    @CreationTimestamp
    @Column(name = "pre_date")
    private LocalDateTime date;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.MERGE)
    private Set<ProductQuantityMappingModel> mappingProductQuantity;

    @ManyToOne
    @JoinColumn(name="pre_customer_id")
    private CustomerModel customer;

    @Column(name="pre_total_value")
    private Double totalValue;
}
