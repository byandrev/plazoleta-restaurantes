package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.CategoriaModel;
import com.pragma.powerup.domain.spi.ICategoriaPersistencePort;
import com.pragma.powerup.infrastructure.exception.NoDataFoundException;
import com.pragma.powerup.infrastructure.out.jpa.entity.CategoriaEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.ICategoriaEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.ICategoriaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class CategoriaJpaAdapter implements ICategoriaPersistencePort {

    private final ICategoriaRepository categoriaRepository;
    private final ICategoriaEntityMapper categoriaEntityMapper;

    @Override
    public CategoriaModel save(CategoriaModel categoriaModel) {
        CategoriaEntity categoriaEntity = categoriaEntityMapper.toEntity(categoriaModel);
        return categoriaEntityMapper.toModel(categoriaRepository.save(categoriaEntity));
    }

    @Override
    public CategoriaModel getByNombre(String nombre) {
        Optional<CategoriaEntity> categoriaEntity = categoriaRepository.findByNombre(nombre);

        if (categoriaEntity.isEmpty()) {
            throw new NoDataFoundException("La categoria no existe");
        }

        return categoriaEntityMapper.toModel(categoriaEntity.get());
    }

}
