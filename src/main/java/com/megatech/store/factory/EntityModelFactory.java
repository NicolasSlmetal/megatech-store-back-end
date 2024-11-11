package com.megatech.store.factory;

import com.megatech.store.domain.Entity;
import com.megatech.store.dtos.InputDTO;
import com.megatech.store.model.Model;

public interface EntityModelFactory <T extends Entity<? extends InputDTO>, M extends Model, DTO extends InputDTO> {

    T createEntityFromDTO(DTO dto);
    T createEntityFromModel(M model);
    M createModelFromEntity(T entity);
}
