package com.pragma.powerup.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmpleadoTiempoResponseDto {

    private Long empleado;

    private Double tiempoMedioSegundos;

}

