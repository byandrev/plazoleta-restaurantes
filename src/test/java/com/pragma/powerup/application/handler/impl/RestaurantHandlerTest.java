package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.mapper.IRestaurantRequestMapper;
import com.pragma.powerup.application.mapper.IRestaurantResponseMapper;
import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.model.RestaurantModel;
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
class RestaurantHandlerTest {

    @Mock
    private IRestaurantServicePort restaurantService;

    @Mock
    private IRestaurantRequestMapper restaurantRequestMapper;

    @Mock
    private IRestaurantResponseMapper restaurantResponseMapper;

    @InjectMocks
    private RestaurantHandler restaurantHandler;

    private RestaurantRequestDto restaurantRequestDto;
    private RestaurantModel restaurantModel;
    private RestaurantResponseDto restaurantResponseDto;

    private final Long RESTAURANT_ID = 1L;
    private final int PAGE = 0;
    private final int SIZE = 2;
    private final String RESTAURANT_NAME_A = "Restaurant A";

    @BeforeEach
    void setUp() {
        restaurantRequestDto = RestaurantRequestDto
                .builder()
                .nombre("Restaurante")
                .direccion("Av 123")
                .telefono("+573001234567")
                .urlLogo("https://logo.png")
                .nit("123456789")
                .build();

        restaurantModel = RestaurantModel.builder()
                .id(RESTAURANT_ID)
                .nombre(RESTAURANT_NAME_A)
                .build();

        restaurantResponseDto = RestaurantResponseDto.builder()
                .id(RESTAURANT_ID)
                .nombre(RESTAURANT_NAME_A)
                .build();
    }

    @Test
    @DisplayName("save debe mapear DTO a Model y llamar al servicio para guardar")
    void save_SuccessfulFlow_CallsMapperAndService() {
        when(restaurantRequestMapper.toRestaurant(restaurantRequestDto)).thenReturn(restaurantModel);

        restaurantHandler.save(restaurantRequestDto);

        verify(restaurantRequestMapper).toRestaurant(restaurantRequestDto);
        verify(restaurantService).save(restaurantModel);
        verifyNoMoreInteractions(restaurantService, restaurantRequestMapper, restaurantResponseMapper);
    }

    @Test
    @DisplayName("getById debe llamar al servicio y mapear el Model a Response DTO")
    void getById_SuccessfulFlow_ReturnsResponseDto() {
        when(restaurantService.getById(RESTAURANT_ID)).thenReturn(restaurantModel);
        when(restaurantResponseMapper.toResponse(restaurantModel)).thenReturn(restaurantResponseDto);

        RestaurantResponseDto result = restaurantHandler.getById(RESTAURANT_ID);

        verify(restaurantService).getById(RESTAURANT_ID);
        verify(restaurantResponseMapper).toResponse(restaurantModel);

        assertNotNull(result);
        assertEquals(RESTAURANT_ID, result.getId());
        assertEquals(RESTAURANT_NAME_A, result.getNombre());

        verifyNoMoreInteractions(restaurantService, restaurantRequestMapper, restaurantResponseMapper);
    }


    @Test
    @DisplayName("getAll debe llamar al servicio con paginación y mapear Page<Model> a Page<ResponseDto>")
    void getAll_SuccessfulFlow_ReturnsPagedResponseDto() {
        RestaurantModel modelB = RestaurantModel.builder().id(2L).nombre("Restaurant B").build();
        RestaurantResponseDto responseB = RestaurantResponseDto.builder().id(2L).nombre("Restaurant B").build();

        List<RestaurantModel> modelList = Arrays.asList(restaurantModel, modelB);
        Page<RestaurantModel> mockedPage = new PageImpl<>(modelList, PageRequest.of(PAGE, SIZE), 10);

        when(restaurantService.getAll(PAGE, SIZE)).thenReturn(mockedPage);

        when(restaurantResponseMapper.toResponse(restaurantModel)).thenReturn(restaurantResponseDto);
        when(restaurantResponseMapper.toResponse(modelB)).thenReturn(responseB);

        Page<RestaurantResponseDto> resultPage = restaurantHandler.getAll(PAGE, SIZE);

        verify(restaurantService).getAll(PAGE, SIZE);

        assertNotNull(resultPage);
        assertEquals(SIZE, resultPage.getContent().size());
        assertEquals(10, resultPage.getTotalElements());
        assertEquals(5, resultPage.getTotalPages());

        verify(restaurantResponseMapper).toResponse(restaurantModel);
        verify(restaurantResponseMapper).toResponse(modelB);

        assertEquals(RESTAURANT_NAME_A, resultPage.getContent().get(0).getNombre());
        assertEquals("Restaurant B", resultPage.getContent().get(1).getNombre());

        verifyNoMoreInteractions(restaurantService, restaurantRequestMapper);
    }

    @Test
    @DisplayName("getAll debe manejar una página vacía correctamente")
    void getAll_EmptyPage_ReturnsEmptyPagedResponseDto() {
        Page<RestaurantModel> mockedEmptyPage = new PageImpl<>(List.of(), PageRequest.of(PAGE, SIZE), 0);

        when(restaurantService.getAll(PAGE, SIZE)).thenReturn(mockedEmptyPage);

        Page<RestaurantResponseDto> resultPage = restaurantHandler.getAll(PAGE, SIZE);

        verify(restaurantService, times(1)).getAll(PAGE, SIZE);
        verifyNoInteractions(restaurantRequestMapper, restaurantResponseMapper);

        assertTrue(resultPage.isEmpty());
        assertEquals(0, resultPage.getTotalElements());

        verifyNoMoreInteractions(restaurantService);
    }

}
