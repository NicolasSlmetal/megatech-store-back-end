package com.megatech.store.repository;

import com.megatech.store.model.ProductModel;
import com.megatech.store.projections.TotalValueInStockPerProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, Long> {

    boolean existsByName(String name);
    boolean existsByImage(String image);

    @Query(value = "SELECT * from tb_product p where p.prd_stock_quantity > 0", nativeQuery = true)
    List<ProductModel> findAllByStockQuantityGreaterThanZero();

    @Query(value = "SELECT * from tb_product p where p.prd_stock_quantity = 0", nativeQuery = true)
    List<ProductModel> findAllByStockQuantityEqualZero();

    @Modifying
    @Query(value = "UPDATE tb_product SET prd_stock_quantity = 0 WHERE prd_id = ?1", nativeQuery = true)
    void emptyStockQuantity(Long productId);

    @Query(value = "SELECT " +
            "p.prd_id as productId, " +
            "p.prd_name as productName, " +
            "p.prd_entry_date as entryDate, " +
            "p.prd_price as productPrice, " +
            "p.prd_stock_quantity as quantity, " +
            "p.prd_stock_quantity * p.prd_price as totalValue " +
            "FROM " +
            "tb_product p WHERE p.prd_stock_quantity > 0", nativeQuery = true)
    List<TotalValueInStockPerProduct> getTotalValueInStockPerProduct();
    @Modifying
    @Query(value = "UPDATE tb_product SET prd_stock_quantity = prd_stock_quantity - ?1 WHERE prd_id = ?2", nativeQuery = true)
    void decreaseStockQuantity(Integer decreaseStockQuantity, Long productId);
}
