package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.PaginationResponseDto;
import com.pragma.powerup.domain.model.PaginationResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IPaginationResponseMapper {

    default <T> PaginationResponseDto<T> toResponse(PaginationResult<T> result) {
        if ( result == null ) {
            return null;
        }

        return new PaginationResponseDto<>(
                result.getContent(),
                result.getTotalPages(),
                result.getTotalElements(),
                result.getNumber()
        );
    }

}
