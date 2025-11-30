package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.PlatoRequestDto;
import com.pragma.powerup.application.dto.request.PlatoUpdateDto;
import com.pragma.powerup.domain.model.CategoriaModel;
import com.pragma.powerup.domain.model.PlatoModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface IPlatoRequestMapper {

    @Mapping(target = "categoria", source = "categoria", qualifiedByName = "stringToCategoriaModel")
    PlatoModel toModel(PlatoRequestDto platoRequestDto);

    PlatoModel toModel(PlatoUpdateDto platoUpdateDto);

    @Named("stringToCategoriaModel")
    default CategoriaModel stringToCategoriaModel(String categoria) {
        if (categoria == null) {
            return null;
        }

        CategoriaModel categoriaModel = new CategoriaModel();
        categoriaModel.setNombre(categoria.toUpperCase());
        return categoriaModel;
    }

}
