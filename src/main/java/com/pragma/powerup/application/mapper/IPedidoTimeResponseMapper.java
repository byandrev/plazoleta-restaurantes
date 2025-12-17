package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.PedidoTimeResponseDto;
import com.pragma.powerup.domain.model.PedidoTimeModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IPedidoTimeResponseMapper {

    PedidoTimeResponseDto toResponse(PedidoTimeModel pedidoTimeModel);

}
