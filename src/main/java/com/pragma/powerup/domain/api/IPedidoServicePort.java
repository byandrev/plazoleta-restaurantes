package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.PaginationResult;
import com.pragma.powerup.domain.model.PedidoEstado;
import com.pragma.powerup.domain.model.PedidoModel;
import com.pragma.powerup.domain.model.UserModel;

public interface IPedidoServicePort {

    PedidoModel save(UserModel client, PedidoModel pedido);

    PedidoModel getById(Long id);

    PaginationResult<PedidoModel> getAll(Long userId, Long restaurantId, PedidoEstado estado, int page, int size, String sortBy);

}
