package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.*;

import java.util.List;

public interface IPedidoServicePort {

    PedidoModel save(UserModel client, PedidoModel pedido);

    PedidoModel cancel(UserModel client, PedidoModel pedido);

    PedidoModel update(UserModel employee, PedidoModel pedido);

    PedidoModel getById(Long id);

    PaginationResult<PedidoModel> getAll(Long userId, Long restaurantId, PedidoEstado estado, PaginationInfo pagination);

    List<TraceabilityModel> getHistory(Long pedidoId);

    PaginationResult<PedidoTimeModel> getTimePedidos(Long restaurantId, PaginationInfo pagination);

}
