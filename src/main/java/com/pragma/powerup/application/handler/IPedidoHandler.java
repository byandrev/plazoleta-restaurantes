package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.PaginationRequestDto;
import com.pragma.powerup.application.dto.request.PedidoRequestDto;
import com.pragma.powerup.application.dto.response.*;
import com.pragma.powerup.application.dto.request.PedidoUpdateDto;
import com.pragma.powerup.domain.model.PedidoEstado;
import com.pragma.powerup.domain.model.PedidoTimeModel;
import com.pragma.powerup.domain.model.UserModel;

import java.util.List;

public interface IPedidoHandler {

    PedidoResponseDto save(UserModel client, PedidoRequestDto pedidoDto);

    PedidoResponseDto update(UserModel employee, PedidoUpdateDto pedidoDto);

    PedidoResponseDto cancel(UserModel client, Long pedidoId);

    PedidoResponseDto getById(Long id);

    PaginationResponseDto<PedidoResponseDto> getAll(Long userId, Long restaurantId, PedidoEstado estado, PaginationRequestDto paginationRequest);

    List<TraceabilityResponseDto> getHistory(Long pedidoId);

    PaginationResponseDto<PedidoTimeResponseDto> getTimePedidos(Long userId, Long restaurantId, PaginationRequestDto pagination);

    PaginationResponseDto<EmpleadoTiempoResponseDto> getTimeEmpleados(Long userId, Long restaurantId, PaginationRequestDto pagination);

}
