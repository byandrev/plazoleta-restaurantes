package com.pragma.powerup.infrastructure.out.feign;

import com.pragma.powerup.application.dto.request.TraceabilityRequestDto;
import com.pragma.powerup.domain.model.TraceabilityModel;
import com.pragma.powerup.infrastructure.input.rest.response.CustomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "traceability-service", url = "http://localhost:8083")
public interface ITraceabilityFeignClient {

    @PostMapping("/api/v1/trazabilidad/")
    void save(@RequestBody TraceabilityRequestDto request);

    @GetMapping("/api/v1/trazabilidad/{pedidoId}")
    CustomResponse<List<TraceabilityModel>> getHistory(@PathVariable Long pedidoId);


}
