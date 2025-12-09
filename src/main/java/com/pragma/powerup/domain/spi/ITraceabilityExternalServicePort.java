package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.TraceabilityModel;

public interface ITraceabilityExternalServicePort {

    void save(TraceabilityModel traceabilityModel);

}
