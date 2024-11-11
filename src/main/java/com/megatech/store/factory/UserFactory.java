package com.megatech.store.factory;

import com.megatech.store.domain.User;
import com.megatech.store.dtos.user.UserDTO;
import com.megatech.store.model.UserModel;
import org.springframework.stereotype.Component;

@Component
public class UserFactory implements EntityModelFactory<User, UserModel, UserDTO> {
    @Override
    public User createEntityFromDTO(UserDTO dto) {
        User user = new User();
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        return user;
    }

    @Override
    public User createEntityFromModel(UserModel model) {
        User user = new User();
        user.setId(model.getId());
        user.setEmail(model.getEmail());
        user.setPassword(model.getPassword());
        user.setRole(model.getRole());
        return user;
    }

    @Override
    public UserModel createModelFromEntity(User entity) {
        UserModel model = new UserModel();
        model.setEmail(entity.getEmail());
        model.setPassword(entity.getPassword());
        model.setRole(entity.getRole());
        return model;
    }
}
