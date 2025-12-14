package com.pragma.powerup.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

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

    @Pattern(regexp = "(?i)^(ASC|DESC)$", message = "La dirección debe ser 'ASC' o 'DESC'")
    private String sortDirection = "ASC";

    public Sort.Direction getSortDirectionEnum() {
        return Sort.Direction.fromString(sortDirection.toUpperCase());
    }

}
