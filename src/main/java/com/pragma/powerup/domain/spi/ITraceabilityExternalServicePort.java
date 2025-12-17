package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.*;

import java.util.List;

public interface ITraceabilityExternalServicePort {

    void save(TraceabilityModel traceabilityModel);

    List<TraceabilityModel> getHistory(Long pedidoId);

    PaginationResult<PedidoTimeModel> getTimePedidos(Long restaurantId, PaginationInfo pagination);

    PaginationResult<EmpleadoTiempoModel> getTimeEmpleados(Long restaurantId, PaginationInfo pagination);

}
