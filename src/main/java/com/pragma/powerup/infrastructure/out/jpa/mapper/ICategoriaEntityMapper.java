package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.CategoriaModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.CategoriaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface ICategoriaEntityMapper {

    CategoriaEntity toEntity(CategoriaModel categoriaModel);

    CategoriaModel toModel(CategoriaEntity categoriaEntity);

}
