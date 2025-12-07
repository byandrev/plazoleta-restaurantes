package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.PedidoModel;

public interface IPedidoPersistencePort {

    PedidoModel save(PedidoModel pedidoEntity);

    PedidoModel getById(Long id);

}
