package com.megatech.store.security;

import com.megatech.store.domain.User;
import com.megatech.store.dtos.user.UserDTO;
import com.megatech.store.exceptions.EntityNotFoundException;
import com.megatech.store.exceptions.ErrorType;
import com.megatech.store.factory.EntityModelFactory;
import com.megatech.store.model.UserModel;
import com.megatech.store.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationService implements UserDetailsService {

    private final UserRepository userRepository;
    private final EntityModelFactory<User, UserModel, UserDTO> userFactory;

    public UserAuthenticationService(UserRepository userRepository, EntityModelFactory<User, UserModel, UserDTO> userFactory) {
        this.userRepository = userRepository;
        this.userFactory = userFactory;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userFactory
                .createEntityFromModel(userRepository.findByEmail(username)
                        .orElseThrow(() -> new EntityNotFoundException("Invalid email", ErrorType.INVALID_REQUEST)));
        return new UserAuthentication(user);
    }
}
