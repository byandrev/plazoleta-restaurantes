package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.PedidoItemRequestDto;
import com.pragma.powerup.application.dto.request.PedidoRequestDto;
import com.pragma.powerup.application.mapper.IPedidoRequestMapper;
import com.pragma.powerup.application.mapper.IPedidoResponseMapper;
import com.pragma.powerup.domain.api.IPedidoServicePort;
import com.pragma.powerup.domain.model.PedidoModel;
import com.pragma.powerup.domain.model.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoHandlerTest {

    @Mock
    private IPedidoServicePort pedidoServicePort;

    @Mock
    private IPedidoRequestMapper pedidoRequestMapper;

    @Mock
    private IPedidoResponseMapper pedidoResponseMapper;

    @InjectMocks
    private PedidoHandler pedidoHandler;

    private PedidoRequestDto pedidoRequestDto;
    private PedidoModel pedidoModel;
    private UserModel client;

    @BeforeEach
    void setUp() {
        pedidoRequestDto = PedidoRequestDto
                .builder()
                .idRestaurante(1L)
                .idChef(2L)
                .items(Set.of(PedidoItemRequestDto.builder().platoId(10L).cantidad(2).build()))
                .build();

        pedidoModel = PedidoModel.builder().build();

        client = UserModel
                .builder()
                .id(10L)
                .correo("user@gmail.com")
                .build();
    }

    @Test
    @DisplayName("save debe mapear DTO a Model y llamar al servicio para guardar")
    void save_SuccessfulFlow_CallsMapperAndService() {
        when(pedidoRequestMapper.toModel(pedidoRequestDto)).thenReturn(pedidoModel);

        pedidoHandler.save(client, pedidoRequestDto);

        verify(pedidoRequestMapper).toModel(pedidoRequestDto);
        verify(pedidoServicePort).save(client, pedidoModel);

        verifyNoMoreInteractions(pedidoServicePort, pedidoRequestMapper);
    }

}
