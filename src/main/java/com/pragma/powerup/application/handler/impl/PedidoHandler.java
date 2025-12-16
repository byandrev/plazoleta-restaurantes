package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.PaginationRequestDto;
import com.pragma.powerup.application.dto.request.PedidoRequestDto;
import com.pragma.powerup.application.dto.response.PaginationResponseDto;
import com.pragma.powerup.application.dto.request.PedidoUpdateDto;
import com.pragma.powerup.application.dto.response.PedidoResponseDto;
import com.pragma.powerup.application.dto.response.PedidoTimeResponseDto;
import com.pragma.powerup.application.dto.response.TraceabilityResponseDto;
import com.pragma.powerup.application.handler.IPedidoHandler;
import com.pragma.powerup.application.mapper.*;
import com.pragma.powerup.domain.api.IPedidoServicePort;
import com.pragma.powerup.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoHandler implements IPedidoHandler {

    private final IPedidoServicePort pedidoService;
    private final IPedidoRequestMapper pedidoRequestMapper;
    private final IPedidoUpdateMapper pedidoUpdateMapper;
    private final IPedidoResponseMapper pedidoResponseMapper;
    private final IPedidoTimeResponseMapper timeResponseMapper;
    private final IPaginationResponseMapper paginationResponseMapper;
    private final IPaginationRequestMapper paginationRequestMapper;
    private final ITraceabilityResponseMapper traceabilityResponseMapper;

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
    public PedidoResponseDto cancel(UserModel client, Long pedidoId) {
        PedidoUpdateDto pedidoDto = PedidoUpdateDto.builder().id(pedidoId).build();
        PedidoModel pedidoModel = pedidoUpdateMapper.toModel(pedidoDto);
        return pedidoResponseMapper.toResponse(pedidoService.cancel(client, pedidoModel));
    }

    @Override
    public PedidoResponseDto getById(Long id) {
        return pedidoResponseMapper.toResponse(pedidoService.getById(id));
    }

    @Override
    public PaginationResponseDto<PedidoResponseDto> getAll(Long userId, Long restaurantId, PedidoEstado estado, PaginationRequestDto paginationRequest) {
        PaginationResult<PedidoModel> pedidosList = pedidoService.getAll(userId, restaurantId, estado, paginationRequestMapper.toModel(paginationRequest));
        PaginationResult<PedidoResponseDto> result = pedidosList.map(pedidoResponseMapper::toResponse);
        return paginationResponseMapper.toResponse(result);
    }

    @Override
    public List<TraceabilityResponseDto> getHistory(Long pedidoId) {
        List<TraceabilityModel> list = pedidoService.getHistory(pedidoId);
        return list.stream().map(traceabilityResponseMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public PaginationResponseDto<PedidoTimeResponseDto> getTimePedidos(Long restaurantId, PaginationRequestDto pagination) {
        PaginationResult<PedidoTimeModel> timeList = pedidoService.getTimePedidos(restaurantId, paginationRequestMapper.toModel(pagination));
        PaginationResult<PedidoTimeResponseDto> result = timeList.map(timeResponseMapper::toResponse);
        return paginationResponseMapper.toResponse(result);
    }

}
