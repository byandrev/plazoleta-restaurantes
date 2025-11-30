package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.PlatoModel;

public interface IPlatoServicePort {

    void save(PlatoModel plato);

    PlatoModel getById(Long id);

}
