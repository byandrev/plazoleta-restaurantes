package com.pragma.powerup.application.dto.request;

import com.pragma.powerup.domain.model.PedidoEstado;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TraceabilityRequestDto {

    private Long pedidoId;

    private Long clienteId;

    private String correoCliente;

    private PedidoEstado estadoAnterior;

    private PedidoEstado estadoNuevo;

    private Long empleadoId;

    private String correoEmpleado;

}
