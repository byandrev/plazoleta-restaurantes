package com.pragma.powerup.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Builder
public class RestaurantRequestDto {

    @NotBlank(message = "El nombre del restaurante es obligatorio")
    @Pattern(
            regexp = "^(?!\\d+$).+",
            message = "El nombre no puede estar compuesto solo por números"
    )
    private String nombre;

    @NotBlank(message = "La direccion no puede estar vacia")
    private String direccion;

    @NotBlank(message = "El telefono no puede estar vacio")
    @Pattern(
        regexp = "^(\\+\\d{1,12}|\\d{1,13})$",
        message = "Teléfono inválido. Máx 13 caracteres y puede iniciar con +"
    )
    private String telefono;

    @NotBlank(message = "El urlLogo no puede estar vacio")
    private String urlLogo;

    @NotBlank(message = "El NIT no puede estar vacio")
    @Pattern(
        regexp = "^[0-9]+$",
        message = "El NIT debe contener solo números"
    )
    private String nit;

    private Long idPropietario;

}
