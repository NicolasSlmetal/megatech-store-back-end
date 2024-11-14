package com.megatech.store.repository;

import com.megatech.store.model.ProductModel;
import com.megatech.store.model.PurchaseModel;
import com.megatech.store.projections.TotalValueSoldPerProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseModel, Long> {

    @Modifying
    @Query(value = "INSERT INTO tb_purchase_product VALUES (?1, ?2, ?3)", nativeQuery = true)
    void insertMappingProductsQuantities(Long purchaseId, Long productId, Integer quantity);

    @Query(value = "SELECT " +
            "p.prd_id as productId, " +
            "p.prd_name as productName, " +
            "SUM(p.prd_price * tpp.tpp_quantity) as totalValue," +
            "SUM(tpp.tpp_quantity) as quantitySold " +
            "FROM " +
            "tb_purchase_product tpp" +
            " INNER JOIN tb_product p" +
            " ON tpp.tpp_prd_id = p.prd_id" +
            " GROUP BY p.prd_id", nativeQuery = true)
    List<TotalValueSoldPerProduct> getTotalValueSoldPerProduct();

    List<PurchaseModel> findAllByCustomerId(Long customerId);

}
