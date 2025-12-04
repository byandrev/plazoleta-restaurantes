package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.PlatoRequestDto;
import com.pragma.powerup.application.dto.request.PlatoUpdateDto;
import com.pragma.powerup.application.dto.response.PlatoResponseDto;

public interface IPlatoHandler {

    void save(Long userId, PlatoRequestDto platoRequestDto);

    PlatoResponseDto getById(Long id);

    PlatoResponseDto update(Long userId, Long id, PlatoUpdateDto platoUpdateDto);

}
