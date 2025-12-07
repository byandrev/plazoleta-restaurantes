package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.PlatoModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Set;

public interface IPlatoPersistencePort {

    PlatoModel save(PlatoModel platoModel);

    Page<PlatoModel> getAll(Long restauranteId, PageRequest pageRequest);

    Page<PlatoModel> getAllByCategoria(String categoria, Long restauranteId, PageRequest pageRequest);

    PlatoModel getById(Long id);

    Set<Long> findNonExistentPlatoIds(Set<Long> ids);

}
