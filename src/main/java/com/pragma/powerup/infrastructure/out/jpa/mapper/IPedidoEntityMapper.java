package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.PedidoModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.PedidoEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.RestaurantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {IPedidoItemMapper.class})
public interface IPedidoEntityMapper {

    PedidoEntity toEntity(PedidoModel pedidoModel);

    @Mapping(target = "idRestaurante", source = "restaurante", qualifiedByName = "restaurantToLong")
    PedidoModel toModel(PedidoEntity pedidoEntity);

    @Named("restaurantToLong")
    default Long restaurantToLong(RestaurantEntity restaurantEntity) {
        return restaurantEntity.getId();
    }
}
