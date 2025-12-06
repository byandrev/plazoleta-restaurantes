package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.PlatoRequestDto;
import com.pragma.powerup.application.dto.request.PlatoUpdateDto;
import com.pragma.powerup.application.dto.response.PlatoResponseDto;
import org.springframework.data.domain.Page;

public interface IPlatoHandler {

    void save(Long userId, PlatoRequestDto platoRequestDto);

    Page<PlatoResponseDto> getAll(Long restauranteId, int page, int size);

    PlatoResponseDto getById(Long id);

    PlatoResponseDto update(Long userId, Long id, PlatoUpdateDto platoUpdateDto);

}
