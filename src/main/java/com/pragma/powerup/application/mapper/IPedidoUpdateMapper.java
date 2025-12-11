package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.PedidoUpdateDto;
import com.pragma.powerup.domain.model.PedidoModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IPedidoUpdateMapper {

    PedidoModel toModel(PedidoUpdateDto pedidoUpdateDto);

}
