package com.megatech.store.repository;

import com.megatech.store.model.ProductModel;
import com.megatech.store.projections.TotalValueInStockPerProduct;
import com.megatech.store.projections.TotalValueSoldPerProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, Long> {

    boolean existsByName(String name);
    boolean existsByImage(String image);

    @Query(value = "SELECT " +
            "p.prd_id as productId, " +
            "p.prd_name as productName, " +
            "p.prd_price as productPrice, " +
            "p.prd_stock_quantity as quantity, " +
            "p.prd_stock_quantity * p.prd_price as totalValue " +
            "FROM " +
            "tb_product p", nativeQuery = true)
    List<TotalValueInStockPerProduct> getTotalValueInStockPerProduct();
    @Modifying
    @Query(value = "UPDATE tb_product SET prd_stock_quantity = prd_stock_quantity - ?1 WHERE prd_id = ?2", nativeQuery = true)
    void decreaseStockQuantity(Integer decreaseStockQuantity, Long productId);
}
