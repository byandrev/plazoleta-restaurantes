package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.PaginationInfo;
import com.pragma.powerup.domain.model.PaginationResult;
import com.pragma.powerup.domain.model.PlatoModel;

public interface IPlatoServicePort {

    PlatoModel save(Long userId, PlatoModel plato);

    PaginationResult<PlatoModel> getAll(String categoria, Long restauranteId, PaginationInfo pagination);

    PlatoModel getById(Long id);

    PlatoModel update(Long userId, Long id, PlatoModel plato);

}
