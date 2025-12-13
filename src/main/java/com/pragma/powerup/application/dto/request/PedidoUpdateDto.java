package com.pragma.powerup.application.dto.request;

import com.pragma.powerup.domain.model.PedidoEstado;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class PedidoUpdateDto {

    @Schema(hidden = true)
    private Long id;

    private Long idChef;

    @NotNull(message = "El estado no puede estar vacio")
    private PedidoEstado estado;

}
