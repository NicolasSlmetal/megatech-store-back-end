package com.megatech.store.repository;

import com.megatech.store.model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, Long> {

    boolean existsByName(String name);
    boolean existsByImage(String image);
}
