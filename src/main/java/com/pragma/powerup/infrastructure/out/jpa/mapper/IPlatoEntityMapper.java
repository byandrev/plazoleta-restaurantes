package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.PlatoModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.PlatoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface IPlatoEntityMapper {

    PlatoEntity toEntity(PlatoModel platoModel);

    PlatoModel toModel(PlatoEntity platoEntity);

}
