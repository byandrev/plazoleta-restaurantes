package com.pragma.powerup.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmpleadoTiempoModel {

    private Long empleado;

    private Double tiempoMedioSegundos;

}
