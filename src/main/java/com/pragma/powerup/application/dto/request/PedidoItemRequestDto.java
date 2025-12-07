package com.pragma.powerup.application.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class PedidoItemRequestDto {

    @NotNull(message = "Se nececita el id del plato para hacer el pedido") private Long platoId;

    @NotNull(message = "La cantidad no puede estar vacia")
    @Min(value = 1, message = "Se necesita minimo un plato para hacer un pedido")
    private Integer cantidad;

}
