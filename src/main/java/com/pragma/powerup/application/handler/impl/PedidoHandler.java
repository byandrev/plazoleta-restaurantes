package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.PedidoRequestDto;
import com.pragma.powerup.application.dto.response.PedidoResponseDto;
import com.pragma.powerup.application.handler.IPedidoHandler;
import com.pragma.powerup.application.mapper.IPedidoRequestMapper;
import com.pragma.powerup.application.mapper.IPedidoResponseMapper;
import com.pragma.powerup.domain.api.IPedidoServicePort;
import com.pragma.powerup.domain.model.PedidoModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PedidoHandler implements IPedidoHandler {

    private final IPedidoServicePort pedidoServicePort;

    private final IPedidoRequestMapper pedidoRequestMapper;

    private final IPedidoResponseMapper pedidoResponseMapper;

    @Override
    public PedidoResponseDto save(PedidoRequestDto pedidoDto) {
        PedidoModel pedidoModel = pedidoRequestMapper.toModel(pedidoDto);
        return pedidoResponseMapper.toResponse(pedidoServicePort.save(pedidoModel));
    }

    @Override
    public PedidoResponseDto getById(Long id) {
        return pedidoResponseMapper.toResponse(pedidoServicePort.getById(id));
    }

}
