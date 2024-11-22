package com.megatech.store.repository;

import com.megatech.store.model.CustomerModel;
import com.megatech.store.model.UserModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    private void insertDefaultData() {
        UserModel user = new UserModel();
        UserModel persistedUser = entityManager.persist(user);
        CustomerModel customerModel = new CustomerModel();
        customerModel.setUser(persistedUser);
        customerModel.setCpf("123456789");
        entityManager.persist(customerModel);
    }

    @Test
    @DisplayName("Should return true when a CPF exists")
    public void testShouldReturnTrueWhenCPFExists() {
        insertDefaultData();

        boolean result = customerRepository.existsByCpf("123456789");

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when a CPF does not exist")
    public void testShouldReturnFalseWhenCPFDoesNotExist() {
        insertDefaultData();

        boolean result = customerRepository.existsByCpf("987654321");

        Assertions.assertFalse(result);
    }
}
