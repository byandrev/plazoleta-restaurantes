package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.PlatoModel;
import com.pragma.powerup.domain.spi.IPlatoPersistencePort;
import com.pragma.powerup.infrastructure.exception.NoDataFoundException;
import com.pragma.powerup.infrastructure.out.jpa.entity.PlatoEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IPlatoEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IPlatoRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class PlatoJpaAdapter implements IPlatoPersistencePort {

    private final IPlatoRepository platoRepository;
    private final IPlatoEntityMapper platoEntityMapper;

    @Override
    public PlatoModel save(PlatoModel platoModel) {
        PlatoEntity platoEntity = platoEntityMapper.toEntity(platoModel);
        return platoEntityMapper.toModel(platoRepository.save(platoEntity));
    }

    @Override
    public PlatoModel getById(Long id) {
        Optional<PlatoEntity> platoEntity = platoRepository.findById(id);

        if (platoEntity.isEmpty()) {
            throw new NoDataFoundException("El plato no existe");
        }

        return platoEntityMapper.toModel(platoEntity.get());
    }
}
