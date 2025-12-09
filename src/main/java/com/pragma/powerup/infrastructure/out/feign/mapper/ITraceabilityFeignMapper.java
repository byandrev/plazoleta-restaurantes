package com.pragma.powerup.infrastructure.out.feign.mapper;

import com.pragma.powerup.application.dto.request.TraceabilityRequestDto;
import com.pragma.powerup.domain.model.TraceabilityModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ITraceabilityFeignMapper {

    TraceabilityRequestDto toDto(TraceabilityModel model);

}
