package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.UnauthorizedUserException;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.model.RolModel;
import com.pragma.powerup.domain.model.RolType;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserExternalServicePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
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

    private PageRequest buildPage() {
        int pageSize = 2;
        int page = 0;
        return PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "nombre"));
    }

    @Test
    @DisplayName("getAll(PageRequest) debe retornar la página de restaurantes ordenada por nombre")
    void getAllRestaurants_Paged_shouldReturnPagedList() {
        RestaurantModel restaurant1 = buildRestaurantModel(1L, "Restaurant A", 100L);
        RestaurantModel restaurant2 = buildRestaurantModel(2L, "Restaurant B", 101L);

        List<RestaurantModel> restaurantList = Arrays.asList(restaurant1, restaurant2);
        Page<RestaurantModel> mockedPage = new PageImpl<>(restaurantList, buildPage(), 20);

        PageRequest pageRequest = buildPage();

        when(restaurantPersistencePort.getAll(pageRequest)).thenReturn(mockedPage);

        Page<RestaurantModel> result = restaurantUseCase.getAll(pageRequest.getPageNumber(), pageRequest.getPageSize());

        assertNotNull(result, "La página no debe ser null.");
        assertEquals(2, result.getContent().size(), "Debe retornar el tamaño de la página (2).");
        assertEquals(20, result.getTotalElements(), "El total de elementos debe coincidir con el mock (20).");
        assertEquals("Restaurant A", result.getContent().get(0).getNombre(), "El primer restaurante debe ser 'Restaurant A'.");
        assertEquals("Restaurant B", result.getContent().get(1).getNombre(), "El segundo restaurante debe ser 'Restaurant B'.");

        verify(restaurantPersistencePort).getAll(pageRequest);
    }

    @Test
    @DisplayName("getAll(PageRequest) debe retornar página vacía si la página no tiene resultados")
    void getAllRestaurants_Paged_shouldReturnEmptyList_whenNoRestaurantsOnPage() {
        Page<RestaurantModel> mockedEmptyPage = new PageImpl<>(Collections.emptyList(), buildPage(), 10);

        PageRequest pageRequest = buildPage();

        when(restaurantPersistencePort.getAll(pageRequest)).thenReturn(mockedEmptyPage);

        Page<RestaurantModel> result = restaurantUseCase.getAll(pageRequest.getPageNumber(), pageRequest.getPageSize());

        assertNotNull(result, "La página no debe ser null.");
        assertTrue(result.isEmpty(), "La página debe estar vacía (isEmpty()).");
        assertEquals(0, result.getContent().size(), "El contenido debe estar vacío.");
        assertEquals(10, result.getTotalElements(), "El total de elementos debe ser 10, aunque la página sea vacía.");

        verify(restaurantPersistencePort).getAll(pageRequest);
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

        doThrow(UnauthorizedUserException.class).when(userExternalServicePort).getUserById(invalidOwnerId);

        assertThrows(UnauthorizedUserException.class, () ->
                restaurantUseCase.save(newRestaurant), "Debe lanzar UnauthorizedUserException.");

        verify(userExternalServicePort).getUserById(invalidOwnerId);
        verify(restaurantPersistencePort, never()).save(any(RestaurantModel.class));
    }

}
