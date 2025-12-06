package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.ResourceNotFound;
import com.pragma.powerup.domain.exception.UnauthorizedUserException;
import com.pragma.powerup.domain.model.CategoriaModel;
import com.pragma.powerup.domain.model.PlatoModel;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.ICategoriaPersistencePort;
import com.pragma.powerup.domain.spi.IPlatoPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatoUseCaseTest {

    @Mock
    private IPlatoPersistencePort platoPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private ICategoriaPersistencePort categoriaPersistencePort;

    @InjectMocks
    private PlatoUseCase platoUseCase;

    private final Long ownerId = 1L;
    private final Long notOwnerId = 2L;
    private final Long restaurantId = 10L;
    private final Long categoryId = 5L;
    private final Long platoId = 50L;

    private PlatoModel buildValidPlato() {
        return PlatoModel.builder()
                .nombre("Hamburguesa Clásica")
                .descripcion("Carne, queso y tomate")
                .precio(15000)
                .urlImagen("http://img.com/hamb.jpg")
                .activo(true)
                .idRestaurante(restaurantId)
                .restaurant(buildOwnerRestaurant())
                .categoria(CategoriaModel.builder().id(categoryId).build())
                .build();
    }

    private RestaurantModel buildOwnerRestaurant() {
        return RestaurantModel
                .builder()
                .id(restaurantId)
                .nombre("Test")
                .idPropietario(ownerId)
                .build();
    }

    @Test
    void savePlato_RestaurantNotFound_throwsException() {
        PlatoModel plato = buildValidPlato();
        Long nonExistentRestaurantId = 99L;
        plato.setIdRestaurante(nonExistentRestaurantId);

        doThrow(ResourceNotFound.class).when(restaurantPersistencePort).getById(nonExistentRestaurantId);

        assertThrows(ResourceNotFound.class, () -> platoUseCase.save(ownerId, plato));
        verify(platoPersistencePort, never()).save(any(PlatoModel.class));
    }

    @Test
    void savePlato_UserNotRestaurantOwner_throwsException() {
        PlatoModel plato = buildValidPlato();
        RestaurantModel nonOwnedRestaurant = buildOwnerRestaurant();
        nonOwnedRestaurant.setIdPropietario(notOwnerId);

        doThrow(UnauthorizedUserException.class).when(restaurantPersistencePort).getById(plato.getIdRestaurante());

        assertThrows(UnauthorizedUserException.class, () -> platoUseCase.save(ownerId, plato));
        verify(platoPersistencePort, never()).save(any(PlatoModel.class));
    }

    @Test
    void updatePlato_PlatoNotFound_throwsException() {
        PlatoModel platoToUpdate = buildValidPlato();
        Long nonExistentPlatoId = 99L;

        doThrow(ResourceNotFound.class).when(platoPersistencePort).getById(nonExistentPlatoId);

        assertThrows(ResourceNotFound.class, () -> platoUseCase.update(ownerId, nonExistentPlatoId, platoToUpdate));

        verify(platoPersistencePort, never()).save(any(PlatoModel.class));
    }

    @Test
    void updatePlato_UserNotRestaurantOwner_throwsException() {
        PlatoModel existingPlato = buildValidPlato();
        RestaurantModel restaurant = buildOwnerRestaurant();
        restaurant.setIdPropietario(notOwnerId);

        when(platoPersistencePort.getById(platoId)).thenReturn(existingPlato);
        when(restaurantPersistencePort.getById(restaurant.getId())).thenReturn(restaurant);

        assertThrows(UnauthorizedUserException.class, () -> platoUseCase.update(ownerId, platoId, existingPlato));
        verify(platoPersistencePort, never()).save(any(PlatoModel.class));
    }

    @Test
    void updatePlato_PriceAndDescriptionChange_success() {
        PlatoModel existingPlato = buildValidPlato();
        RestaurantModel restaurant = buildOwnerRestaurant();
        int newPrice = 25000;
        String newDescription = "Carne doble, queso y tomate";

        PlatoModel platoUpdate = PlatoModel.builder()
                .descripcion(newDescription)
                .precio(newPrice)
                .build();

        when(platoPersistencePort.getById(platoId)).thenReturn(existingPlato);
        when(restaurantPersistencePort.getById(restaurant.getId())).thenReturn(restaurant);

        when(platoPersistencePort.save(any(PlatoModel.class))).thenAnswer(invocation -> {
            PlatoModel savedPlato = invocation.getArgument(0);
            assertEquals(newDescription, savedPlato.getDescripcion());
            assertEquals(newPrice, savedPlato.getPrecio());
            assertEquals(existingPlato.getNombre(), savedPlato.getNombre());
            return savedPlato;
        });

        PlatoModel actualPlato = platoUseCase.update(ownerId, platoId, platoUpdate);

        assertNotNull(actualPlato);
        assertEquals(newDescription, actualPlato.getDescripcion());
        assertEquals(newPrice, actualPlato.getPrecio());
        verify(platoPersistencePort).save(any(PlatoModel.class));
    }

    @Test
    void updatePlato_ActivoStatus_success() {
        PlatoModel existingPlato = buildValidPlato();
        RestaurantModel restaurant = buildOwnerRestaurant();
        Boolean newActivo = false;

        PlatoModel platoUpdate = PlatoModel.builder()
                .activo(newActivo)
                .build();

        when(platoPersistencePort.getById(platoId)).thenReturn(existingPlato);
        when(restaurantPersistencePort.getById(restaurant.getId())).thenReturn(restaurant);

        when(platoPersistencePort.save(any(PlatoModel.class))).thenAnswer(invocation -> {
            PlatoModel savedPlato = invocation.getArgument(0);
            assertEquals(newActivo, savedPlato.getActivo());
            assertEquals(existingPlato.getNombre(), savedPlato.getNombre());
            return savedPlato;
        });

        PlatoModel actualPlato = platoUseCase.update(ownerId, platoId, platoUpdate);

        assertNotNull(actualPlato);
        assertEquals(newActivo, actualPlato.getActivo());
        verify(platoPersistencePort).save(any(PlatoModel.class));
    }

}
