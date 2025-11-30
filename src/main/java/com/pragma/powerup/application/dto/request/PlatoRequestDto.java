package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PlatoRequestDto {

    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;

    @NotBlank(message = "La descripcion no puede estar vacia")
    private String descripcion;

    @NotNull(message = "El precio no puede estar vacio")
    @Min(value = 1, message = "El precio no puede ser menor a 1")
    private Integer precio;

    @NotBlank(message = "La url_imagen no puede estar vacia")
    private String urlImagen;

    @NotNull(message = "La id_categoria no puede estar vacio")
    private Long idCategoria;

    @NotNull(message = "El id_restaurante no puede estar vacio")
    private Long idRestaurante;

}
