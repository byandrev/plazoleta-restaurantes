package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.EmployeeRequestDto;
import com.pragma.powerup.application.dto.request.PaginationRequestDto;
import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.PaginationResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.mapper.*;
import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.model.EmployeeModel;
import com.pragma.powerup.domain.model.PaginationInfo;
import com.pragma.powerup.domain.model.PaginationResult;
import com.pragma.powerup.domain.model.RestaurantModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantHandlerTest {

    @Mock
    private IRestaurantServicePort restaurantService;
    @Mock
    private IRestaurantRequestMapper restaurantRequestMapper;
    @Mock
    private IRestaurantResponseMapper restaurantResponseMapper;
    @Mock
    private IEmployeeRequestDtoMapper employeeRequestDtoMapper;
    @Mock
    private IPaginationResponseMapper paginationResponseMapper;
    @Mock
    private IPaginationRequestMapper paginationRequestMapper;

    @InjectMocks
    private RestaurantHandler restaurantHandler;

    @Test
    @DisplayName("save() debería mapear DTO a modelo y llamar al servicio para guardar")
    void save_ShouldConvertAndCallService() {
        RestaurantRequestDto requestDto = RestaurantRequestDto.builder().build();
        RestaurantModel restaurantModel = RestaurantModel.builder().build();

        when(restaurantRequestMapper.toRestaurant(requestDto)).thenReturn(restaurantModel);

        restaurantHandler.save(requestDto);

        verify(restaurantRequestMapper).toRestaurant(requestDto);
        verify(restaurantService).save(restaurantModel);
    }

    @Test
    @DisplayName("getById() debería obtener el modelo del servicio y mapearlo a DTO de respuesta")
    void getById_ShouldReturnResponseDto() {
        Long restaurantId = 1L;
        RestaurantModel restaurantModel = RestaurantModel.builder().id(restaurantId).build();
        RestaurantResponseDto responseDto = RestaurantResponseDto.builder().id(restaurantId).build();

        when(restaurantService.getById(restaurantId)).thenReturn(restaurantModel);
        when(restaurantResponseMapper.toResponse(restaurantModel)).thenReturn(responseDto);

        RestaurantResponseDto result = restaurantHandler.getById(restaurantId);

        assertNotNull(result);
        assertEquals(responseDto, result);

        verify(restaurantService).getById(restaurantId);
        verify(restaurantResponseMapper).toResponse(restaurantModel);
    }

    @Test
    @DisplayName("getAll() debería manejar la paginación, obtener la lista, mapear y devolver la respuesta paginada")
    void getAll_ShouldReturnPaginationResponse() {
        PaginationRequestDto paginationRequest = new PaginationRequestDto();

        PaginationInfo paginationModel = mock(PaginationInfo.class);
        PaginationResult<RestaurantModel> restaurantListMock = mock(PaginationResult.class);
        PaginationResult<RestaurantResponseDto> mappedResultMock = mock(PaginationResult.class);
        PaginationResponseDto<RestaurantResponseDto> finalResponse = PaginationResponseDto.<RestaurantResponseDto>builder().build();

        when(paginationRequestMapper.toModel(paginationRequest)).thenReturn(paginationModel);
        when(restaurantService.getAll(eq(paginationModel))).thenReturn(restaurantListMock);
        doReturn(mappedResultMock).when(restaurantListMock).map(any());
        when(paginationResponseMapper.toResponse(mappedResultMock)).thenReturn(finalResponse);

        PaginationResponseDto<RestaurantResponseDto> result = restaurantHandler.getAll(paginationRequest);

        assertNotNull(result);
        assertEquals(finalResponse, result);

        verify(paginationRequestMapper).toModel(paginationRequest);
        verify(restaurantService).getAll(eq(paginationModel));
        verify(paginationResponseMapper).toResponse(mappedResultMock);
        verify(restaurantListMock).map(any());
    }

    @Test
    @DisplayName("assignEmployee() debería mapear DTO de empleado y llamar al servicio para asignar")
    void assignEmployee_ShouldConvertAndCallService() {
        Long ownerId = 5L;
        EmployeeRequestDto employeeRequestDto = EmployeeRequestDto.builder().userId(ownerId).build();
        EmployeeModel employeeModel = EmployeeModel.builder().userId(ownerId).build();

        when(employeeRequestDtoMapper.toModel(employeeRequestDto)).thenReturn(employeeModel);

        restaurantHandler.assignEmployee(ownerId, employeeRequestDto);

        verify(employeeRequestDtoMapper).toModel(employeeRequestDto);
        verify(restaurantService).assignEmployee(ownerId, employeeModel);
    }

}
