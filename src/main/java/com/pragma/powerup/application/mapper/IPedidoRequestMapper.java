package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.PedidoRequestDto;
import com.pragma.powerup.domain.model.PedidoModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IPedidoRequestMapper {

    PedidoModel toModel(PedidoRequestDto pedidoRequestDto);

}
