package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.PaginationResult;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface IPaginationResultMapper {

    default <T> PaginationResult<T> toModel(Page<T> result) {
        if ( result == null ) {
            return null;
        }

        return new PaginationResult<>(
                result.getContent(),
                result.getTotalPages(),
                result.getTotalElements(),
                result.getNumber()
        );
    }

}

