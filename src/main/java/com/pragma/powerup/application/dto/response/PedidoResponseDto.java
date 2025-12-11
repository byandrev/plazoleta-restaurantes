package com.pragma.powerup.application.dto.response;

import com.pragma.powerup.domain.model.PedidoEstado;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PedidoResponseDto {

    private Long id;

    private PedidoEstado estado;

    private LocalDateTime fecha;

    private Long idCliente;

    private Long idChef;

    private Long idRestaurante;

}
