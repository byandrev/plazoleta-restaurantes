package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationInfo {

    private int page;
    private int size;
    private String sortBy;
    private String sortDirection;

}
