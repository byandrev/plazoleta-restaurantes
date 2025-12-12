package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.PaginationRequestDto;
import com.pragma.powerup.application.dto.request.PedidoRequestDto;
import com.pragma.powerup.application.dto.response.PaginationResponseDto;
import com.pragma.powerup.application.dto.request.PedidoUpdateDto;
import com.pragma.powerup.application.dto.response.PedidoResponseDto;
import com.pragma.powerup.domain.model.PedidoEstado;
import com.pragma.powerup.domain.model.UserModel;

public interface IPedidoHandler {

    PedidoResponseDto save(UserModel client, PedidoRequestDto pedidoDto);

    PedidoResponseDto update(UserModel employee, PedidoUpdateDto pedidoDto);

    PedidoResponseDto getById(Long id);

    PaginationResponseDto<PedidoResponseDto> getAll(Long userId, Long restaurantId, PedidoEstado estado, PaginationRequestDto paginationRequest);

}
