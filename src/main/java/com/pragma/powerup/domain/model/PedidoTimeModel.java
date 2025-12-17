package com.pragma.powerup.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PedidoTimeModel {

    private Long pedido;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    private Float tiempo;

}
