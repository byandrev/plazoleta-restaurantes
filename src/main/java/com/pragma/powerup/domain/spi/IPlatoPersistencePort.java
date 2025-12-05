package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.PlatoModel;

public interface IPlatoPersistencePort {

    PlatoModel save(PlatoModel platoModel);

    PlatoModel getById(Long id);

}
