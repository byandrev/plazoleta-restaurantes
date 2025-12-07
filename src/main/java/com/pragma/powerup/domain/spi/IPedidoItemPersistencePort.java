package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.PedidoItemModel;

public interface IPedidoItemPersistencePort {

    void save(PedidoItemModel pedidoItem);

}
