package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.model.PlatoModel;
import com.pragma.powerup.domain.spi.ICategoriaPersistencePort;
import com.pragma.powerup.domain.spi.IPlatoPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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


    @Test
    void update() {
        PlatoModel plato = new PlatoModel();

        plato.setId(1L);
        plato.setDescripcion("Updated");
        plato.setPrecio(1000);

        when(platoPersistencePort.update(1L, plato)).thenReturn(plato);

        PlatoModel actualPlato = platoUseCase.update(1L, plato);

        assertNotNull(actualPlato);
        assertEquals(plato.getDescripcion(), actualPlato.getDescripcion());

        verify(platoPersistencePort).update(1L, plato);
    }
}
