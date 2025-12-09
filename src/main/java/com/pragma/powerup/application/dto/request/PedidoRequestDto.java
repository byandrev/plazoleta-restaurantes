package com.pragma.powerup.application.dto.request;

import com.pragma.powerup.domain.model.UserModel;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
public class PedidoRequestDto {

    @NotNull(message = "El idCliente no puede estar vacio")
    private Long idCliente;

    private UserModel cliente;

    @NotNull(message = "El idChef no puede estar vacio")
    private Long idChef;

    @NotNull(message = "El idRestaurante no puede estar vacio")
    private Long idRestaurante;

    @Valid
    @NotNull(message = "Se necesita minimo un plato para hacer un pedido")
    @Size(min = 1, message = "Se necesita minimo un plato para hacer un pedido")
    private Set<PedidoItemRequestDto> items;

}
