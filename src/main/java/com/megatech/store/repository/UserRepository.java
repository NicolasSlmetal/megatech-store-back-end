package com.megatech.store.repository;

import com.megatech.store.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    boolean existsByEmail(String email);
    Optional<UserModel> findByEmail(String email);
}
