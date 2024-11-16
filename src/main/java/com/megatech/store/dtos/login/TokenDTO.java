package com.megatech.store.dtos.login;

import com.megatech.store.domain.Role;

public record TokenDTO (
        String token,
        Role role,
        Long id
) {
}
