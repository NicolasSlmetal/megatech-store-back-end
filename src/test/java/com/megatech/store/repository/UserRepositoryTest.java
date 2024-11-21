package com.megatech.store.repository;

import com.megatech.store.domain.Role;
import com.megatech.store.model.UserModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;

    private void insertDefaultData() {
        UserModel user = new UserModel();
        user.setEmail("email");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);
        entityManager.persist(user);
    }

    @Test
    @DisplayName("Should return true when email exists")
    public void shouldReturnTrueWhenEmailExists() {
        this.insertDefaultData();

        boolean result = userRepository.existsByEmail("email");

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    public void shouldReturnFalseWhenEmailDoesNotExist() {
        this.insertDefaultData();

        boolean result = userRepository.existsByEmail("email2");

        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Should return a user by your email")
    public void shouldReturnUserByEmail() {
        this.insertDefaultData();

        Optional<UserModel> result = userRepository.findByEmail("email");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("email", result.get().getEmail());
        Assertions.assertEquals("password", result.get().getPassword());
        Assertions.assertEquals(Role.CUSTOMER, result.get().getRole());
    }

    @Test
    @DisplayName("Should return a empty value when email does not exist")
    public void shouldReturnEmptyValueWhenEmailDoesNotExist() {
        this.insertDefaultData();

        Optional<UserModel> result = userRepository.findByEmail("email2");

        Assertions.assertFalse(result.isPresent());
    }
}
