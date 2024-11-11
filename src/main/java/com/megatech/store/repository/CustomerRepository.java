package com.megatech.store.repository;

import com.megatech.store.model.CustomerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerModel, Long> {

    boolean existsByCpf(String cpf);
}
