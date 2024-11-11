package com.megatech.store.domain;

import com.megatech.store.dtos.user.UserDTO;
import com.megatech.store.exceptions.InvalidProductFieldException;
import com.megatech.store.exceptions.InvalidUserFieldException;
import com.megatech.store.model.UserModel;

import java.util.Map;
import java.util.regex.Pattern;

public class User {

    public static final String EMAIL_PATTERN = "^([A-Za-z0-9._-])+@([A-Za-z0-9]+\\.)+[A-Za-z]{2,6}$";
    private static final Map<Pattern, String> passwordErrorMapping = Map.of(
            Pattern.compile("[A-Z]"), "password must contain at least one uppercase letter",
            Pattern.compile("[a-z]"), "password must contain at least one lowercase letter",
            Pattern.compile("[0-9]"), "password must contain at least one digit",
            Pattern.compile("[^A-Za-z0-9]"), "password must contain at least symbol"
    );
    public static final int MINIMUM_PASSWORD_SIZE = 8;

    private Long id;
    private String email;
    private String password;
    private Role role;

    public User(UserDTO userDTO, Role role) {
        setEmail(userDTO.email());
        setPassword(userDTO.password());
        setRole(role);
    }

    public User(UserModel userModel){
        setId(userModel.getId());
        setEmail(userModel.getEmail());
        setPassword(userModel.getPassword());
        setRole(userModel.getRole());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id == null) throw new InvalidProductFieldException("id cannot be null");
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (!isValidEmail(email)) throw new InvalidUserFieldException("email is not following the right pattern");
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null) {
            throw new InvalidUserFieldException("password cannot be null");
        }
        if (password.length() < MINIMUM_PASSWORD_SIZE) {
            throw new InvalidUserFieldException(String.format("password must be at least %d characters", MINIMUM_PASSWORD_SIZE));
        }

        StringBuilder errorMessageBuilder = new StringBuilder();
        for (Pattern pattern : passwordErrorMapping.keySet()) {
            if (!pattern.matcher(password).find()) {
                errorMessageBuilder.append(passwordErrorMapping.get(pattern));
            }
            errorMessageBuilder.append("\n");
        }
        String errorMessage = errorMessageBuilder.toString().strip();
        if (!errorMessage.isEmpty()) {
            throw new InvalidUserFieldException(errorMessage);
        }

        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        if (role == null) {
            throw new InvalidUserFieldException("role cannot be null");
        }
        this.role = role;
    }

    private static boolean isValidEmail(String email) {
        return Pattern.compile(EMAIL_PATTERN).matcher(email).find();
    }

}