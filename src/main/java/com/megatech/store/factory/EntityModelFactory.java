package com.megatech.store.factory;

import com.megatech.store.dtos.InputDTO;
import com.megatech.store.model.Model;

public interface EntityModelFactory <T, M extends Model, DTO extends InputDTO> {

    T createEntityFromDTO(DTO dto);
    T createEntityFromModel(M model);
    M createModelFromEntity(T entity);
}
