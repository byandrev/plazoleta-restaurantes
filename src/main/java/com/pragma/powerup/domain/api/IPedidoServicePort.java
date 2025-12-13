package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.*;

public interface IPedidoServicePort {

    PedidoModel save(UserModel client, PedidoModel pedido);

    PedidoModel update(UserModel employee, PedidoModel pedido);

    PedidoModel getById(Long id);

    PaginationResult<PedidoModel> getAll(Long userId, Long restaurantId, PedidoEstado estado, PaginationInfo pagination);

}
