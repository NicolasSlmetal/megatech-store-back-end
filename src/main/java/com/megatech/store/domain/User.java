package com.megatech.store.domain;

import com.megatech.store.dtos.user.UserDTO;
import com.megatech.store.exceptions.ErrorType;
import com.megatech.store.exceptions.InvalidProductFieldException;
import com.megatech.store.exceptions.InvalidUserFieldException;
import com.megatech.store.model.UserModel;

import java.util.Map;
import java.util.regex.Pattern;

public class User implements Entity<UserDTO>, Cloneable {

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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void update(UserDTO updateDTO) {
        if (updateDTO.email() != null) {
            setEmail(updateDTO.email());
        }
        if (updateDTO.password() != null) {
            setPassword(updateDTO.password());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id == null) throw new InvalidProductFieldException("id cannot be null", ErrorType.INVALID_ID);
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null) {
            throw new InvalidUserFieldException("email cannot be null", ErrorType.INVALID_USER_EMAIL);
        }
        if (!isValidEmail(email)) {
            throw new InvalidUserFieldException("email is not following the right pattern", ErrorType.INVALID_USER_EMAIL);
        }
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null) {
            throw new InvalidUserFieldException("password cannot be null", ErrorType.INVALID_USER_PASSWORD);
        }
        if (password.length() < MINIMUM_PASSWORD_SIZE) {
            String templateErrorMessage = String.format("password must be at least %d characters", MINIMUM_PASSWORD_SIZE);
            throw new InvalidUserFieldException(templateErrorMessage, ErrorType.INVALID_USER_PASSWORD);
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
            throw new InvalidUserFieldException(errorMessage, ErrorType.INVALID_USER_PASSWORD);
        }

        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        if (role == null) {
            throw new InvalidUserFieldException("role cannot be null", ErrorType.INVALID_USER_ROLE);
        }
        this.role = role;
    }

    private static boolean isValidEmail(String email) {
        return Pattern.compile(EMAIL_PATTERN).matcher(email).find();
    }


}
