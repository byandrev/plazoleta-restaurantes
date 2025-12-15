package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.TraceabilityModel;

import java.util.List;

public interface ITraceabilityExternalServicePort {

    void save(TraceabilityModel traceabilityModel);

    List<TraceabilityModel> getHistory(Long pedidoId);

}
