package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.*;
import com.pragma.powerup.domain.spi.IPedidoPersistencePort;
import com.pragma.powerup.domain.spi.IPlatoPersistencePort;
import com.pragma.powerup.domain.spi.ITraceabilityExternalServicePort;
import com.pragma.powerup.domain.utils.ConvertDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoUseCaseTest {

    @Mock
    private IPedidoPersistencePort pedidoPersistencePort;

    @Mock
    private IPlatoPersistencePort platoPersistencePort;

    @Mock
    private ITraceabilityExternalServicePort traceabilityService;

    @InjectMocks
    private PedidoUseCase pedidoUseCase;

    private final Long RESTAURANT_ID = 1L;

    private PedidoModel pedidoRequest;
    private PedidoModel pedidoSavedFirst;
    private Set<PedidoItemModel> itemsRequest;
    private PlatoModel platoModel;

    @BeforeEach
    void setUp() {
        platoModel = PlatoModel.builder().id(1L).nombre("Pizza").precio(10).build();

        itemsRequest = new HashSet<>(List.of(
                PedidoItemModel.builder().platoId(1L).cantidad(2).build()
        ));

        pedidoRequest = PedidoModel.builder()
                .cliente(UserModel.builder().id(10L).correo("cliente@gmail.com").build())
                .idRestaurante(RESTAURANT_ID)
                .items(itemsRequest)
                .build();

        pedidoSavedFirst = PedidoModel.builder()
                .id(5L)
                .idCliente(10L)
                .idRestaurante(RESTAURANT_ID)
                .estado(PedidoEstado.PENDIENTE)
                .fecha(ConvertDate.getCurrentDateTimeUTC())
                .items(new HashSet<>())
                .build();
    }

    @Test
    @DisplayName("Debe crear un pedido exitosamente si el cliente no tiene pedidos en proceso")
    void save_shouldCreatePedidoSuccessfully_whenClientHasNoPendingOrders() {
        when(pedidoPersistencePort.existsByClienteIdAndEstadoIn(pedidoRequest.getIdCliente())).thenReturn(false);
        when(platoPersistencePort.findNonExistentPlatoIds(any(Long.class), any(Set.class))).thenReturn(new HashSet<>());
        when(pedidoPersistencePort.save(any(PedidoModel.class))).thenReturn(pedidoSavedFirst);
        when(platoPersistencePort.getById(pedidoRequest.getItems().stream().findFirst().get().getPlatoId())).thenReturn(platoModel);

        PedidoModel result = pedidoUseCase.save(pedidoRequest);

        verify(pedidoPersistencePort).existsByClienteIdAndEstadoIn(pedidoRequest.getIdCliente());
        verify(platoPersistencePort).findNonExistentPlatoIds(any(Long.class), any(Set.class));
        verify(platoPersistencePort).getById(1L);
        verify(pedidoPersistencePort, times(2)).save(any(PedidoModel.class));
        verify(traceabilityService).save(any(TraceabilityModel.class));

        assertNotNull(result);
        assertEquals(PedidoEstado.PENDIENTE, result.getEstado());
        assertEquals(1, result.getItems().size());
    }

    @Test
    @DisplayName("Debe lanzar DomainException si el cliente ya tiene un pedido en proceso")
    void save_shouldThrowDomainException_whenClientHasPendingOrders() {
        when(pedidoPersistencePort.existsByClienteIdAndEstadoIn(pedidoRequest.getIdCliente())).thenReturn(true);

        DomainException exception = assertThrows(DomainException.class, () ->
                pedidoUseCase.save(pedidoRequest)
        );

        assertEquals("No puedes crear un pedido porque tienes uno pendiente.", exception.getMessage());

        verify(pedidoPersistencePort, never()).save(any(PedidoModel.class));
        verify(platoPersistencePort, never()).getById(anyLong());
        verify(traceabilityService, never()).save(any(TraceabilityModel.class));
    }

    @Test
    @DisplayName("Debe lanzar una excepción si el plato asociado a un ítem no existe")
    void save_shouldThrowException_whenPlatoDoesNotExist() {
        when(pedidoPersistencePort.existsByClienteIdAndEstadoIn(pedidoRequest.getIdCliente())).thenReturn(false);
        when(platoPersistencePort.findNonExistentPlatoIds(any(Long.class), any(Set.class))).thenReturn(new HashSet<>());
        when(pedidoPersistencePort.save(any(PedidoModel.class))).thenReturn(pedidoSavedFirst);
        when(platoPersistencePort.getById(anyLong())).thenThrow(new RuntimeException("Plato not found"));

        assertThrows(RuntimeException.class, () ->
                pedidoUseCase.save(pedidoRequest)
        );

        verify(pedidoPersistencePort).save(any(PedidoModel.class));
        verify(platoPersistencePort).getById(1L);
        verify(traceabilityService, never()).save(any(TraceabilityModel.class));
    }


}
