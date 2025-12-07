package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IPedidoServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.*;
import com.pragma.powerup.domain.spi.IPedidoPersistencePort;
import com.pragma.powerup.domain.spi.IPlatoPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PedidoUseCase implements IPedidoServicePort {

    private final IPedidoPersistencePort pedidoPersistencePort;

    private final IPlatoPersistencePort platoPersistencePort;

    @Override
    public PedidoModel save(PedidoModel pedido) {
        if (pedidoPersistencePort.existsByClienteIdAndEstadoIn(pedido.getIdCliente())) {
            throw new DomainException("No puedes crear un pedido porque tienes uno pendiente.");
        }

        pedido.setEstado(PedidoEstado.PENDIENTE);
        pedido.setFecha(LocalDate.now());

        pedido.setRestaurante(RestaurantModel
                .builder()
                .id(pedido.getIdRestaurante())
                .build()
        );

        Set<PedidoItemModel> items = pedido.getItems();

        pedido.setItems(new HashSet<>());

        PedidoModel pedidoSaved = pedidoPersistencePort.save(pedido);

        for (PedidoItemModel item : items) {
            PlatoModel plato = platoPersistencePort.getById(item.getPlatoId());
            PedidoItemModel newItem = PedidoItemModel
                    .builder()
                    .pedidoId(pedidoSaved.getId())
                    .platoId(plato.getId())
                    .cantidad(item.getCantidad())
                    .build();

            pedidoSaved.addItem(newItem);
        }

        return pedidoPersistencePort.save(pedidoSaved);
    }

    @Override
    public PedidoModel getById(Long id) {
        return pedidoPersistencePort.getById(id);
    }

}
