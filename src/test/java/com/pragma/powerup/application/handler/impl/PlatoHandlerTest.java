package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.PaginationRequestDto;
import com.pragma.powerup.application.dto.request.PlatoRequestDto;
import com.pragma.powerup.application.dto.request.PlatoUpdateDto;
import com.pragma.powerup.application.dto.response.PaginationResponseDto;
import com.pragma.powerup.application.dto.response.PlatoResponseDto;
import com.pragma.powerup.application.mapper.IPaginationRequestMapper;
import com.pragma.powerup.application.mapper.IPaginationResponseMapper;
import com.pragma.powerup.application.mapper.IPlatoRequestMapper;
import com.pragma.powerup.application.mapper.IPlatoResponseMapper;
import com.pragma.powerup.domain.api.IPlatoServicePort;
import com.pragma.powerup.domain.model.PaginationInfo;
import com.pragma.powerup.domain.model.PaginationResult;
import com.pragma.powerup.domain.model.PlatoModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatoHandlerTest {

    @Mock
    private IPlatoServicePort platoService;
    @Mock
    private IPlatoRequestMapper platoRequestMapper;
    @Mock
    private IPlatoResponseMapper platoResponseMapper;
    @Mock
    private IPaginationResponseMapper paginationResponseMapper;
    @Mock
    private IPaginationRequestMapper paginationRequestMapper;

    @InjectMocks
    private PlatoHandler platoHandler;

    @Test
    @DisplayName("save() debería convertir el DTO a modelo y llamar al servicio")
    void save_ShouldConvertAndCallService() {
        Long userId = 1L;
        PlatoRequestDto requestDto = PlatoRequestDto.builder().build();
        PlatoModel platoModel = PlatoModel.builder().build();

        when(platoRequestMapper.toModel(requestDto)).thenReturn(platoModel);

        platoHandler.save(userId, requestDto);

        verify(platoRequestMapper).toModel(requestDto);
        verify(platoService).save(userId, platoModel);
    }

    @Test
    @DisplayName("getAll() debería procesar la paginación, mapear resultados y devolver respuesta paginada")
    void getAll_ShouldReturnPaginationResponse() {
        String categoria = "Entradas";
        Long restauranteId = 10L;
        PaginationRequestDto paginationRequest = new PaginationRequestDto();
        PaginationInfo paginationModel = new PaginationInfo();

        PaginationResult<PlatoModel> paginationResultMock = mock(PaginationResult.class);
        PaginationResult<PlatoResponseDto> mappedResultMock = mock(PaginationResult.class);
        PaginationResponseDto<PlatoResponseDto> finalResponse = new PaginationResponseDto<>();

        when(paginationRequestMapper.toModel(paginationRequest)).thenReturn(paginationModel);
        when(platoService.getAll(eq(categoria), eq(restauranteId), any())).thenReturn(paginationResultMock);

        Mockito.doReturn(mappedResultMock).when(paginationResultMock).map(any());

        when(paginationResponseMapper.toResponse(mappedResultMock)).thenReturn(finalResponse);

        PaginationResponseDto<PlatoResponseDto> result = platoHandler.getAll(categoria, restauranteId, paginationRequest);

        assertNotNull(result);
        assertEquals(finalResponse, result);

        verify(paginationRequestMapper).toModel(paginationRequest);
        verify(platoService).getAll(eq(categoria), eq(restauranteId), any());
        verify(paginationResultMock).map(any());
        verify(paginationResponseMapper).toResponse(mappedResultMock);
    }

    @Test
    @DisplayName("getById() debería obtener el modelo del servicio y retornarlo como DTO")
    void getById_ShouldReturnDto() {
        Long platoId = 5L;
        PlatoModel platoModel = PlatoModel.builder().build();
        PlatoResponseDto responseDto = PlatoResponseDto.builder().build();

        when(platoService.getById(platoId)).thenReturn(platoModel);
        when(platoResponseMapper.toResponse(platoModel)).thenReturn(responseDto);

        PlatoResponseDto result = platoHandler.getById(platoId);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(platoService).getById(platoId);
        verify(platoResponseMapper).toResponse(platoModel);
    }

    @Test
    @DisplayName("update() debería convertir DTO, llamar al servicio y devolver respuesta mapeada")
    void update_ShouldUpdateAndReturnDto() {
        Long userId = 1L;
        Long platoId = 5L;
        PlatoUpdateDto updateDto = PlatoUpdateDto.builder().build();

        PlatoModel inputModel = PlatoModel.builder().build();
        PlatoModel updatedModel = PlatoModel.builder().build();
        PlatoResponseDto responseDto = PlatoResponseDto.builder().build();

        when(platoRequestMapper.toModel(updateDto)).thenReturn(inputModel);
        when(platoService.update(userId, platoId, inputModel)).thenReturn(updatedModel);
        when(platoResponseMapper.toResponse(updatedModel)).thenReturn(responseDto);

        PlatoResponseDto result = platoHandler.update(userId, platoId, updateDto);

        assertNotNull(result);
        assertEquals(responseDto, result);

        verify(platoRequestMapper).toModel(updateDto);
        verify(platoService).update(userId, platoId, inputModel);
        verify(platoResponseMapper).toResponse(updatedModel);
    }

}