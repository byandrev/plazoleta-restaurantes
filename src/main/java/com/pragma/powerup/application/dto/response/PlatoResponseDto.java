package com.pragma.powerup.application.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlatoResponseDto {

    private Long id;

    private String nombre;

    private String descripcion;

    private Integer precio;

    private String urlImagen;

    private Long idCategoria;

    private Long idRestaurante;

    private boolean activo;

}
