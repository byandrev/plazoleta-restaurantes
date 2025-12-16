package com.pragma.powerup.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PedidoTimeModel {

    private Long pedido;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    private Float tiempo;

}
