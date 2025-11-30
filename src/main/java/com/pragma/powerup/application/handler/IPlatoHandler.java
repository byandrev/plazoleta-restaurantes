package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.PlatoRequestDto;
import com.pragma.powerup.application.dto.response.PlatoResponseDto;

public interface IPlatoHandler {

    void save(PlatoRequestDto platoRequestDto);

    PlatoResponseDto getById(Long id);

}
