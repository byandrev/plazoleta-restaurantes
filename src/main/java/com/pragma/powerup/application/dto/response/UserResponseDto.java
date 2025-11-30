package com.pragma.powerup.application.dto.response;

import com.pragma.powerup.domain.model.RolModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserResponseDto {

    private Long id;

    private String nombre;

    private String apellido;

    private String numeroDocumento;

    private String celular;

    private LocalDate fechaNacimiento;

    private String correo;

    private RolModel rol;

}
