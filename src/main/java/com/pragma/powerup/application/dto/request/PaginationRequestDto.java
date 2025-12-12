package com.pragma.powerup.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationRequestDto {

    @Min(value = 0, message = "La página no puede ser menor a 0")
    private int page = 0;

    @Min(value = 1, message = "El tamaño debe ser al menos 1")
    @Max(value = 50, message = "El tamaño no puede exceder 50")
    private int size = 10;

    private String sortBy = "id";

    private String sortDirection = "ASC";

}
