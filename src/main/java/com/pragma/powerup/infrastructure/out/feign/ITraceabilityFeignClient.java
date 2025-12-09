package com.pragma.powerup.infrastructure.out.feign;

import com.pragma.powerup.application.dto.request.TraceabilityRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "traceability-service", url = "http://localhost:8083")
public interface ITraceabilityFeignClient {

    @PostMapping("/api/v1/trazabilidad/")
    void save(@RequestBody TraceabilityRequestDto request);

}
