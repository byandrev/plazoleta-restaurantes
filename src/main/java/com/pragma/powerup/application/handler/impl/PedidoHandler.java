package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.PedidoRequestDto;
import com.pragma.powerup.application.dto.request.PedidoUpdateDto;
import com.pragma.powerup.application.dto.response.PedidoResponseDto;
import com.pragma.powerup.application.handler.IPedidoHandler;
import com.pragma.powerup.application.mapper.IPedidoRequestMapper;
import com.pragma.powerup.application.mapper.IPedidoResponseMapper;
import com.pragma.powerup.application.mapper.IPedidoUpdateMapper;
import com.pragma.powerup.domain.api.IPedidoServicePort;
import com.pragma.powerup.domain.model.PedidoEstado;
import com.pragma.powerup.domain.model.PedidoModel;
import com.pragma.powerup.domain.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PedidoHandler implements IPedidoHandler {

    private final IPedidoServicePort pedidoService;

    private final IPedidoRequestMapper pedidoRequestMapper;

    private final IPedidoUpdateMapper pedidoUpdateMapper;

    private final IPedidoResponseMapper pedidoResponseMapper;

    @Override
    public PedidoResponseDto save(UserModel client, PedidoRequestDto pedidoDto) {
        PedidoModel pedidoModel = pedidoRequestMapper.toModel(pedidoDto);
        return pedidoResponseMapper.toResponse(pedidoService.save(client, pedidoModel));
    }

    @Override
    public PedidoResponseDto update(UserModel employee, PedidoUpdateDto pedidoDto) {
        PedidoModel pedidoModel = pedidoUpdateMapper.toModel(pedidoDto);
        return pedidoResponseMapper.toResponse(pedidoService.update(employee, pedidoModel));
    }

    @Override
    public PedidoResponseDto getById(Long id) {
        return pedidoResponseMapper.toResponse(pedidoService.getById(id));
    }

    @Override
    public Page<PedidoResponseDto> getAll(Long userId, Long restaurantId, PedidoEstado estado, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("fecha").ascending());
        Page<PedidoModel> pedidosList = pedidoService.getAll(userId, restaurantId, estado, pageRequest);
        return pedidosList.map(pedidoResponseMapper::toResponse);
    }

}
