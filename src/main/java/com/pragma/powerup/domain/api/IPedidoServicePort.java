package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.PedidoEstado;
import com.pragma.powerup.domain.model.PedidoModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IPedidoServicePort {

    PedidoModel save(PedidoModel pedido);

    PedidoModel getById(Long id);

    Page<PedidoModel> getAll(Long userId, Long restaurantId, PedidoEstado estado, PageRequest pageRequest);

}
