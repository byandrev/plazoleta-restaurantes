package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.*;
import com.pragma.powerup.domain.spi.IEmployeePersistencePort;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoUseCaseTest {

    @Mock
    private IPedidoPersistencePort pedidoPersistence;

    @Mock
    private IPlatoPersistencePort platoPersistence;

    @Mock
    private ITraceabilityExternalServicePort traceabilityService;

    @Mock
    private IEmployeePersistencePort  employeePersistence;

    @InjectMocks
    private PedidoUseCase pedidoUseCase;

    private static final Long RESTAURANT_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 10);
    private PedidoEstado ESTADO = PedidoEstado.PENDIENTE;
    private Page<PedidoModel> mockPage;

    private PedidoModel pedidoRequest;
    private PedidoModel pedidoSavedFirst;
    private Set<PedidoItemModel> itemsRequest;
    private PlatoModel platoModel;
    private UserModel client;

    @BeforeEach
    void setUp() {
        client = UserModel
                .builder()
                .id(10L)
                .correo("user@gmail.com")
                .build();

        platoModel = PlatoModel.builder().id(1L).nombre("Pizza").precio(10).build();

        itemsRequest = new HashSet<>(List.of(
                PedidoItemModel.builder().platoId(1L).cantidad(2).build()
        ));

        pedidoRequest = PedidoModel.builder()
                .cliente(client)
                .idRestaurante(RESTAURANT_ID)
                .items(itemsRequest)
                .build();

        pedidoSavedFirst = PedidoModel.builder()
                .id(5L)
                .idCliente(client.getId())
                .idRestaurante(RESTAURANT_ID)
                .estado(PedidoEstado.PENDIENTE)
                .fecha(ConvertDate.getCurrentDateTimeUTC())
                .items(new HashSet<>())
                .build();

        List<PedidoModel> pedidoList = Collections.singletonList(PedidoModel.builder().build());
        mockPage = new PageImpl<>(pedidoList, PAGE_REQUEST, 1);
    }

    @Test
    @DisplayName("Debe crear un pedido exitosamente si el cliente no tiene pedidos en proceso")
    void save_shouldCreatePedidoSuccessfully_whenClientHasNoPendingOrders() {
        when(pedidoPersistence.existsByClienteIdAndEstadoIn(pedidoRequest.getCliente().getId())).thenReturn(false);
        when(platoPersistence.findNonExistentPlatoIds(any(Long.class), any(Set.class))).thenReturn(new HashSet<>());
        when(pedidoPersistence.save(any(PedidoModel.class))).thenReturn(pedidoSavedFirst);
        when(platoPersistence.getById(pedidoRequest.getItems().stream().findFirst().get().getPlatoId())).thenReturn(platoModel);

        PedidoModel result = pedidoUseCase.save(client, pedidoRequest);

        verify(pedidoPersistence).existsByClienteIdAndEstadoIn(pedidoRequest.getCliente().getId());
        verify(platoPersistence).findNonExistentPlatoIds(any(Long.class), any(Set.class));
        verify(platoPersistence).getById(1L);
        verify(pedidoPersistence).save(any(PedidoModel.class));
        verify(traceabilityService).save(any(TraceabilityModel.class));

        assertNotNull(result);
        assertEquals(PedidoEstado.PENDIENTE, result.getEstado());
        assertEquals(1, result.getItems().size());
    }

    @Test
    @DisplayName("Debe lanzar DomainException si el cliente ya tiene un pedido en proceso")
    void save_shouldThrowDomainException_whenClientHasPendingOrders() {
        when(pedidoPersistence.existsByClienteIdAndEstadoIn(pedidoRequest.getCliente().getId())).thenReturn(true);

        DomainException exception = assertThrows(DomainException.class, () ->
                pedidoUseCase.save(client, pedidoRequest)
        );

        assertEquals("No puedes crear un pedido porque tienes uno pendiente.", exception.getMessage());

        verify(pedidoPersistence, never()).save(any(PedidoModel.class));
        verify(platoPersistence, never()).getById(anyLong());
        verify(traceabilityService, never()).save(any(TraceabilityModel.class));
    }

    @Test
    @DisplayName("Debe lanzar una excepción si el plato asociado a un ítem no existe")
    void save_shouldThrowException_whenPlatoDoesNotExist() {
        when(pedidoPersistence.existsByClienteIdAndEstadoIn(pedidoRequest.getCliente().getId())).thenReturn(false);
        when(platoPersistence.findNonExistentPlatoIds(any(Long.class), any(Set.class))).thenReturn(new HashSet<>());
        when(pedidoPersistence.save(any(PedidoModel.class))).thenReturn(pedidoSavedFirst);
        when(platoPersistence.getById(anyLong())).thenThrow(new RuntimeException("Plato not found"));

        assertThrows(RuntimeException.class, () ->
                pedidoUseCase.save(client, pedidoRequest)
        );

        verify(pedidoPersistence).save(any(PedidoModel.class));
        verify(platoPersistence).getById(1L);
        verify(traceabilityService, never()).save(any(TraceabilityModel.class));
    }

    @Test
    @DisplayName("Debe lanzar DomainException si el usuario no es empleado del restaurante")
    void getAll_UserIsNotEmployee_ThrowsDomainException() {
        when(employeePersistence.existsById(USER_ID, RESTAURANT_ID)).thenReturn(false);

        DomainException exception = assertThrows(DomainException.class, () ->
                pedidoUseCase.getAll(USER_ID, RESTAURANT_ID, null, PAGE_REQUEST)
        );

        assertEquals("No eres empleado del restaurante", exception.getMessage());

        verify(employeePersistence).existsById(USER_ID, RESTAURANT_ID);
    }

    @Test
    @DisplayName("Debe llamar a getAllByEstado cuando 'estado' es diferente a null")
    void getAll_EstadoIsNotNull_CallsGetAllByEstado() {
        PedidoEstado estado = PedidoEstado.PENDIENTE;
        when(employeePersistence.existsById(USER_ID, RESTAURANT_ID)).thenReturn(true);
        when(pedidoPersistence.getAllByEstado(RESTAURANT_ID, estado, PAGE_REQUEST)).thenReturn(mockPage);

        Page<PedidoModel> result = pedidoUseCase.getAll(USER_ID, RESTAURANT_ID, estado, PAGE_REQUEST);

        assertNotNull(result);
        assertEquals(mockPage, result);

        verify(employeePersistence).existsById(USER_ID, RESTAURANT_ID);
        verify(pedidoPersistence).getAllByEstado(RESTAURANT_ID, estado, PAGE_REQUEST);
        verify(pedidoPersistence, times(0)).getAll(RESTAURANT_ID, PAGE_REQUEST);
    }


}
