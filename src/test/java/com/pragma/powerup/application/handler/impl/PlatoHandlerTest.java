package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.PlatoRequestDto;
import com.pragma.powerup.application.dto.request.PlatoUpdateDto;
import com.pragma.powerup.application.dto.response.PlatoResponseDto;
import com.pragma.powerup.application.mapper.IPlatoRequestMapper;
import com.pragma.powerup.application.mapper.IPlatoResponseMapper;
import com.pragma.powerup.domain.api.IPlatoServicePort;
import com.pragma.powerup.domain.model.CategoriaModel;
import com.pragma.powerup.domain.model.PlatoModel;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatoHandlerTest {

    @Mock
    private IPlatoServicePort platoService;

    @Mock
    private IPlatoRequestMapper platoRequestMapper;

    @Mock
    private IPlatoResponseMapper platoResponseMapper;

    @InjectMocks
    private PlatoHandler platoHandler;

    private PlatoRequestDto platoRequestDto;
    private PlatoUpdateDto platoUpdateDto;
    private PlatoModel platoModel;
    private PlatoResponseDto platoResponseDto;

    private final Long USER_ID = 1L;
    private final Long PLATO_ID = 1L;
    private final Long RESTAURANT_ID = 1L;
    private final int PAGE = 0;
    private final int SIZE = 10;
    private final String CATEGORIA = "Categoria";

    @BeforeEach
    void setUp() {
        platoRequestDto = PlatoRequestDto.builder()
                .nombre("Burger")
                .descripcion("Deliciosa hamburguesa")
                .precio(100)
                .urlImagen("https://logo.png")
                .categoria("Categoria")
                .idRestaurante(RESTAURANT_ID)
                .build();

        platoUpdateDto = PlatoUpdateDto.builder()
                .descripcion("Descripción actualizada")
                .precio(150)
                .activo(true)
                .build();

        platoModel = PlatoModel.builder()
                .id(PLATO_ID)
                .nombre("Burger")
                .descripcion("Deliciosa hamburguesa")
                .precio(100)
                .urlImagen("https://logo.png")
                .categoria(CategoriaModel.builder().id(1L).nombre("Categoria").build())
                .idRestaurante(RESTAURANT_ID)
                .activo(true)
                .build();

        platoResponseDto = PlatoResponseDto.builder()
                .id(PLATO_ID)
                .nombre("Burger")
                .descripcion("Deliciosa hamburguesa")
                .precio(100)
                .urlImagen("https://logo.png")
                .categoria(CategoriaModel.builder().id(1L).nombre("Categoria").build())
                .idRestaurante(RESTAURANT_ID)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("save debe mapear DTO a Model y llamar al servicio para guardar")
    void save_SuccessfulFlow_CallsMapperAndService() {
        when(platoRequestMapper.toModel(platoRequestDto)).thenReturn(platoModel);

        platoHandler.save(USER_ID, platoRequestDto);

        verify(platoRequestMapper).toModel(platoRequestDto);
        verify(platoService).save(USER_ID, platoModel);
        verifyNoMoreInteractions(platoService, platoRequestMapper, platoResponseMapper);
    }

    @Test
    @DisplayName("getById debe llamar al servicio y mapear el Model a Response DTO")
    void getById_SuccessfulFlow_ReturnsResponseDto() {
        when(platoService.getById(PLATO_ID)).thenReturn(platoModel);
        when(platoResponseMapper.toResponse(platoModel)).thenReturn(platoResponseDto);

        PlatoResponseDto result = platoHandler.getById(PLATO_ID);

        verify(platoService).getById(PLATO_ID);
        verify(platoResponseMapper).toResponse(platoModel);

        assertNotNull(result);
        assertEquals(PLATO_ID, result.getId());
        assertEquals("Burger", result.getNombre());
        assertEquals(100, result.getPrecio());
        assertEquals(RESTAURANT_ID, result.getIdRestaurante());

        verifyNoMoreInteractions(platoService, platoRequestMapper, platoResponseMapper);
    }

    @Test
    @DisplayName("getAll debe llamar al servicio con paginación y mapear Page<Model> a Page<ResponseDto>")
    void getAll_SuccessfulFlow_ReturnsPagedResponseDto() {
        PlatoModel platoModel2 = PlatoModel.builder()
                .id(2L)
                .nombre("Pizza")
                .descripcion("Pizza deliciosa")
                .precio(200)
                .urlImagen("https://pizza.png")
                .categoria(CategoriaModel.builder().id(1L).nombre("Categoria").build())
                .idRestaurante(RESTAURANT_ID)
                .activo(true)
                .build();

        PlatoResponseDto platoResponseDto2 = PlatoResponseDto.builder()
                .id(2L)
                .nombre("Pizza")
                .descripcion("Pizza deliciosa")
                .precio(200)
                .urlImagen("https://pizza.png")
                .categoria(CategoriaModel.builder().id(1L).nombre("Categoria").build())
                .idRestaurante(RESTAURANT_ID)
                .activo(true)
                .build();

        List<PlatoModel> modelList = Arrays.asList(platoModel, platoModel2);
        Page<PlatoModel> mockedPage = new PageImpl<>(modelList, PageRequest.of(PAGE, SIZE), 10);

        when(platoService.getAll(CATEGORIA, RESTAURANT_ID, PAGE, SIZE)).thenReturn(mockedPage);
        when(platoResponseMapper.toResponse(platoModel)).thenReturn(platoResponseDto);
        when(platoResponseMapper.toResponse(platoModel2)).thenReturn(platoResponseDto2);

        Page<PlatoResponseDto> resultPage = platoHandler.getAll(CATEGORIA, RESTAURANT_ID, PAGE, SIZE);

        verify(platoService).getAll(CATEGORIA, RESTAURANT_ID, PAGE, SIZE);

        assertNotNull(resultPage);
        assertEquals(2, resultPage.getContent().size());
        assertEquals(10, resultPage.getTotalElements());
        assertEquals(1, resultPage.getTotalPages());

        verify(platoResponseMapper).toResponse(platoModel);
        verify(platoResponseMapper).toResponse(platoModel2);

        assertEquals("Burger", resultPage.getContent().get(0).getNombre());
        assertEquals("Pizza", resultPage.getContent().get(1).getNombre());

        verifyNoMoreInteractions(platoService, platoRequestMapper);
    }

    @Test
    @DisplayName("getAll debe manejar una página vacía correctamente")
    void getAll_EmptyPage_ReturnsEmptyPagedResponseDto() {
        Page<PlatoModel> mockedEmptyPage = new PageImpl<>(List.of(), PageRequest.of(PAGE, SIZE), 0);

        when(platoService.getAll(CATEGORIA, RESTAURANT_ID, PAGE, SIZE)).thenReturn(mockedEmptyPage);

        Page<PlatoResponseDto> resultPage = platoHandler.getAll(CATEGORIA, RESTAURANT_ID, PAGE, SIZE);

        verify(platoService, times(1)).getAll(CATEGORIA, RESTAURANT_ID, PAGE, SIZE);
        verifyNoInteractions(platoRequestMapper);

        assertTrue(resultPage.isEmpty());
        assertEquals(0, resultPage.getTotalElements());

        verifyNoMoreInteractions(platoService);
    }

    @Test
    @DisplayName("getAll debe funcionar correctamente cuando categoria es null")
    void getAll_WithNullCategoria_ReturnsPagedResponseDto() {
        Page<PlatoModel> mockedPage = new PageImpl<>(List.of(platoModel), PageRequest.of(PAGE, SIZE), 1);

        when(platoService.getAll(null, RESTAURANT_ID, PAGE, SIZE)).thenReturn(mockedPage);
        when(platoResponseMapper.toResponse(platoModel)).thenReturn(platoResponseDto);

        Page<PlatoResponseDto> resultPage = platoHandler.getAll(null, RESTAURANT_ID, PAGE, SIZE);

        verify(platoService).getAll(null, RESTAURANT_ID, PAGE, SIZE);
        verify(platoResponseMapper).toResponse(platoModel);

        assertNotNull(resultPage);
        assertEquals(1, resultPage.getContent().size());
    }

    @Test
    @DisplayName("update debe mapear DTO a Model, llamar al servicio y mapear el resultado a ResponseDto")
    void update_SuccessfulFlow_ReturnsResponseDto() {
        PlatoModel updatedPlatoModel = PlatoModel.builder()
                .id(PLATO_ID)
                .nombre("Burger")
                .descripcion("Descripción actualizada")
                .precio(150)
                .urlImagen("https://logo.png")
                .categoria(CategoriaModel.builder().id(1L).nombre("Categoria").build())
                .idRestaurante(RESTAURANT_ID)
                .activo(true)
                .build();

        PlatoResponseDto updatedPlatoResponseDto = PlatoResponseDto.builder()
                .id(PLATO_ID)
                .nombre("Burger")
                .descripcion("Descripción actualizada")
                .precio(150)
                .urlImagen("https://logo.png")
                .categoria(CategoriaModel.builder().id(1L).nombre("Categoria").build())
                .idRestaurante(RESTAURANT_ID)
                .activo(true)
                .build();

        when(platoRequestMapper.toModel(platoUpdateDto)).thenReturn(updatedPlatoModel);
        when(platoService.update(USER_ID, PLATO_ID, updatedPlatoModel)).thenReturn(updatedPlatoModel);
        when(platoResponseMapper.toResponse(updatedPlatoModel)).thenReturn(updatedPlatoResponseDto);

        PlatoResponseDto result = platoHandler.update(USER_ID, PLATO_ID, platoUpdateDto);

        verify(platoRequestMapper).toModel(platoUpdateDto);
        verify(platoService).update(USER_ID, PLATO_ID, updatedPlatoModel);
        verify(platoResponseMapper).toResponse(updatedPlatoModel);

        assertNotNull(result);
        assertEquals(PLATO_ID, result.getId());
        assertEquals("Descripción actualizada", result.getDescripcion());
        assertEquals(150, result.getPrecio());
        assertTrue(result.isActivo());

        verifyNoMoreInteractions(platoService, platoRequestMapper, platoResponseMapper);
    }

}