package com.pragma.powerup.infrastructure.out.feign;

import com.pragma.powerup.application.dto.request.TraceabilityRequestDto;
import com.pragma.powerup.domain.model.EmpleadoTiempoModel;
import com.pragma.powerup.domain.model.PaginationResult;
import com.pragma.powerup.domain.model.PedidoTimeModel;
import com.pragma.powerup.domain.model.TraceabilityModel;
import com.pragma.powerup.infrastructure.input.rest.response.CustomResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "traceability-service", url = "http://localhost:8083")
public interface ITraceabilityFeignClient {

    @PostMapping("/api/v1/trazabilidad/")
    void save(@RequestBody TraceabilityRequestDto request);

    @GetMapping("/api/v1/trazabilidad/{pedidoId}")
    CustomResponse<List<TraceabilityModel>> getHistory(@PathVariable Long pedidoId);

    @GetMapping("/api/v1/trazabilidad/pedidos/")
    CustomResponse<PaginationResult<PedidoTimeModel>> getTimePedidos(
            @RequestParam Long restauranteId,
            @QueryMap Pageable pagination
    );

    @GetMapping("/api/v1/trazabilidad/empleados/")
    CustomResponse<PaginationResult<EmpleadoTiempoModel>> getTimeEmpleados(
            @RequestParam Long restauranteId,
            @QueryMap Pageable pagination
    );

}
