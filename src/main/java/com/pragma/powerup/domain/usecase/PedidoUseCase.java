package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IPedidoServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.*;
import com.pragma.powerup.domain.spi.*;
import com.pragma.powerup.domain.utils.ConvertDate;
import com.pragma.powerup.domain.utils.PinGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoUseCase implements IPedidoServicePort {

    private final IPedidoPersistencePort pedidoPersistencePort;
    private final IPlatoPersistencePort platoPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistence;
    private final IUserExternalServicePort userExternalService;
    private final ITraceabilityExternalServicePort traceabilityService;
    private final IEmployeePersistencePort employeePersistence;
    private final IMessageExternalServicePort messageExternalService;

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
                .restaurantId(pedido.getIdRestaurante())
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
        boolean hasPendingOrder = pedidoPersistencePort.existsByClienteIdAndEstadoIn(client.getId());
        pedido.canBeCreated(hasPendingOrder);

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

        return pedidoPersistencePort.save(pedidoSaved);
    }

    @Override
    public PedidoModel cancel(UserModel client, PedidoModel pedidoUpdate) {
        PedidoModel currentPedido = pedidoPersistencePort.getById(pedidoUpdate.getId());
        PedidoEstado backStatus = currentPedido.getEstado();

        if (!Objects.equals(currentPedido.getIdCliente(), client.getId())) {
            throw new DomainException("No tienes permiso para cancelar el pedido: " + pedidoUpdate.getId());
        }

        currentPedido.cancel();

        PedidoModel pedidoUpdated = pedidoPersistencePort.save(currentPedido);
        UserModel chef = userExternalService.getUserById(currentPedido.getIdChef());
        pedidoUpdated.setCliente(client);
        pedidoUpdated.setChef(chef);

        updateTraceability(pedidoUpdated, backStatus);

        return pedidoUpdated;
    }

    @Override
    public PedidoModel update(UserModel employee, PedidoModel pedidoUpdate) {
        PedidoModel currentPedido = pedidoPersistencePort.getById(pedidoUpdate.getId());
        RestaurantModel restaurant = currentPedido.getRestaurante();

        if (!employeePersistence.existsById(employee.getId(), restaurant.getId())) {
            throw new DomainException("No eres empleado del restaurante");
        }

        if (currentPedido.getEstado() == pedidoUpdate.getEstado()) {
            throw new DomainException("El nuevo estado es el mismo que el actual");
        }

        PedidoEstado backStatus = currentPedido.getEstado();
        Long employeeId = employee.getId();

        if (pedidoUpdate.getEstado() == PedidoEstado.EN_PREPARACION) {
            currentPedido.prepare(employeeId);
        } else if (pedidoUpdate.getEstado() == PedidoEstado.LISTO) {
            currentPedido.ready(employeeId);
        } else if (pedidoUpdate.getEstado() == PedidoEstado.ENTREGADO) {
            currentPedido.deliver(pedidoUpdate.getPin());
        } else if (pedidoUpdate.getEstado() == PedidoEstado.CANCELADO) {
            currentPedido.cancel();
        } else {
            throw new DomainException("Transición de estado no válida: " + pedidoUpdate.getEstado());
        }

        UserModel client = userExternalService.getUserById(currentPedido.getIdCliente());
        UserModel chef = userExternalService.getUserById(currentPedido.getIdChef());

        if (currentPedido.getEstado() == PedidoEstado.LISTO) {
            String pin = PinGenerator.generatePin(6);
            currentPedido.setPin(pin);

            messageExternalService.send(
                    client.getCelular(),
                    "El PIN de tu pedido #" + currentPedido.getId() + " es " + pin
            );
        }

        PedidoModel pedidoUpdated = pedidoPersistencePort.save(currentPedido);
        pedidoUpdated.setCliente(client);
        pedidoUpdated.setChef(chef);

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

    @Override
    public List<TraceabilityModel> getHistory(Long pedidoId) {
        return traceabilityService.getHistory(pedidoId);
    }

    @Override
    public PaginationResult<PedidoTimeModel> getTimePedidos(Long userId, Long restaurantId, PaginationInfo pagination) {
        RestaurantModel restaurant = restaurantPersistence.getById(restaurantId);

        if (!Objects.equals(restaurant.getIdPropietario(), userId)) {
            throw new DomainException("No eres propietario del restaurante");
        }

        return traceabilityService.getTimePedidos(restaurantId, pagination);
    }

}
