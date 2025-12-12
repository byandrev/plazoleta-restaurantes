package com.pragma.powerup.domain.model;

import lombok.Data;

@Data
public class PaginationInfo {

    private int page;
    private int size;
    private String sortBy;
    private String sortDirection;

}
