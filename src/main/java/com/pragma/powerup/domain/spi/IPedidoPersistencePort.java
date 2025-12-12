package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.PaginationResult;
import com.pragma.powerup.domain.model.PedidoEstado;
import com.pragma.powerup.domain.model.PedidoModel;

public interface IPedidoPersistencePort {

    PedidoModel save(PedidoModel pedidoEntity);

    PedidoModel getById(Long id);

    Boolean existsByClienteIdAndEstadoIn(Long clienteId);

    PaginationResult<PedidoModel> getAll(Long restaurantId, int page, int size, String sortBy);

    PaginationResult<PedidoModel> getAllByEstado(Long restaurantId, PedidoEstado estado, int page, int size, String sortBy);

}
