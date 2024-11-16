package com.megatech.store.service;

import com.megatech.store.dtos.login.LoginDTO;
import com.megatech.store.dtos.login.TokenDTO;
import com.megatech.store.exceptions.InvalidUserFieldException;
import com.megatech.store.model.UserModel;
import com.megatech.store.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    public UserService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public TokenDTO login(LoginDTO loginDTO) {
        UserModel user = userRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new InvalidUserFieldException("Invalid email or password"));

        if (!user.getPassword().equals(loginDTO.password())) {
            throw new InvalidUserFieldException("Invalid email or password");
        }

        return new TokenDTO(tokenService.generateToken(user.getEmail(), user.getId()), user.getRole(), user.getId());
    }

    public void verifyAuth(String token) {
        token = token.replace("Bearer ", "");
        tokenService.verifyToken(token);
    }

    public void validateIfEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new InvalidUserFieldException("Email %s are invalid".formatted(email));
        }
    }

}
