package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.*;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserExternalServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IUserExternalServicePort userExternalServicePort;

    @InjectMocks
    private RestaurantUseCase restaurantUseCase;

    private static final PaginationInfo PAGE_REQUEST = new PaginationInfo(0, 10, "id", "ASC");
    private PaginationResult<RestaurantModel> mockPage;
    private PaginationResult<RestaurantModel> emptyMockPage;

    @BeforeEach
    void setUp() {
        List<RestaurantModel> restaurantesList = List.of(RestaurantModel.builder().id(1L).build(), RestaurantModel.builder().id(2L).build());

        mockPage = new PaginationResult<>(
                restaurantesList,
                PAGE_REQUEST.getPage(),
                PAGE_REQUEST.getSize(),
                restaurantesList.size()
        );

        emptyMockPage = new PaginationResult<>(List.of(), PAGE_REQUEST.getPage(), PAGE_REQUEST.getSize(), 0);
    }

    private RestaurantModel buildRestaurantModel(Long id, String name, Long ownerId) {
        return RestaurantModel.builder()
                .id(id)
                .nombre(name)
                .direccion("Calle 123")
                .telefono("3001234567")
                .urlLogo("http://logo.png")
                .nit("800000001")
                .idPropietario(ownerId)
                .build();
    }

    private UserModel buildOwner() {
        return UserModel
                .builder()
                .id(1L)
                .nombre("Propietario")
                .rol(RolModel.builder().nombre(RolType.PROPIETARIO).build())
                .build();
    }

    @Test
    @DisplayName("getAll(PageRequest) debe retornar la página de restaurantes ordenada por nombre")
    void getAllRestaurants_Paged_shouldReturnPagedList() {
        when(restaurantPersistencePort.getAll(PAGE_REQUEST)).thenReturn(mockPage);

        PaginationResult<RestaurantModel> result = restaurantUseCase.getAll(PAGE_REQUEST);

        assertNotNull(result, "La página no debe ser null.");
        assertEquals(2, result.getContent().size(), "Debe retornar el tamaño de la página.");
        assertEquals(10, result.getTotalElements(), "El total de elementos debe coincidir con el mock.");

        verify(restaurantPersistencePort).getAll(PAGE_REQUEST);
    }

    @Test
    @DisplayName("getAll(PageRequest) debe retornar página vacía si la página no tiene resultados")
    void getAllRestaurants_Paged_shouldReturnEmptyList_whenNoRestaurantsOnPage() {
        when(restaurantPersistencePort.getAll(PAGE_REQUEST)).thenReturn(emptyMockPage);

        PaginationResult<RestaurantModel> result = restaurantUseCase.getAll(PAGE_REQUEST);

        assertNotNull(result, "La página no debe ser null.");
        assertEquals(0, result.getContent().size(), "El contenido debe estar vacío.");
        assertEquals(10, result.getTotalElements(), "El total de elementos debe ser 10, aunque la página sea vacía.");

        verify(restaurantPersistencePort).getAll(PAGE_REQUEST);
    }

    @Test
    @DisplayName("getById debe retornar un restaurante")
    void getRestaurantById_shouldReturnRestaurant_whenFound() {
        Long restaurantId = 1L;
        RestaurantModel expectedRestaurant = buildRestaurantModel(restaurantId, "Restaurant Found", 100L);

        when(restaurantPersistencePort.getById(restaurantId)).thenReturn(expectedRestaurant);

        assertDoesNotThrow(() -> {
            RestaurantModel result = restaurantUseCase.getById(restaurantId);

            assertNotNull(result, "El resultado no debe ser null.");
            assertEquals(restaurantId, result.getId(), "El ID debe coincidir.");
            assertEquals("Restaurant Found", result.getNombre());
        }, "No deberia lanzar una excepcion");

        verify(restaurantPersistencePort).getById(restaurantId);
    }

    @Test
    @DisplayName("save(restaurant) no debe generar ninguna excepcion")
    void saveRestaurant_success() {
        UserModel ownerUser = buildOwner();
        RestaurantModel newRestaurant = buildRestaurantModel(null, "New Restaurant", ownerUser.getId());

        when(userExternalServicePort.getUserById(ownerUser.getId())).thenReturn(ownerUser);
        when(restaurantPersistencePort.save(newRestaurant)).thenReturn(newRestaurant);
        assertDoesNotThrow(() -> restaurantUseCase.save(newRestaurant), "No debe lanzar excepción si el dueño es válido.");

        verify(userExternalServicePort).getUserById(ownerUser.getId());
        verify(restaurantPersistencePort).save(newRestaurant);
    }

    @Test
    @DisplayName("save(restaurante) debe lanzar una excepcion cuando usuario no tiene el rol propietario")
    void saveRestaurant_whenUserIsNotOwner_shouldThrowException() {
        Long invalidOwnerId = 200L;
        RestaurantModel newRestaurant = buildRestaurantModel(null, "Forbidden restaurant", invalidOwnerId);

        doThrow(DomainException.class).when(userExternalServicePort).getUserById(invalidOwnerId);

        assertThrows(DomainException.class, () ->
                restaurantUseCase.save(newRestaurant), "Debe lanzar DomainException.");

        verify(userExternalServicePort).getUserById(invalidOwnerId);
        verify(restaurantPersistencePort, never()).save(any(RestaurantModel.class));
    }

}
