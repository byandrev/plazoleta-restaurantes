package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.PlatoModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IPlatoServicePort {

    PlatoModel save(Long userId, PlatoModel plato);

    Page<PlatoModel> getAll(String categoria, Long restauranteId, PageRequest pageRequest);

    PlatoModel getById(Long id);

    PlatoModel update(Long userId, Long id, PlatoModel plato);

}
