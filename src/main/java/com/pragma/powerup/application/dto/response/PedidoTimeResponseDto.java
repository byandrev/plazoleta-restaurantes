package com.pragma.powerup.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PedidoTimeResponseDto {

    private Long pedido;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    private Float tiempo;

}
