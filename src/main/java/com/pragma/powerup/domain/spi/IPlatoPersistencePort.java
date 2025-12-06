package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.PlatoModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IPlatoPersistencePort {

    PlatoModel save(PlatoModel platoModel);

    Page<PlatoModel> getAll(Long restauranteId, PageRequest pageRequest);

    Page<PlatoModel> getAllByCategoria(String categoria, Long restauranteId, PageRequest pageRequest);

    PlatoModel getById(Long id);

}
