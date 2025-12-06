package com.pragma.powerup.application.dto.response;

import com.pragma.powerup.domain.model.CategoriaModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlatoResponseDto {

    private Long id;

    private String nombre;

    private String descripcion;

    private Integer precio;

    private String urlImagen;

    private CategoriaModel categoria;

    private Long idRestaurante;

    private boolean activo;

}
