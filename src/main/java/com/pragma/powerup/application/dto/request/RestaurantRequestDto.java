package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantRequestDto {

    private String nombre;

    private String direccion;

    private String telefono;

    private String urlLogo;

    private String nit;

    private Long idPropietario;

}
