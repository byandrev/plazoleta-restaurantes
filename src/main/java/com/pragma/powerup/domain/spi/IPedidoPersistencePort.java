package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.PaginationInfo;
import com.pragma.powerup.domain.model.PaginationResult;
import com.pragma.powerup.domain.model.PedidoEstado;
import com.pragma.powerup.domain.model.PedidoModel;

public interface IPedidoPersistencePort {

    PedidoModel save(PedidoModel pedidoEntity);

    PedidoModel getById(Long id);

    Boolean existsByClienteIdAndEstadoIn(Long clienteId);

    PaginationResult<PedidoModel> getAll(Long restaurantId, PaginationInfo pagination);

    PaginationResult<PedidoModel> getAllByEstado(Long restaurantId, PedidoEstado estado, PaginationInfo pagination);

}
