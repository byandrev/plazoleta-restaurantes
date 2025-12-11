package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.PedidoRequestDto;
import com.pragma.powerup.application.dto.response.PedidoResponseDto;
import com.pragma.powerup.domain.model.PedidoEstado;
import org.springframework.data.domain.Page;

public interface IPedidoHandler {

    PedidoResponseDto save(PedidoRequestDto pedidoDto);

    PedidoResponseDto getById(Long id);

    Page<PedidoResponseDto> getAll(Long userId, Long restaurantId, PedidoEstado estado, int page, int size);

}
