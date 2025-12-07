package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.exception.ResourceNotFound;
import com.pragma.powerup.domain.model.PedidoModel;
import com.pragma.powerup.domain.spi.IPedidoPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.PedidoEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IPedidoEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IPedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PedidoJpaAdapter implements IPedidoPersistencePort {

    private final IPedidoRepository pedidoRepository;

    private final IPedidoEntityMapper pedidoEntityMapper;

    @Override
    public PedidoModel save(PedidoModel pedidoModel) {
        PedidoEntity pedidoEntity = pedidoEntityMapper.toEntity(pedidoModel);
        return pedidoEntityMapper.toModel(pedidoRepository.save(pedidoEntity));
    }

    @Override
    public PedidoModel getById(Long id) {
        PedidoEntity pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("El pedido no existe"));
        return pedidoEntityMapper.toModel(pedido);
    }

}
