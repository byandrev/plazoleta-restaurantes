package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.infrastructure.exception.ResourceNotFound;
import com.pragma.powerup.domain.model.CategoriaModel;
import com.pragma.powerup.domain.spi.ICategoriaPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.CategoriaEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.ICategoriaEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.ICategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
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
        CategoriaEntity categoriaEntity = categoriaRepository.findByNombre(nombre)
                .orElseThrow(() -> new ResourceNotFound("La categoria no existe"));
        return categoriaEntityMapper.toModel(categoriaEntity);
    }

}
