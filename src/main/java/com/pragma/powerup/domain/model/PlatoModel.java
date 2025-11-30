package com.pragma.powerup.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlatoModel {

    private Long id;

    private String nombre;

    private CategoriaModel categoria;

    private String descripcion;

    private Integer precio;

    private Long idRestaurante;

    private String urlImagen;

    private boolean activo;

}
