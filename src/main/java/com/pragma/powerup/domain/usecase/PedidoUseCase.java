package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IPedidoServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.*;
import com.pragma.powerup.domain.spi.IPedidoPersistencePort;
import com.pragma.powerup.domain.spi.IPlatoPersistencePort;
import com.pragma.powerup.domain.spi.ITraceabilityExternalServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoUseCase implements IPedidoServicePort {

    private final IPedidoPersistencePort pedidoPersistencePort;

    private final IPlatoPersistencePort platoPersistencePort;

    private final ITraceabilityExternalServicePort traceabilityService;

    private void checkPlatos(PedidoModel pedido) {
        Set<Long> platosIds = pedido.getItems().stream().map(PedidoItemModel::getPlatoId).collect(Collectors.toSet());
        Set<Long> platosNotFound = platoPersistencePort.findNonExistentPlatoIds(platosIds);

        if (!platosNotFound.isEmpty()) {
            String missingIds = platosNotFound.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            throw new DomainException("Los siguientes IDs de plato no fueron encontrados: " + missingIds);
        }
    }

    private PedidoModel saveAllItems(PedidoModel pedido, Set<PedidoItemModel> items) {
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

        return pedidoSaved;
    }

    @Override
    public PedidoModel save(PedidoModel pedido) {
        if (pedidoPersistencePort.existsByClienteIdAndEstadoIn(pedido.getIdCliente())) {
            throw new DomainException("No puedes crear un pedido porque tienes uno pendiente.");
        }

        checkPlatos(pedido);

        pedido.setEstado(PedidoEstado.PENDIENTE);
        pedido.setFecha(LocalDate.now());
        pedido.setRestaurante(RestaurantModel.builder().id(pedido.getIdRestaurante()).build());

        Set<PedidoItemModel> items = pedido.getItems();
        pedido.setItems(new HashSet<>());

        PedidoModel pedidoSaved = saveAllItems(pedido, items);

        traceabilityService.save(TraceabilityModel
                .builder()
                        .pedidoId(pedidoSaved.getId())
                        .clienteId(pedido.getCliente().getId())
                        .correoCliente(pedido.getCliente().getCorreo())
                        .estadoNuevo(pedidoSaved.getEstado())
                        .estadoAnterior(PedidoEstado.NINGUNO)
                        .correoEmpleado(null)
                        .empleadoId(0L)
                .build());

        return pedidoPersistencePort.save(pedidoSaved);
    }

    @Override
    public PedidoModel getById(Long id) {
        return pedidoPersistencePort.getById(id);
    }

}
