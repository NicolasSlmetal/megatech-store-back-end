package com.megatech.store.controller;

import com.megatech.store.dtos.login.LoginDTO;
import com.megatech.store.dtos.login.TokenDTO;
import com.megatech.store.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginDTO loginDTO) {
        return ResponseEntity.ok(userService.login(loginDTO));
    }
}
