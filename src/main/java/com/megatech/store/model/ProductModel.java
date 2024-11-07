package com.megatech.store.model;

import com.megatech.store.domain.Product;
import com.megatech.store.dtos.products.UpdateProductDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "prd_manufacturer")
    private String manufacturer;

    @Column(name = "prd_price")
    private Double price;

    @Column(name = "prd_stock_quantity")
    private Integer stockQuantity;

    public ProductModel(Product product) {
        this.name = product.getName();
        this.manufacturer = product.getManufacturer();
        this.price = product.getPrice();
        this.stockQuantity = product.getStockQuantity();
    }

}
