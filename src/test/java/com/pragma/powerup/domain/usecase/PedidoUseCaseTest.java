package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.*;
import com.pragma.powerup.domain.spi.*;
import com.pragma.powerup.domain.utils.ConvertDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

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
    @Mock
    private IUserExternalServicePort userExternalService;
    @Mock
    private IMessageExternalServicePort messageExternalService;
    @Mock
    private IRestaurantPersistencePort restaurantPersistence;

    @InjectMocks
    private PedidoUseCase pedidoUseCase;

    private static final Long PEDIDO_ID = 5L;
    private static final Long RESTAURANT_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long CHEF_ID = 2L;
    private static final Long OWNER_ID = 3L;
    private static final PaginationInfo PAGE_REQUEST = new PaginationInfo(0, 10, "id", "ASC");
    private PaginationResult<PedidoModel> mockPage;

    private PedidoModel pedidoRequest;
    private PedidoModel pedidoSavedFirst;
    private Set<PedidoItemModel> itemsRequest;
    private PlatoModel platoModel;
    private UserModel client;
    private UserModel chefModel;
    private PedidoModel existingPedido;
    private PedidoModel pedidoUpdatedRequest;
    private PedidoModel pedidoUpdatedSaved;
    private RestaurantModel restaurantModel;
    private PaginationInfo paginationInfo;

    @BeforeEach
    void setUp() {
        client = UserModel
                .builder()
                .id(10L)
                .correo("user@gmail.com")
                .build();

        restaurantModel = RestaurantModel.builder().id(RESTAURANT_ID).idPropietario(OWNER_ID).build();

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

        existingPedido = PedidoModel.builder()
                .id(PEDIDO_ID)
                .idCliente(client.getId())
                .cliente(client)
                .idRestaurante(RESTAURANT_ID)
                .restaurante(restaurantModel)
                .estado(PedidoEstado.PENDIENTE)
                .idChef(null)
                .build();

        pedidoUpdatedRequest = PedidoModel.builder()
                .id(PEDIDO_ID)
                .estado(PedidoEstado.EN_PREPARACION)
                .cliente(client)
                .idCliente(client.getId())
                .chef(chefModel)
                .idChef(CHEF_ID)
                .build();

        pedidoUpdatedSaved = PedidoModel.builder()
                .id(PEDIDO_ID)
                .idCliente(10L)
                .idRestaurante(RESTAURANT_ID)
                .restaurante(restaurantModel)
                .estado(PedidoEstado.EN_PREPARACION)
                .idChef(CHEF_ID)
                .build();

        List<PedidoModel> pedidoList = Collections.singletonList(PedidoModel.builder().build());
        mockPage = new PaginationResult<PedidoModel>(
                pedidoList,
                PAGE_REQUEST.getPage(),
                PAGE_REQUEST.getSize(),
                1
        );

        chefModel = UserModel.builder()
                .id(CHEF_ID)
                .correo("chef@gmail.com")
                .build();

        paginationInfo = PaginationInfo.builder()
                .page(0)
                .size(10)
                .build();
    }

    @Test
    @DisplayName("save() debe crear un pedido exitosamente si el cliente no tiene pedidos en proceso")
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
    @DisplayName("save() debe lanzar DomainException si el cliente ya tiene un pedido en proceso")
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
    @DisplayName("save() debe lanzar una excepción si el plato asociado a un ítem no existe")
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
    @DisplayName("getAll() debe lanzar DomainException si el usuario no es empleado del restaurante")
    void getAll_UserIsNotEmployee_ThrowsDomainException() {
        when(employeePersistence.existsById(USER_ID, RESTAURANT_ID)).thenReturn(false);

        DomainException exception = assertThrows(DomainException.class, () ->
                pedidoUseCase.getAll(USER_ID, RESTAURANT_ID, null, PAGE_REQUEST)
        );

        assertEquals("No eres empleado del restaurante", exception.getMessage());

        verify(employeePersistence).existsById(USER_ID, RESTAURANT_ID);
    }

    @Test
    @DisplayName("getAll() debe llamar a getAllByEstado cuando 'estado' es diferente a null")
    void getAll_EstadoIsNotNull_CallsGetAllByEstado() {
        PedidoEstado estado = PedidoEstado.PENDIENTE;
        when(employeePersistence.existsById(USER_ID, RESTAURANT_ID)).thenReturn(true);
        when(pedidoPersistence.getAllByEstado(RESTAURANT_ID, estado, PAGE_REQUEST)).thenReturn(mockPage);

        PaginationResult<PedidoModel> result = pedidoUseCase.getAll(USER_ID, RESTAURANT_ID, estado, PAGE_REQUEST);

        assertNotNull(result);
        assertEquals(mockPage, result);

        verify(employeePersistence).existsById(USER_ID, RESTAURANT_ID);
        verify(pedidoPersistence).getAllByEstado(RESTAURANT_ID, estado, PAGE_REQUEST);
        verify(pedidoPersistence, times(0)).getAll(RESTAURANT_ID, PAGE_REQUEST);
    }

    @Test
    @DisplayName("update() debe actualizar el pedido exitosamente")
    void update_ShouldUpdatePedidoSuccessfully() {
        when(pedidoPersistence.getById(PEDIDO_ID)).thenReturn(existingPedido);
        when(employeePersistence.existsById(CHEF_ID, RESTAURANT_ID)).thenReturn(true);
        when(pedidoPersistence.save(any(PedidoModel.class))).thenReturn(pedidoUpdatedSaved);
        when(userExternalService.getUserById(existingPedido.getIdCliente())).thenReturn(client);
        when(userExternalService.getUserById(CHEF_ID)).thenReturn(chefModel);

        PedidoModel result = pedidoUseCase.update(chefModel, pedidoUpdatedRequest);

        assertNotNull(result);
        assertEquals(PedidoEstado.EN_PREPARACION, result.getEstado());
        assertEquals(CHEF_ID, result.getIdChef());
        assertNotNull(result.getCliente());
        assertNotNull(result.getChef());

        verify(pedidoPersistence).getById(PEDIDO_ID);
        verify(employeePersistence).existsById(CHEF_ID, RESTAURANT_ID);
        verify(userExternalService).getUserById(existingPedido.getIdCliente());
        verify(userExternalService).getUserById(CHEF_ID);
        verify(traceabilityService).save(any(TraceabilityModel.class));
    }

    @Test
    @DisplayName("update() debe lanzar DomainException si el empleado intenta asignar a otro chef")
    void update_ShouldThrowDomainException_WhenEmployeeTriesToAssignOtherChef() {
        UserModel empleadoDiferente = UserModel.builder().id(USER_ID).correo("diferente@gmail.com").build();

        when(pedidoPersistence.getById(anyLong())).thenReturn(existingPedido);
        when(employeePersistence.existsById(anyLong(), anyLong())).thenReturn(false);

        DomainException exception = assertThrows(DomainException.class, () ->
                pedidoUseCase.update(empleadoDiferente, pedidoUpdatedRequest)
        );

        assertEquals("No eres empleado del restaurante", exception.getMessage());

        verify(pedidoPersistence).getById(anyLong());
        verify(employeePersistence).existsById(anyLong(), anyLong());
        verify(pedidoPersistence, never()).save(any(PedidoModel.class));
        verify(traceabilityService, never()).save(any(TraceabilityModel.class));
    }

    @Test
    @DisplayName("update() debe lanzar DomainException si el chef asignado no es empleado del restaurante")
    void update_ShouldThrowDomainException_WhenChefIsNotRestaurantEmployee() {
        when(pedidoPersistence.getById(PEDIDO_ID)).thenReturn(existingPedido);
        when(employeePersistence.existsById(CHEF_ID, RESTAURANT_ID)).thenReturn(false);

        DomainException exception = assertThrows(DomainException.class, () ->
                pedidoUseCase.update(chefModel, pedidoUpdatedRequest)
        );

        assertEquals("No eres empleado del restaurante", exception.getMessage());

        verify(pedidoPersistence).getById(PEDIDO_ID);
        verify(employeePersistence).existsById(CHEF_ID, RESTAURANT_ID);
        verify(pedidoPersistence, never()).save(any(PedidoModel.class));
        verify(traceabilityService, never()).save(any(TraceabilityModel.class));
    }

    @Test
    @DisplayName("update() debe lanzar DomainException si el nuevo estado es igual al estado anterior")
    void update_ShouldThrowDomainException_WhenNewStateIsSameAsOldState() {
        when(pedidoPersistence.getById(PEDIDO_ID)).thenReturn(existingPedido);
        PedidoModel requestSameState = PedidoModel.builder()
                .id(PEDIDO_ID)
                .estado(PedidoEstado.PENDIENTE)
                .idChef(CHEF_ID)
                .build();

        when(employeePersistence.existsById(CHEF_ID, RESTAURANT_ID)).thenReturn(true);

        DomainException exception = assertThrows(DomainException.class, () ->
                pedidoUseCase.update(chefModel, requestSameState)
        );

        assertEquals("El nuevo estado es el mismo que el actual", exception.getMessage());

        verify(pedidoPersistence).getById(PEDIDO_ID);
        verify(employeePersistence).existsById(CHEF_ID, RESTAURANT_ID);
        verify(pedidoPersistence, never()).save(any(PedidoModel.class));
        verify(traceabilityService, never()).save(any(TraceabilityModel.class));
    }

    @Test
    @DisplayName("update() debe cambiar exitosamente el estado de PENDIENTE a EN_PREPARACION")
    void update_ShouldChangeStateTo_EN_PREPARACION_Successfully() {
        PedidoModel request = PedidoModel.builder()
                .id(PEDIDO_ID)
                .estado(PedidoEstado.EN_PREPARACION)
                .cliente(client)
                .build();

        pedidoUpdatedRequest.setEstado(PedidoEstado.EN_PREPARACION);

        when(pedidoPersistence.getById(PEDIDO_ID)).thenReturn(existingPedido);
        when(employeePersistence.existsById(CHEF_ID, RESTAURANT_ID)).thenReturn(true);
        when(pedidoPersistence.save(any(PedidoModel.class))).thenReturn(pedidoUpdatedRequest);
        when(userExternalService.getUserById(pedidoUpdatedRequest.getIdCliente())).thenReturn(client);
        when(userExternalService.getUserById(pedidoUpdatedRequest.getIdChef())).thenReturn(chefModel);

        PedidoModel result = pedidoUseCase.update(chefModel, request);

        assertEquals(PedidoEstado.EN_PREPARACION, result.getEstado());
        assertEquals(CHEF_ID, result.getIdChef());

        verify(pedidoPersistence).getById(PEDIDO_ID);
        verify(pedidoPersistence).save(argThat(p -> p.getEstado() == PedidoEstado.EN_PREPARACION && p.getIdChef().equals(CHEF_ID)));
        verify(traceabilityService).save(any(TraceabilityModel.class));
        verify(messageExternalService, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("update() debe cambiar exitosamente el estado de EN_PREPARACION a LISTO y enviar PIN")
    void update_ShouldChangeStateTo_LISTO_SuccessfullyAndSendPin() {
        existingPedido.setEstado(PedidoEstado.EN_PREPARACION);

        PedidoModel request = PedidoModel.builder().id(PEDIDO_ID).estado(PedidoEstado.LISTO).build();

        pedidoUpdatedRequest.setEstado(PedidoEstado.LISTO);

        when(pedidoPersistence.getById(PEDIDO_ID)).thenReturn(existingPedido);
        when(employeePersistence.existsById(CHEF_ID, RESTAURANT_ID)).thenReturn(true);
        when(pedidoPersistence.save(any(PedidoModel.class))).thenReturn(pedidoUpdatedRequest);
        when(userExternalService.getUserById(pedidoUpdatedRequest.getIdCliente())).thenReturn(client);
        when(userExternalService.getUserById(pedidoUpdatedRequest.getIdChef())).thenReturn(chefModel);

        PedidoModel result = pedidoUseCase.update(chefModel, request);

        assertEquals(PedidoEstado.LISTO, result.getEstado());

        verify(pedidoPersistence).save(argThat(p -> p.getEstado() == PedidoEstado.LISTO));
        verify(traceabilityService).save(any(TraceabilityModel.class));
        verify(messageExternalService).send(
                eq(client.getCelular()),
                contains("El PIN de tu pedido #" + PEDIDO_ID + " es ")
        );
    }

    @Test
    @DisplayName("update() debe cambiar exitosamente el estado de LISTO a ENTREGADO")
    void update_ShouldChangeStateTo_ENTREGADO_Successfully() {
        existingPedido.setEstado(PedidoEstado.LISTO);
        existingPedido.setPin("123456");

        PedidoModel request = PedidoModel.builder()
                .id(PEDIDO_ID)
                .estado(PedidoEstado.ENTREGADO)
                .pin("123456")
                .build();

        pedidoUpdatedRequest.setEstado(PedidoEstado.ENTREGADO);

        when(pedidoPersistence.getById(PEDIDO_ID)).thenReturn(existingPedido);
        when(employeePersistence.existsById(CHEF_ID, RESTAURANT_ID)).thenReturn(true);
        when(pedidoPersistence.save(any(PedidoModel.class))).thenReturn(pedidoUpdatedRequest);
        when(userExternalService.getUserById(existingPedido.getIdCliente())).thenReturn(client);
        when(userExternalService.getUserById(existingPedido.getIdChef())).thenReturn(chefModel);

        PedidoModel result = pedidoUseCase.update(chefModel, request);

        assertEquals(PedidoEstado.ENTREGADO, result.getEstado());

        verify(pedidoPersistence).save(argThat(p -> p.getEstado() == PedidoEstado.ENTREGADO));
        verify(traceabilityService).save(any(TraceabilityModel.class));
        verify(messageExternalService, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("update() debe lanzar un DomainException cuando el PIN no es el mismo")
    void update_ShouldChangeStateTo_ENTREGADO_InvalidPIN_Successfully() {
        existingPedido.setEstado(PedidoEstado.LISTO);
        existingPedido.setPin("654321");

        PedidoModel request = PedidoModel.builder()
                .id(PEDIDO_ID)
                .estado(PedidoEstado.ENTREGADO)
                .pin("123456")
                .build();

        when(pedidoPersistence.getById(PEDIDO_ID)).thenReturn(existingPedido);
        when(employeePersistence.existsById(CHEF_ID, RESTAURANT_ID)).thenReturn(true);

        DomainException exception = assertThrows(DomainException.class, () ->
            pedidoUseCase.update(chefModel, request)
        );

        assertEquals("El PIN no es el correcto", exception.getMessage());

        verify(pedidoPersistence, never()).save(argThat(p -> p.getEstado() == PedidoEstado.ENTREGADO));
        verify(traceabilityService, never()).save(any(TraceabilityModel.class));
        verify(messageExternalService, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("update() debe cambiar exitosamente el estado de PENDIENTE a CANCELADO")
    void update_ShouldChangeStateTo_CANCELADO_Successfully() {
        PedidoModel request = PedidoModel.builder()
                .id(PEDIDO_ID)
                .estado(PedidoEstado.CANCELADO)
                .build();

        pedidoUpdatedRequest.setEstado(PedidoEstado.CANCELADO);

        when(pedidoPersistence.getById(PEDIDO_ID)).thenReturn(existingPedido);
        when(employeePersistence.existsById(CHEF_ID, RESTAURANT_ID)).thenReturn(true);
        when(userExternalService.getUserById(existingPedido.getIdCliente())).thenReturn(client);
        when(userExternalService.getUserById(existingPedido.getIdChef())).thenReturn(chefModel);
        when(pedidoPersistence.save(any(PedidoModel.class))).thenReturn(pedidoUpdatedRequest);

        PedidoModel result = pedidoUseCase.update(chefModel, request);

        assertEquals(PedidoEstado.CANCELADO, result.getEstado());

        verify(pedidoPersistence).save(argThat(p -> p.getEstado() == PedidoEstado.CANCELADO));
        verify(traceabilityService).save(any(TraceabilityModel.class));
    }

    @Test
    @DisplayName("cancel() debe cambiar exitosamente el estado de PENDIENTE a CANCELADO por el cliente")
    void cancel_ShouldChangeStateTo_CANCELADO_WhenInPENDIENTE() {
        PedidoModel request = PedidoModel.builder().id(PEDIDO_ID).build();
        pedidoUpdatedRequest.setEstado(PedidoEstado.CANCELADO);

        when(pedidoPersistence.getById(PEDIDO_ID)).thenReturn(existingPedido);
        when(pedidoPersistence.save(any(PedidoModel.class))).thenReturn(pedidoUpdatedRequest);
        when(userExternalService.getUserById(existingPedido.getIdChef())).thenReturn(chefModel);

        PedidoModel result = pedidoUseCase.cancel(client, request);

        assertEquals(PedidoEstado.CANCELADO, result.getEstado());

        verify(pedidoPersistence).getById(PEDIDO_ID);
        verify(pedidoPersistence).save(argThat(p -> p.getEstado() == PedidoEstado.CANCELADO && Objects.equals(p.getId(), PEDIDO_ID)));
        verify(traceabilityService).save(argThat(t -> t.getEstadoNuevo() == PedidoEstado.CANCELADO && t.getEstadoAnterior() == PedidoEstado.PENDIENTE));
    }

    @Test
    @DisplayName("getHistory() debe retornar el historial de trazabilidad del pedido")
    void getHistory_ShouldReturnTraceabilityHistory() {
        List<TraceabilityModel> traceabilityHistory = List.of(
            TraceabilityModel.builder().pedidoId(PEDIDO_ID).estadoAnterior(PedidoEstado.PENDIENTE).estadoNuevo(PedidoEstado.EN_PREPARACION).build(),
            TraceabilityModel.builder().pedidoId(PEDIDO_ID).estadoAnterior(PedidoEstado.EN_PREPARACION).estadoNuevo(PedidoEstado.LISTO).build()
        );

        when(traceabilityService.getHistory(PEDIDO_ID)).thenReturn(traceabilityHistory);

        List<TraceabilityModel> result = pedidoUseCase.getHistory(PEDIDO_ID);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(PedidoEstado.PENDIENTE, result.get(0).getEstadoAnterior());
        assertEquals(PedidoEstado.EN_PREPARACION, result.get(0).getEstadoNuevo());

        verify(traceabilityService).getHistory(PEDIDO_ID);
    }

    @Test
    @DisplayName("getHistory() debe retornar una lista vacía si no hay historial")
    void getHistory_ShouldReturnEmptyList_WhenNoHistory() {
        when(traceabilityService.getHistory(PEDIDO_ID)).thenReturn(Collections.emptyList());

        List<TraceabilityModel> result = pedidoUseCase.getHistory(PEDIDO_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(traceabilityService).getHistory(PEDIDO_ID);
    }

    @Test
    @DisplayName("getTimePedidos() debe retornar la información de tiempo si el usuario es el propietario del restaurante")
    void getTimePedidos_ShouldReturnTimeInfo_WhenUserIsOwner() {
        PaginationResult<PedidoTimeModel> expectedPaginationResult = new PaginationResult<PedidoTimeModel>(
                Collections.singletonList(
                        PedidoTimeModel.builder()
                                .pedido(1L)
                                .tiempo(5000F)
                                .build()
                ), 1, 1L, 1
        );

        when(restaurantPersistence.getById(RESTAURANT_ID)).thenReturn(restaurantModel);
        when(traceabilityService.getTimePedidos(RESTAURANT_ID, paginationInfo)).thenReturn(expectedPaginationResult);

        PaginationResult<PedidoTimeModel> result = pedidoUseCase.getTimePedidos(OWNER_ID, RESTAURANT_ID, paginationInfo);

        assertNotNull(result, "El resultado no debe ser nulo.");
        assertEquals(
                expectedPaginationResult.getTotalElements(),
                result.getTotalElements(),
                "El total de elementos debe coincidir."
        );

        verify(restaurantPersistence).getById(RESTAURANT_ID);
        verify(traceabilityService).getTimePedidos(RESTAURANT_ID, paginationInfo);
    }

    @Test
    @DisplayName("getTimePedidos() debe lanzar DomainException si el usuario no es el propietario del restaurante")
    void getTimePedidos_ShouldThrowDomainException_WhenUserIsNotOwner() {
        when(restaurantPersistence.getById(RESTAURANT_ID)).thenReturn(restaurantModel);

        DomainException exception = assertThrows(DomainException.class, () ->
                pedidoUseCase.getTimePedidos(USER_ID, RESTAURANT_ID, paginationInfo)
        );

        assertEquals("No eres propietario del restaurante", exception.getMessage());

        verify(restaurantPersistence).getById(RESTAURANT_ID);
        verify(traceabilityService, never()).getTimePedidos(anyLong(), any(PaginationInfo.class));
    }

}
