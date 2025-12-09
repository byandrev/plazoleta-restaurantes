package com.pragma.powerup.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TraceabilityModel {

    private String id;

    private Long pedidoId;

    private Long clienteId;

    private String correoCliente;

    private LocalDateTime fecha;

    private PedidoEstado estadoAnterior;

    private PedidoEstado estadoNuevo;

    private Long empleadoId;

    private String correoEmpleado;

}
