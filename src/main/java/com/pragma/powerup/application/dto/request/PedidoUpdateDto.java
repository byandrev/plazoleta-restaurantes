package com.pragma.powerup.application.dto.request;

import com.pragma.powerup.domain.model.PedidoEstado;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class PedidoUpdateDto {

    @Schema(hidden = true)
    private Long id;

    @NotNull(message = "El estado no puede estar vacio")
    private PedidoEstado estado;

    @Size(min = 6, max = 6, message = "El tamaño del PIN debe ser 6 digitos")
    private String pin;

}
