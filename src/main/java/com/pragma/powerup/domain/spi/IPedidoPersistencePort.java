package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.PedidoEstado;
import com.pragma.powerup.domain.model.PedidoModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IPedidoPersistencePort {

    PedidoModel save(PedidoModel pedidoEntity);

    PedidoModel getById(Long id);

    Boolean existsByClienteIdAndEstadoIn(Long clienteId);

    Page<PedidoModel> getAll(PageRequest pageRequest);

    Page<PedidoModel> getAllByEstado(PedidoEstado estado, PageRequest pageRequest);

}
