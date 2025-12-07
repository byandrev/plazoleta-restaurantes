package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.PedidoRequestDto;
import com.pragma.powerup.application.dto.response.PedidoResponseDto;

public interface IPedidoHandler {

    PedidoResponseDto save(PedidoRequestDto pedidoDto);

    PedidoResponseDto getById(Long id);

}
