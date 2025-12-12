package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IPedidoServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.*;
import com.pragma.powerup.domain.spi.IEmployeePersistencePort;
import com.pragma.powerup.domain.spi.IPedidoPersistencePort;
import com.pragma.powerup.domain.spi.IPlatoPersistencePort;
import com.pragma.powerup.domain.spi.ITraceabilityExternalServicePort;
import com.pragma.powerup.domain.utils.ConvertDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoUseCase implements IPedidoServicePort {

    private final IPedidoPersistencePort pedidoPersistencePort;

    private final IPlatoPersistencePort platoPersistencePort;

    private final ITraceabilityExternalServicePort traceabilityService;

    private final IEmployeePersistencePort employeePersistence;

    private void checkPlatos(PedidoModel pedido) {
        Set<Long> platosIds = pedido.getItems().stream().map(PedidoItemModel::getPlatoId).collect(Collectors.toSet());
        Set<Long> platosNotFound = platoPersistencePort.findNonExistentPlatoIds(pedido.getIdRestaurante(), platosIds);

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
    public PedidoModel save(UserModel client, PedidoModel pedido) {
        if (pedidoPersistencePort.existsByClienteIdAndEstadoIn(client.getId())) {
            throw new DomainException("No puedes crear un pedido porque tienes uno pendiente.");
        }

        checkPlatos(pedido);

        pedido.setEstado(PedidoEstado.PENDIENTE);
        pedido.setFecha(ConvertDate.getCurrentDateTimeUTC());
        pedido.setRestaurante(RestaurantModel.builder().id(pedido.getIdRestaurante()).build());

        Set<PedidoItemModel> items = pedido.getItems();
        pedido.setItems(new HashSet<>());
        pedido.setIdCliente(client.getId());

        PedidoModel pedidoSaved = saveAllItems(pedido, items);

        traceabilityService.save(TraceabilityModel
                .builder()
                        .pedidoId(pedidoSaved.getId())
                        .clienteId(client.getId())
                        .correoCliente(client.getCorreo())
                        .estadoNuevo(pedidoSaved.getEstado())
                        .estadoAnterior(PedidoEstado.NINGUNO)
                        .correoEmpleado(null)
                        .empleadoId(0L)
                .build());

        return pedidoSaved;
    }

    @Override
    public PedidoModel getById(Long id) {
        return pedidoPersistencePort.getById(id);
    }

    @Override
    public PaginationResult<PedidoModel> getAll(Long userId, Long restaurantId, PedidoEstado estado, int page, int size, String sortBy) {
        if (!employeePersistence.existsById(userId, restaurantId)) {
            throw new DomainException("No eres empleado del restaurante");
        }

        if (estado != null) {
            return pedidoPersistencePort.getAllByEstado(restaurantId, estado, page, size, sortBy);
        }

        return pedidoPersistencePort.getAll(restaurantId, page, size, sortBy);
    }

}
