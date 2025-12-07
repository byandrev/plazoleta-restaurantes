package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.PedidoItemModel;
import com.pragma.powerup.domain.spi.IPedidoItemPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IPedidoItemMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IPedidoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PedidoItemJpaAdapter implements IPedidoItemPersistencePort {

    private final IPedidoItemRepository pedidoItemRepository;

    private final IPedidoItemMapper pedidoItemMapper;

    @Override
    public void save(PedidoItemModel pedidoItem) {
        pedidoItemRepository.save(pedidoItemMapper.toEntity(pedidoItem));
    }

}
