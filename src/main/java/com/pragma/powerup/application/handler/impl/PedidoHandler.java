package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.PedidoRequestDto;
import com.pragma.powerup.application.dto.response.PaginationResponseDto;
import com.pragma.powerup.application.dto.response.PedidoResponseDto;
import com.pragma.powerup.application.handler.IPedidoHandler;
import com.pragma.powerup.application.mapper.IPaginationResponseMapper;
import com.pragma.powerup.application.mapper.IPedidoRequestMapper;
import com.pragma.powerup.application.mapper.IPedidoResponseMapper;
import com.pragma.powerup.domain.api.IPedidoServicePort;
import com.pragma.powerup.domain.model.PaginationResult;
import com.pragma.powerup.domain.model.PedidoEstado;
import com.pragma.powerup.domain.model.PedidoModel;
import com.pragma.powerup.domain.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PedidoHandler implements IPedidoHandler {

    private final IPedidoServicePort pedidoService;

    private final IPedidoRequestMapper pedidoRequestMapper;

    private final IPedidoResponseMapper pedidoResponseMapper;

    private final IPaginationResponseMapper paginationResponseMapper;

    @Override
    public PedidoResponseDto save(UserModel client, PedidoRequestDto pedidoDto) {
        PedidoModel pedidoModel = pedidoRequestMapper.toModel(pedidoDto);
        return pedidoResponseMapper.toResponse(pedidoService.save(client, pedidoModel));
    }

    @Override
    public PedidoResponseDto getById(Long id) {
        return pedidoResponseMapper.toResponse(pedidoService.getById(id));
    }

    @Override
    public PaginationResponseDto<PedidoResponseDto> getAll(Long userId, Long restaurantId, PedidoEstado estado, int page, int size) {
        PaginationResult<PedidoModel> pedidosList = pedidoService.getAll(userId, restaurantId, estado, page, size, "fecha");
        PaginationResult<PedidoResponseDto> result = pedidosList.map(pedidoResponseMapper::toResponse);
        return paginationResponseMapper.toResponse(result);
    }

}
