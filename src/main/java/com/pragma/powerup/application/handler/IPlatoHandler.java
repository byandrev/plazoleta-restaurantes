package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.PaginationRequestDto;
import com.pragma.powerup.application.dto.request.PlatoRequestDto;
import com.pragma.powerup.application.dto.request.PlatoUpdateDto;
import com.pragma.powerup.application.dto.response.PaginationResponseDto;
import com.pragma.powerup.application.dto.response.PlatoResponseDto;

public interface IPlatoHandler {

    void save(Long userId, PlatoRequestDto platoRequestDto);

    PaginationResponseDto<PlatoResponseDto> getAll(String categoria, Long restauranteId, PaginationRequestDto paginationRequest);

    PlatoResponseDto getById(Long id);

    PlatoResponseDto update(Long userId, Long id, PlatoUpdateDto platoUpdateDto);

}
