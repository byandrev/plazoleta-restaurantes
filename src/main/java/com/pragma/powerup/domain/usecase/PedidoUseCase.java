package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IPedidoServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.*;
import com.pragma.powerup.domain.spi.*;
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

    private final IUserExternalServicePort userExternalService;

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

    private void updateTraceability(PedidoModel pedido, PedidoEstado backStatus) {
        traceabilityService.save(TraceabilityModel
                .builder()
                .pedidoId(pedido.getId())
                .clienteId(pedido.getCliente().getId())
                .correoCliente(pedido.getCliente().getCorreo())
                .estadoNuevo(pedido.getEstado())
                .estadoAnterior(backStatus)
                .correoEmpleado(pedido.getChef().getCorreo())
                .empleadoId(pedido.getChef().getId())
                .build());
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
        pedidoSaved.setCliente(client);
        pedidoSaved.setChef(UserModel.builder().id(0L).correo(null).build());

        updateTraceability(pedidoSaved, PedidoEstado.PENDIENTE);

        return pedidoSaved;
    }

    @Override
    public PedidoModel update(UserModel employee, PedidoModel pedido) {
        if (!employee.getId().equals(pedido.getIdChef())) {
            throw new DomainException("No puedes asignar a otro empleado a un pedido.");
        }

        PedidoModel pedidoSaved = pedidoPersistencePort.getById(pedido.getId());
        RestaurantModel restaurante = pedidoSaved.getRestaurante();

        if (!employeePersistence.existsById(pedido.getIdChef(), restaurante.getId())) {
            throw new DomainException("No eres empleado del restaurante");
        }

        if (pedidoSaved.getEstado() == pedido.getEstado()) {
            throw new DomainException("Estas enviando un estado nuevo el cual es el mismo al anterior");
        }

        PedidoEstado backStatus = pedidoSaved.getEstado();
        pedidoSaved.setEstado(pedido.getEstado());
        pedidoSaved.setIdChef(pedido.getIdChef());

        PedidoModel pedidoUpdated = pedidoPersistencePort.save(pedidoSaved);
        pedidoUpdated.setCliente(userExternalService.getUserById(pedidoSaved.getIdCliente()));
        pedidoUpdated.setChef(userExternalService.getUserById(pedidoSaved.getIdChef()));

        updateTraceability(pedidoUpdated, backStatus);

        return pedidoUpdated;
    }

    @Override
    public PedidoModel getById(Long id) {
        return pedidoPersistencePort.getById(id);
    }

    @Override
    public PaginationResult<PedidoModel> getAll(Long userId, Long restaurantId, PedidoEstado estado, PaginationInfo pagination) {
        if (!employeePersistence.existsById(userId, restaurantId)) {
            throw new DomainException("No eres empleado del restaurante");
        }

        if (estado != null) {
            return pedidoPersistencePort.getAllByEstado(restaurantId, estado, pagination);
        }

        return pedidoPersistencePort.getAll(restaurantId, pagination);
    }

}
