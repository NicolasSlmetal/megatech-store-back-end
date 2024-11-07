package com.megatech.store.model;

import com.megatech.store.domain.Product;
import com.megatech.store.dtos.products.UpdateProductDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity(name="product")
@Table(name = "tb_product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {

    @Id
    @Column(name = "prd_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prd_name")
    private String name;

    @Column(name = "prd_image")
    private String image;

    @Column(name = "prd_manufacturer")
    private String manufacturer;

    @Column(name = "prd_price")
    private Double price;

    @Column(name = "prd_stock_quantity")
    private Integer stockQuantity;

    @Column(name = "prd_entry_date", updatable = false)
    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime entryDate;

    public ProductModel(Product product) {
        this.name = product.getName();
        this.image = product.getImage();
        this.manufacturer = product.getManufacturer();
        this.price = product.getPrice();
        this.stockQuantity = product.getStockQuantity();
    }

}
