package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.PaginationRequestDto;
import com.pragma.powerup.domain.model.PaginationInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IPaginationRequestMapper {

    PaginationInfo toModel (PaginationRequestDto paginationRequestDto);

}
