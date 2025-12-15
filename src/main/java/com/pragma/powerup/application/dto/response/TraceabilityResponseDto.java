package com.pragma.powerup.application.dto.response;

import com.pragma.powerup.domain.model.PedidoEstado;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TraceabilityResponseDto {

    private String id;

    private Long pedidoId;

    private Long clienteId;

    private String correoCliente;

    private LocalDateTime fecha;

    private PedidoEstado estadoAnterior;

    private PedidoEstado estadoNuevo;

}
