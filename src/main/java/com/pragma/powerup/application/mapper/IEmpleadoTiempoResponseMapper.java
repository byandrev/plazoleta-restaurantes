package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.EmpleadoTiempoResponseDto;
import com.pragma.powerup.domain.model.EmpleadoTiempoModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IEmpleadoTiempoResponseMapper {

    EmpleadoTiempoResponseDto toResponse(EmpleadoTiempoModel model);

}
