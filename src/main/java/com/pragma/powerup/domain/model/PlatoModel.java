package com.pragma.powerup.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlatoModel {

    private Long id;

    private String nombre;

    private CategoriaModel categoria;

    private String descripcion;

    private Integer precio;

    private Long idRestaurante;

    private RestaurantModel restaurant;

    private String urlImagen;

    private Boolean activo;

}
