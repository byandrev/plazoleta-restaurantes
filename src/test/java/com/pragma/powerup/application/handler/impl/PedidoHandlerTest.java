package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.PaginationRequestDto;
import com.pragma.powerup.application.dto.request.PedidoRequestDto;
import com.pragma.powerup.application.dto.request.PedidoUpdateDto;
import com.pragma.powerup.application.dto.response.PaginationResponseDto;
import com.pragma.powerup.application.dto.response.PedidoResponseDto;
import com.pragma.powerup.application.mapper.*;
import com.pragma.powerup.domain.api.IPedidoServicePort;
import com.pragma.powerup.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
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
class PedidoHandlerTest {

    private static final Long PEDIDO_ID = 1L;
    private static final Long CLIENTE_ID = 10L;

    @Mock
    private IPedidoServicePort pedidoService;
    @Mock
    private IPedidoRequestMapper pedidoRequestMapper;
    @Mock
    private IPedidoUpdateMapper pedidoUpdateMapper;
    @Mock
    private IPedidoResponseMapper pedidoResponseMapper;
    @Mock
    private IPaginationResponseMapper paginationResponseMapper;
    @Mock
    private IPaginationRequestMapper paginationRequestMapper;

    @InjectMocks
    private PedidoHandler pedidoHandler;

    private UserModel clientModel;
    private PedidoResponseDto responseDto;

    @BeforeEach
    void setUp() {
        clientModel = UserModel.builder().id(CLIENTE_ID).correo("cliente@mail.com").build();
        responseDto = PedidoResponseDto.builder().id(PEDIDO_ID).estado(PedidoEstado.CANCELADO).build();
    }

    @Test
    @DisplayName("save() debería mapear DTO, llamar al servicio con el cliente y retornar la respuesta")
    void save_ShouldConvertAndCallService() {
        UserModel clientMock = mock(UserModel.class);
        PedidoRequestDto requestDto = PedidoRequestDto.builder().build();
        PedidoModel inputModel = PedidoModel.builder().id(1L).build();
        PedidoModel savedModel = PedidoModel.builder().id(1L).build();

        when(pedidoRequestMapper.toModel(requestDto)).thenReturn(inputModel);
        when(pedidoService.save(clientMock, inputModel)).thenReturn(savedModel);
        when(pedidoResponseMapper.toResponse(savedModel)).thenReturn(responseDto);

        PedidoResponseDto result = pedidoHandler.save(clientMock, requestDto);

        assertNotNull(result);
        assertEquals(responseDto, result);

        verify(pedidoRequestMapper).toModel(requestDto);
        verify(pedidoService).save(clientMock, inputModel);
        verify(pedidoResponseMapper).toResponse(savedModel);
    }

    @Test
    @DisplayName("update() debería mapear DTO de actualización, llamar al servicio con el empleado y retornar la respuesta")
    void update_ShouldConvertAndCallService() {
        UserModel employeeMock = mock(UserModel.class);
        PedidoUpdateDto updateDto = PedidoUpdateDto.builder().build();
        PedidoModel inputModel = PedidoModel.builder().build();
        PedidoModel updatedModel = PedidoModel.builder().build();

        when(pedidoUpdateMapper.toModel(updateDto)).thenReturn(inputModel);
        when(pedidoService.update(employeeMock, inputModel)).thenReturn(updatedModel);
        when(pedidoResponseMapper.toResponse(updatedModel)).thenReturn(responseDto);

        PedidoResponseDto result = pedidoHandler.update(employeeMock, updateDto);

        assertNotNull(result);
        assertEquals(responseDto, result);

        verify(pedidoUpdateMapper).toModel(updateDto);
        verify(pedidoService).update(employeeMock, inputModel);
        verify(pedidoResponseMapper).toResponse(updatedModel);
    }

    @Test
    @DisplayName("cancel() debe construir DTO, mapear, llamar al servicio y retornar la respuesta")
    void cancel_ShouldBuildDtoMapAndCallService() {
        UserModel employeeMock = mock(UserModel.class);
        PedidoUpdateDto updateDto = PedidoUpdateDto.builder().id(PEDIDO_ID).build();
        PedidoModel inputModel = PedidoModel.builder().id(PEDIDO_ID).build();
        PedidoModel updatedModel = PedidoModel.builder().id(PEDIDO_ID).build();

        when(pedidoUpdateMapper.toModel(updateDto)).thenReturn(inputModel);
        when(pedidoService.cancel(employeeMock, inputModel)).thenReturn(updatedModel);
        when(pedidoResponseMapper.toResponse(updatedModel)).thenReturn(responseDto);

        PedidoResponseDto result = pedidoHandler.cancel(employeeMock, updatedModel.getId());

        assertNotNull(result);
        assertEquals(responseDto, result);

        verify(pedidoUpdateMapper).toModel(updateDto);
        verify(pedidoService).cancel(employeeMock, inputModel);
        verify(pedidoResponseMapper).toResponse(updatedModel);
    }

    @Test
    @DisplayName("getById() debería obtener el modelo del servicio y retornarlo como DTO")
    void getById_ShouldReturnDto() {
        Long pedidoId = 1L;
        PedidoModel pedidoModel = PedidoModel.builder().id(pedidoId).build();
        PedidoResponseDto responseDto = PedidoResponseDto.builder().id(pedidoId).build();

        when(pedidoService.getById(pedidoId)).thenReturn(pedidoModel);
        when(pedidoResponseMapper.toResponse(pedidoModel)).thenReturn(responseDto);

        PedidoResponseDto result = pedidoHandler.getById(pedidoId);

        assertNotNull(result);
        assertEquals(responseDto, result);

        verify(pedidoService).getById(pedidoId);
        verify(pedidoResponseMapper).toResponse(pedidoModel);
    }

    @Test
    @DisplayName("getAll() debería manejar la paginación, mapear el resultado y devolver respuesta paginada")
    void getAll_ShouldReturnPaginationResponse() {
        // Arrange
        Long userId = 10L;
        Long restaurantId = 5L;
        PedidoEstado estado = PedidoEstado.PENDIENTE;
        PaginationRequestDto paginationRequest = new PaginationRequestDto();

        PaginationInfo paginationModel = mock(PaginationInfo.class);
        PaginationResult<PedidoModel> pedidosListMock = mock(PaginationResult.class);
        PaginationResult<PedidoResponseDto> mappedResultMock = mock(PaginationResult.class);
        PaginationResponseDto<PedidoResponseDto> finalResponse = new PaginationResponseDto<>();

        when(paginationRequestMapper.toModel(paginationRequest)).thenReturn(paginationModel);
        when(pedidoService.getAll(eq(userId), eq(restaurantId), eq(estado), eq(paginationModel))).thenReturn(pedidosListMock);
        doReturn(mappedResultMock).when(pedidosListMock).map(any());
        when(paginationResponseMapper.toResponse(mappedResultMock)).thenReturn(finalResponse);

        PaginationResponseDto<PedidoResponseDto> result = pedidoHandler.getAll(userId, restaurantId, estado, paginationRequest);

        assertNotNull(result);
        assertEquals(finalResponse, result);

        verify(paginationRequestMapper).toModel(paginationRequest);
        verify(pedidoService).getAll(eq(userId), eq(restaurantId), eq(estado), eq(paginationModel));
        verify(paginationResponseMapper).toResponse(mappedResultMock);
        verify(pedidosListMock).map(any());
    }

}
