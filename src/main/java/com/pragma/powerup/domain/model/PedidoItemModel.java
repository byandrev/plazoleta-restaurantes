package com.pragma.powerup.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PedidoItemModel {

    private Long id;

    private Long platoId;

    private Long pedidoId;

    private Integer cantidad;

}
