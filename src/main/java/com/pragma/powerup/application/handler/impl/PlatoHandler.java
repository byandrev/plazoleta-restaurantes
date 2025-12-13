package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.PaginationRequestDto;
import com.pragma.powerup.application.dto.request.PlatoRequestDto;
import com.pragma.powerup.application.dto.request.PlatoUpdateDto;
import com.pragma.powerup.application.dto.response.PaginationResponseDto;
import com.pragma.powerup.application.dto.response.PlatoResponseDto;
import com.pragma.powerup.application.handler.IPlatoHandler;
import com.pragma.powerup.application.mapper.IPaginationRequestMapper;
import com.pragma.powerup.application.mapper.IPaginationResponseMapper;
import com.pragma.powerup.application.mapper.IPlatoRequestMapper;
import com.pragma.powerup.application.mapper.IPlatoResponseMapper;
import com.pragma.powerup.domain.api.IPlatoServicePort;
import com.pragma.powerup.domain.model.PaginationResult;
import com.pragma.powerup.domain.model.PlatoModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlatoHandler implements IPlatoHandler {

    private final IPlatoServicePort platoService;
    private final IPlatoRequestMapper platoRequestMapper;
    private final IPlatoResponseMapper platoResponseMapper;
    private final IPaginationResponseMapper paginationResponseMapper;
    private final IPaginationRequestMapper paginationRequestMapper;

    @Override
    public void save(Long userId, PlatoRequestDto platoRequestDto) {
        platoService.save(userId, platoRequestMapper.toModel(platoRequestDto));
    }

    @Override
    public PaginationResponseDto<PlatoResponseDto> getAll(String categoria, Long restauranteId, PaginationRequestDto paginationRequest) {
        PaginationResult<PlatoModel> platosList = platoService.getAll(
                categoria, restauranteId, paginationRequestMapper.toModel(paginationRequest)
        );

        return paginationResponseMapper.toResponse(platosList.map(platoResponseMapper::toResponse));
    }

    @Override
    public PlatoResponseDto getById(Long id) {
        return platoResponseMapper.toResponse(platoService.getById(id));
    }

    @Override
    public PlatoResponseDto update(Long userId, Long id, PlatoUpdateDto platoUpdateDto) {
        PlatoModel platoModel = platoRequestMapper.toModel(platoUpdateDto);
        return platoResponseMapper.toResponse(platoService.update(userId, id, platoModel));
    }

}
