package com.megatech.store.security;

import com.megatech.store.domain.Role;
import org.springframework.security.core.GrantedAuthority;

public class UserAuthorization implements GrantedAuthority {
    private final Role role;

    public UserAuthorization(Role role) {
        this.role = role;
    }
    @Override
    public String getAuthority() {
        return role.name();
    }
}
