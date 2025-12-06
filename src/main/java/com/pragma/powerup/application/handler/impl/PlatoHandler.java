package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.PlatoRequestDto;
import com.pragma.powerup.application.dto.request.PlatoUpdateDto;
import com.pragma.powerup.application.dto.response.PlatoResponseDto;
import com.pragma.powerup.application.handler.IPlatoHandler;
import com.pragma.powerup.application.mapper.IPlatoRequestMapper;
import com.pragma.powerup.application.mapper.IPlatoResponseMapper;
import com.pragma.powerup.domain.api.IPlatoServicePort;
import com.pragma.powerup.domain.model.PlatoModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PlatoHandler implements IPlatoHandler {

    private final IPlatoServicePort platoService;
    private final IPlatoRequestMapper platoRequestMapper;
    private final IPlatoResponseMapper platoResponseMapper;

    @Override
    public void save(Long userId, PlatoRequestDto platoRequestDto) {
        platoService.save(userId, platoRequestMapper.toModel(platoRequestDto));
    }

    @Override
    public Page<PlatoResponseDto> getAll(String categoria, Long restauranteId, int page, int size) {
        Page<PlatoModel> platosList = platoService.getAll(categoria, restauranteId, page, size);
        return platosList.map(platoResponseMapper::toResponse);
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
