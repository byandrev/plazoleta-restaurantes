package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.infrastructure.exception.ResourceNotFound;
import com.pragma.powerup.domain.model.PlatoModel;
import com.pragma.powerup.domain.spi.IPlatoPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.PlatoEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IPlatoEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IPlatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
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
    public Page<PlatoModel> getAll(Long restauranteId, PageRequest pageRequest) {
        Page<PlatoEntity> page = platoRepository.findAllByRestaurante_Id(restauranteId, pageRequest);
        return page.map(platoEntityMapper::toModel);
    }

    @Override
    public Page<PlatoModel> getAllByCategoria(String categoria, Long restauranteId, PageRequest pageRequest) {
        Page<PlatoEntity> page = platoRepository.findByRestaurante_IdAndCategoria_Nombre(restauranteId, categoria, pageRequest);
        return page.map(platoEntityMapper::toModel);
    }

    @Override
    public PlatoModel getById(Long id) {
        PlatoEntity platoEntity = platoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("El plato no existe"));
        return platoEntityMapper.toModel(platoEntity);
    }

    @Override
    public Set<Long> findNonExistentPlatoIds(Long restaurantId, Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptySet();

        List<Long> foundIds = platoRepository.findAllIdsByIds(restaurantId, ids);
        Set<Long> missingIds = new HashSet<>(ids);
        missingIds.removeAll(foundIds);

        return missingIds;
    }

}
