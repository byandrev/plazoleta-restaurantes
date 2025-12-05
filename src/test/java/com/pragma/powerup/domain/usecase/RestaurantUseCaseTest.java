package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.UnauthorizedUserException;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.model.RolModel;
import com.pragma.powerup.domain.model.RolType;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserExternalServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    void getAllRestaurants_shouldReturnAllRestaurants() {
        RestaurantModel restaurant1 = buildRestaurantModel(1L, "Restaurant A", 100L);
        RestaurantModel restaurant2 = buildRestaurantModel(2L, "Restaurant B", 101L);

        when(restaurantPersistencePort.getAll()).thenReturn(List.of(restaurant1, restaurant2));

        List<RestaurantModel> result = restaurantUseCase.getAll();

        assertNotNull(result, "La lista no debe ser null.");
        assertEquals(2, result.size(), "Debe retornar 2 restaurantes.");
        assertEquals("Restaurant A", result.get(0).getNombre(), "El nombre del primer restaurante debe coincidir.");

        verify(restaurantPersistencePort).getAll();
    }

    @Test
    void getAllRestaurants_shouldReturnEmptyList_whenNoRestaurantsExist() {
        when(restaurantPersistencePort.getAll()).thenReturn(Collections.emptyList());
        List<RestaurantModel> result = restaurantUseCase.getAll();

        assertNotNull(result, "La lista no debe ser null (debe ser vacía).");
        assertTrue(result.isEmpty(), "La lista debe estar vacía.");

        verify(restaurantPersistencePort).getAll();
    }

    @Test
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
