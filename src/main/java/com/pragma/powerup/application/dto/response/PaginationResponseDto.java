package com.pragma.powerup.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaginationResponseDto<T> {

    private List<T> content;

    private int totalPages;

    private long totalElements;

    private int number;

}
