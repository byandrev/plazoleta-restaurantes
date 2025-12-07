package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.PedidoResponseDto;
import com.pragma.powerup.domain.model.PedidoModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IPedidoResponseMapper {

    PedidoResponseDto toResponse(PedidoModel pedidoModel);

}
