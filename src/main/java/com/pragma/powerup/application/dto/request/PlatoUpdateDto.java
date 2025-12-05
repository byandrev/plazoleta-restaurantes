package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
public class PlatoUpdateDto {

    private String descripcion;

    @Min(value = 1, message = "El precio no puede ser menor a 1")
    private Integer precio;

    private Boolean activo;

}
