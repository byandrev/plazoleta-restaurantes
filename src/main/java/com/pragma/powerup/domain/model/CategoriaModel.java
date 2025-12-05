package com.pragma.powerup.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CategoriaModel {

    private Long id;

    private String nombre;

    private String descripcion;

}
