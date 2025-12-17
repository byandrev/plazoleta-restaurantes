package com.pragma.powerup.infrastructure.out.feign.adapter;

import com.pragma.powerup.domain.model.*;
import com.pragma.powerup.domain.spi.ITraceabilityExternalServicePort;
import com.pragma.powerup.infrastructure.exception.ResourceNotFound;
import com.pragma.powerup.infrastructure.input.rest.response.CustomResponse;
import com.pragma.powerup.infrastructure.out.feign.ITraceabilityFeignClient;
import com.pragma.powerup.infrastructure.out.feign.mapper.ITraceabilityFeignMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IPaginationMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TraceabilityExternalAdapter implements ITraceabilityExternalServicePort {

    private final ITraceabilityFeignClient client;
    private final ITraceabilityFeignMapper mapper;
    private final IPaginationMapper paginationMapper;

    @Override
    public void save(TraceabilityModel traceabilityModel) {
        client.save(mapper.toDto(traceabilityModel));
    }

    @Override
    public List<TraceabilityModel> getHistory(Long pedidoId) {
        try {
            CustomResponse<List<TraceabilityModel>> response = client.getHistory(pedidoId);
            return response.getData();
        } catch (FeignException ex) {
            throw new ResourceNotFound("El pedido " + pedidoId + " no existe");
        }
    }

    @Override
    public PaginationResult<PedidoTimeModel> getTimePedidos(Long restaurantId, PaginationInfo pagination) {
        CustomResponse<PaginationResult<PedidoTimeModel>> response = client.getTimePedidos(
                restaurantId,
                paginationMapper.toPageable(pagination)
        );
        return response.getData();
    }

    @Override
    public PaginationResult<EmpleadoTiempoModel> getTimeEmpleados(Long restaurantId, PaginationInfo pagination) {
        try {
            CustomResponse<PaginationResult<EmpleadoTiempoModel>> response = client.getTimeEmpleados(
                    restaurantId,
                    paginationMapper.toPageable(pagination)
            );
            return response.getData();
        } catch (FeignException ex) {
            throw new ResourceNotFound("Error al obtener el ranking de empleados del restaurante " + restaurantId);
        }
    }

}
