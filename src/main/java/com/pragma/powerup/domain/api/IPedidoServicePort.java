package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.PedidoModel;

public interface IPedidoServicePort {

    PedidoModel save(PedidoModel pedido);

    PedidoModel getById(Long id);

}
