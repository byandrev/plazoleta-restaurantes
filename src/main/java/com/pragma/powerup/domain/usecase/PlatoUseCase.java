package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IPlatoServicePort;
import com.pragma.powerup.domain.model.PlatoModel;
import com.pragma.powerup.domain.spi.IPlatoPersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlatoUseCase implements IPlatoServicePort {

    private final IPlatoPersistencePort platoPersistencePort;

    @Override
    public void save(PlatoModel plato) {
        platoPersistencePort.save(plato);
    }

    @Override
    public PlatoModel getById(Long id) {
        return platoPersistencePort.getById(id);
    }

}
