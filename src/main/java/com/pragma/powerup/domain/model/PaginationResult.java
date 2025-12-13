package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationResult<T> {

    private List<T> content;

    private int totalPages;

    private long totalElements;

    private int number;

    public <U> PaginationResult<U> map(Function<? super T, ? extends U> converter) {
        List<U> mappedContent = this.content.stream()
                .map(converter)
                .collect(Collectors.toList());

        return new PaginationResult<>(
                mappedContent,
                this.totalPages,
                this.totalElements,
                this.number
        );
    }

}
