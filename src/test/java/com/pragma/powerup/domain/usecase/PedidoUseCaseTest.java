package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.PedidoEstado;
import com.pragma.powerup.domain.model.PedidoItemModel;
import com.pragma.powerup.domain.model.PedidoModel;
import com.pragma.powerup.domain.model.PlatoModel;
import com.pragma.powerup.domain.spi.IPedidoPersistencePort;
import com.pragma.powerup.domain.spi.IPlatoPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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

    @InjectMocks
    private PedidoUseCase pedidoUseCase;

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
                .idCliente(10L)
                .idRestaurante(1L)
                .items(itemsRequest)
                .build();

        pedidoSavedFirst = PedidoModel.builder()
                .id(5L)
                .idCliente(10L)
                .idRestaurante(1L)
                .estado(PedidoEstado.PENDIENTE)
                .fecha(LocalDate.now())
                .items(new HashSet<>())
                .build();
    }

    @Test
    @DisplayName("Debe crear un pedido exitosamente si el cliente no tiene pedidos en proceso")
    void save_shouldCreatePedidoSuccessfully_whenClientHasNoPendingOrders() {
        when(pedidoPersistencePort.existsByClienteIdAndEstadoIn(pedidoRequest.getIdCliente())).thenReturn(false);
        when(pedidoPersistencePort.save(pedidoRequest)).thenReturn(pedidoRequest);
        when(platoPersistencePort.getById(pedidoRequest.getItems().stream().findFirst().get().getPlatoId())).thenReturn(platoModel);
        when(pedidoPersistencePort.save(pedidoRequest)).thenReturn(pedidoRequest);

        PedidoModel result = pedidoUseCase.save(pedidoRequest);

        verify(pedidoPersistencePort).existsByClienteIdAndEstadoIn(pedidoRequest.getIdCliente());
        verify(platoPersistencePort).getById(1L);
        verify(pedidoPersistencePort, times(2)).save(any(PedidoModel.class));

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
    }

    @Test
    @DisplayName("Debe lanzar una excepción si el plato asociado a un ítem no existe")
    void save_shouldThrowException_whenPlatoDoesNotExist() {
        when(pedidoPersistencePort.existsByClienteIdAndEstadoIn(pedidoRequest.getIdCliente())).thenReturn(false);
        when(pedidoPersistencePort.save(any(PedidoModel.class))).thenReturn(pedidoSavedFirst);
        when(platoPersistencePort.getById(anyLong())).thenThrow(new RuntimeException("Plato not found"));

        assertThrows(RuntimeException.class, () ->
                pedidoUseCase.save(pedidoRequest)
        );

        verify(pedidoPersistencePort).save(any(PedidoModel.class));
        verify(platoPersistencePort).getById(1L);
    }


}
