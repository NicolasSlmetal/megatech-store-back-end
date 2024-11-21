package com.megatech.store.repository;

import com.megatech.store.model.AddressModel;
import com.megatech.store.model.CustomerModel;
import com.megatech.store.model.UserModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
@DataJpaTest
public class AddressRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private AddressRepository addressRepository;

    public UserModel createUser() {
        UserModel user = new UserModel();
        entityManager.persist(user);
        return user;
    }

    public void insertDefaultData() {
        AddressModel address1 = createAddress();
        entityManager.persist(address1);
        entityManager.flush();
    }

    private AddressModel createAddress() {
        CustomerModel customerModel = createCustomer();
        AddressModel address1 = new AddressModel();
        address1.setStreet("street");
        address1.setNumber(1);
        address1.setCity("city");
        address1.setState("state");
        address1.setZipcode("zipcode");
        address1.setCustomer(customerModel);
        return address1;
    }

    private CustomerModel createCustomer() {
        CustomerModel customerModel = new CustomerModel();
        customerModel.setUser(createUser());
        return entityManager.persist(customerModel);
    }

    @Test
    @DisplayName("Should return a list with a address corresponding the parameters")
    public void testShouldReturnAListWithAAddressCorrespondingTheParameters() {

        this.insertDefaultData();

        List<AddressModel> actual = addressRepository
                .findByStreetAndNumberAndCityAndStateAndZipcode("street", 1, "city", "state", "zipcode");


        Assertions.assertFalse(actual.isEmpty());
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals("street", actual.get(0).getStreet());
        Assertions.assertEquals(1, actual.get(0).getNumber());
        Assertions.assertEquals("city", actual.get(0).getCity());
        Assertions.assertEquals("state", actual.get(0).getState());
        Assertions.assertEquals("zipcode", actual.get(0).getZipcode());
    }

    @Test
    @DisplayName("Should return a empty list when a non existing parameter is provided")
    public void testShouldReturnAnEmptyListWhenANonExistingParameterIsProvided() {
        this.insertDefaultData();

        List<AddressModel> result = addressRepository.findByStreetAndNumberAndCityAndStateAndZipcode("street2", 1, "city", "state", "zipcode");

        Assertions.assertTrue(result.isEmpty());
    }
}
