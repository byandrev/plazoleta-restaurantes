package com.pragma.powerup.infrastructure.out.feign.adapter;

import com.pragma.powerup.domain.model.TraceabilityModel;
import com.pragma.powerup.domain.spi.ITraceabilityExternalServicePort;
import com.pragma.powerup.infrastructure.out.feign.ITraceabilityFeignClient;
import com.pragma.powerup.infrastructure.out.feign.mapper.ITraceabilityFeignMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TraceabilityExternalAdapter implements ITraceabilityExternalServicePort {

    private final ITraceabilityFeignClient client;

    private final ITraceabilityFeignMapper mapper;

    @Override
    public void save(TraceabilityModel traceabilityModel) {
        client.save(mapper.toDto(traceabilityModel));
    }

}
