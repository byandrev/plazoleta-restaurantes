package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.PaginationInfo;
import com.pragma.powerup.domain.model.PaginationResult;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Mapper(componentModel = "spring")
public interface IPaginationMapper {

    default <T> PaginationResult<T> toModel(Page<T> result) {
        if (result == null) {
            return null;
        }

        return new PaginationResult<>(
                result.getContent(),
                result.getTotalPages(),
                result.getTotalElements(),
                result.getNumber()
        );
    }

    default PageRequest toPageable(PaginationInfo pagination) {
        if (pagination == null) {
            return null;
        }

        Sort sort = Sort.by(pagination.getSortBy());

        if (pagination.getSortDirection().equals("ASC")) {
            sort = sort.ascending();
        } else {
            sort = sort.descending();
        }

        return PageRequest.of(
                pagination.getPage(),
                pagination.getSize(),
                sort
        );
    }

}

