package com.megatech.store.service;

import com.megatech.store.exceptions.InvalidUserFieldException;
import com.megatech.store.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateIfEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new InvalidUserFieldException("Email %s are invalid".formatted(email));
        }
    }

}
