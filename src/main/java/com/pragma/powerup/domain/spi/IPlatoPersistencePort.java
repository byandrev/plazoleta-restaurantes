package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.PaginationInfo;
import com.pragma.powerup.domain.model.PaginationResult;
import com.pragma.powerup.domain.model.PlatoModel;

import java.util.Set;

public interface IPlatoPersistencePort {

    PlatoModel save(PlatoModel platoModel);

    PaginationResult<PlatoModel> getAll(Long restauranteId, PaginationInfo pagination);

    PaginationResult<PlatoModel> getAllByCategoria(String categoria, Long restauranteId, PaginationInfo pagination);

    PlatoModel getById(Long id);

    Set<Long> findNonExistentPlatoIds(Long restaurantId, Set<Long> ids);

}
