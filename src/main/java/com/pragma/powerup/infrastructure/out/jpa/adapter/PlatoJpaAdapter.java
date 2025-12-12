package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.PaginationInfo;
import com.pragma.powerup.domain.model.PaginationResult;
import com.pragma.powerup.domain.model.PlatoModel;
import com.pragma.powerup.domain.spi.IPlatoPersistencePort;
import com.pragma.powerup.infrastructure.exception.ResourceNotFound;
import com.pragma.powerup.infrastructure.out.jpa.entity.PlatoEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IPaginationMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IPlatoEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IPlatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class PlatoJpaAdapter implements IPlatoPersistencePort {

    private final IPlatoRepository platoRepository;
    private final IPlatoEntityMapper platoEntityMapper;
    private final IPaginationMapper paginationMapper;

    @Override
    public PlatoModel save(PlatoModel platoModel) {
        PlatoEntity platoEntity = platoEntityMapper.toEntity(platoModel);
        return platoEntityMapper.toModel(platoRepository.save(platoEntity));
    }

    @Override
    public PaginationResult<PlatoModel> getAll(Long restauranteId, PaginationInfo pagination) {
        Page<PlatoEntity> page = platoRepository.findAllByRestaurante_Id(restauranteId, paginationMapper.toPageable(pagination));
        return paginationMapper.toModel(page.map(platoEntityMapper::toModel));
    }

    @Override
    public PaginationResult<PlatoModel> getAllByCategoria(String categoria, Long restauranteId, PaginationInfo pagination) {
        Page<PlatoEntity> page = platoRepository.findByRestaurante_IdAndCategoria_Nombre(
                restauranteId, categoria, paginationMapper.toPageable(pagination)
        );
        return paginationMapper.toModel(page.map(platoEntityMapper::toModel));
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
