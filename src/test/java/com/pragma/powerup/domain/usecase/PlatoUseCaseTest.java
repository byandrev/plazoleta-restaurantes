package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.*;
import com.pragma.powerup.domain.spi.ICategoriaPersistencePort;
import com.pragma.powerup.domain.spi.IPlatoPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.infrastructure.exception.ResourceNotFound;
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
    private final String categoria = "Categoria";
    private final Long platoId = 50L;
    private static final PaginationInfo PAGE_REQUEST = new PaginationInfo(0, 10, "id", "ASC");
    private PaginationResult<PlatoModel> mockPage;
    private PaginationResult<PlatoModel> emptyMockPage;

    @BeforeEach
    void setUp() {
        List<PlatoModel> platosList = List.of(PlatoModel.builder().id(1L).build(), PlatoModel.builder().id(2L).build());

        mockPage = new PaginationResult<>(
                platosList,
                PAGE_REQUEST.getPage(),
                PAGE_REQUEST.getSize(),
                platosList.size()
        );

        emptyMockPage = new PaginationResult<>(List.of(), PAGE_REQUEST.getPage(), PAGE_REQUEST.getSize(), 0);
    }

    private PlatoModel buildValidPlato(String nombre) {
        return PlatoModel.builder()
                .nombre(nombre)
                .descripcion("Carne, queso y tomate")
                .precio(15000)
                .urlImagen("http://img.com/hamb.jpg")
                .activo(true)
                .idRestaurante(restaurantId)
                .restaurante(buildOwnerRestaurant())
                .categoria(CategoriaModel.builder().nombre(categoria).id(categoryId).build())
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
        PlatoModel plato = buildValidPlato("Burger");
        Long nonExistentRestaurantId = 99L;
        plato.setIdRestaurante(nonExistentRestaurantId);

        doThrow(ResourceNotFound.class).when(restaurantPersistencePort).getById(nonExistentRestaurantId);

        assertThrows(ResourceNotFound.class, () -> platoUseCase.save(ownerId, plato));
        verify(platoPersistencePort, never()).save(any(PlatoModel.class));
    }

    @Test
    void savePlato_UserNotRestaurantOwner_throwsException() {
        PlatoModel plato = buildValidPlato("Burger");
        RestaurantModel nonOwnedRestaurant = buildOwnerRestaurant();
        nonOwnedRestaurant.setIdPropietario(notOwnerId);

        doThrow(DomainException.class).when(restaurantPersistencePort).getById(plato.getIdRestaurante());

        assertThrows(DomainException.class, () -> platoUseCase.save(ownerId, plato));
        verify(platoPersistencePort, never()).save(any(PlatoModel.class));
    }

    @Test
    void updatePlato_PlatoNotFound_throwsException() {
        PlatoModel platoToUpdate = buildValidPlato("Burger");
        Long nonExistentPlatoId = 99L;

        doThrow(ResourceNotFound.class).when(platoPersistencePort).getById(nonExistentPlatoId);

        assertThrows(ResourceNotFound.class, () -> platoUseCase.update(ownerId, nonExistentPlatoId, platoToUpdate));

        verify(platoPersistencePort, never()).save(any(PlatoModel.class));
    }

    @Test
    void updatePlato_UserNotRestaurantOwner_throwsException() {
        PlatoModel existingPlato = buildValidPlato("Burger");
        RestaurantModel restaurant = buildOwnerRestaurant();
        restaurant.setIdPropietario(notOwnerId);

        when(platoPersistencePort.getById(platoId)).thenReturn(existingPlato);
        when(restaurantPersistencePort.getById(restaurant.getId())).thenReturn(restaurant);

        assertThrows(DomainException.class, () -> platoUseCase.update(ownerId, platoId, existingPlato));
        verify(platoPersistencePort, never()).save(any(PlatoModel.class));
    }

    @Test
    void updatePlato_PriceAndDescriptionChange_success() {
        PlatoModel existingPlato = buildValidPlato("Burger");
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
        PlatoModel existingPlato = buildValidPlato("Burger");
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

    @Test
    @DisplayName("getAll debe retornar la página de platos del restaurante ordenada por nombre")
    void getAllPlatos_Paged_shouldReturnPagedList() {
        when(platoPersistencePort.getAll(restaurantId, PAGE_REQUEST)).thenReturn(mockPage);

        PaginationResult<PlatoModel> result = platoUseCase.getAll(null, restaurantId, PAGE_REQUEST);

        assertNotNull(result, "La página no debe ser null.");
        assertEquals(2, result.getContent().size(), "Debe retornar el tamaño de la página.");
        assertEquals(10, result.getTotalElements(), "El total de elementos debe coincidir con el mock.");

        verify(platoPersistencePort).getAll(restaurantId, PAGE_REQUEST);
    }

    @Test
    @DisplayName("getAll por categoria debe retornar la página de platos del restaurante ordenada por nombre")
    void getAllPlatosWithCategoria_Paged_shouldReturnPagedList() {
        when(platoPersistencePort.getAllByCategoria(categoria.toUpperCase(), restaurantId, PAGE_REQUEST)).thenReturn(mockPage);

        PaginationResult<PlatoModel> result = platoUseCase.getAll(categoria, restaurantId, PAGE_REQUEST);

        assertNotNull(result, "La página no debe ser null.");
        assertEquals(2, result.getContent().size(), "Debe retornar el tamaño de la página.");
        assertEquals(10, result.getTotalElements(), "El total de elementos debe coincidir con el mock.");

        verify(platoPersistencePort).getAllByCategoria(categoria.toUpperCase(), restaurantId, PAGE_REQUEST);
    }

    @Test
    @DisplayName("getAll por categoria debe retornar página vacía si la página no tiene resultados")
    void getAllPlatosWithCategoria_Paged_shouldReturnEmptyList_whenNoPlatosOnPage() {
        when(platoPersistencePort.getAllByCategoria(categoria.toUpperCase(), restaurantId, PAGE_REQUEST)).thenReturn(emptyMockPage);

        PaginationResult<PlatoModel> result = platoUseCase.getAll(categoria, restaurantId, PAGE_REQUEST);

        assertNotNull(result, "La página no debe ser null.");
        assertEquals(0, result.getContent().size(), "El contenido debe estar vacío.");
        assertEquals(10, result.getTotalElements(), "El total de elementos debe ser 10, aunque la página sea vacía.");

        verify(platoPersistencePort).getAllByCategoria(categoria.toUpperCase(), restaurantId, PAGE_REQUEST);
    }

}
