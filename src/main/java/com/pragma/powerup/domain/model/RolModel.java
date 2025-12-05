package com.pragma.powerup.domain.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RolModel {

    private Long id;
    private RolType nombre;
    private String descripcion;

}