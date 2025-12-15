package com.pragma.powerup.infrastructure.out.feign.adapter;

import com.pragma.powerup.domain.model.TraceabilityModel;
import com.pragma.powerup.domain.spi.ITraceabilityExternalServicePort;
import com.pragma.powerup.infrastructure.exception.ResourceNotFound;
import com.pragma.powerup.infrastructure.input.rest.response.CustomResponse;
import com.pragma.powerup.infrastructure.out.feign.ITraceabilityFeignClient;
import com.pragma.powerup.infrastructure.out.feign.mapper.ITraceabilityFeignMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TraceabilityExternalAdapter implements ITraceabilityExternalServicePort {

    private final ITraceabilityFeignClient client;

    private final ITraceabilityFeignMapper mapper;

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

}
