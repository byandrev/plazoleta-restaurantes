package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.PlatoModel;

public interface IPlatoServicePort {

    PlatoModel save(Long userId, PlatoModel plato);

    PlatoModel getById(Long id);

    PlatoModel update(Long userId, Long id, PlatoModel plato);

}
