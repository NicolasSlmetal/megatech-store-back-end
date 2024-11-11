package com.megatech.store.repository;

import com.megatech.store.model.AddressModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<AddressModel, Long> {


    Optional<AddressModel>
    findByStreetAndNumberAndCityAndStateAndZipcode(String street, Integer number, String city, String state, String zipcode);
}
