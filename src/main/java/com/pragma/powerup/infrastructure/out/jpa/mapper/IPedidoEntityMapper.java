package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.PedidoModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.PedidoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {IPedidoItemMapper.class})
public interface IPedidoEntityMapper {

    PedidoEntity toEntity(PedidoModel pedidoModel);

    PedidoModel toModel(PedidoEntity pedidoEntity);

}
