package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IUserExternalServicePort;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IUserExternalServicePort userExternalServicePort;

    @InjectMocks
    private RestaurantUseCase restaurantUseCase;


    @Test
    void getAllRestaurants() {
        RestaurantModel restaurant1 = new RestaurantModel();
        restaurant1.setId(1L);
        restaurant1.setNombre("Restaurant1");

        RestaurantModel restaurant2 = new RestaurantModel();
        restaurant2.setId(2L);
        restaurant2.setNombre("Restaurant2");

        when(restaurantPersistencePort.getAll()).thenReturn(List.of(restaurant1, restaurant2));

        List<RestaurantModel> result = restaurantUseCase.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Restaurant1", result.get(0).getNombre());
        assertEquals("Restaurant2", result.get(1).getNombre());
        verify(restaurantPersistencePort).getAll();
    }

    @Test
    void getRestaurantById() {
        RestaurantModel restaurant1 = new RestaurantModel();
        restaurant1.setId(1L);
        restaurant1.setNombre("Restaurant1");

        when(restaurantPersistencePort.getById(1L)).thenReturn(restaurant1);

        RestaurantModel result = restaurantUseCase.getById(1L);

        assertNotNull(result);
        assertEquals("Restaurant1", result.getNombre());
        verify(restaurantPersistencePort).getById(1L);
    }

}
