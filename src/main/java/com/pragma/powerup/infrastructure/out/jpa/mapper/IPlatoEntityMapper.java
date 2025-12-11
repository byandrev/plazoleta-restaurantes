package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.PlatoModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.PlatoEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.RestaurantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface IPlatoEntityMapper {

    PlatoEntity toEntity(PlatoModel platoModel);

    @Mapping(target = "idRestaurante", source = "restaurante", qualifiedByName = "restaurantToLong")
    PlatoModel toModel(PlatoEntity platoEntity);

    @Named("restaurantToLong")
    default Long restaurantToLong(RestaurantEntity restaurantEntity) {
        return restaurantEntity.getId();
    }

}
