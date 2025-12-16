package com.pragma.powerup.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class EmployeeRequestDto {

    @NotNull(message = "El userId no puede estar vacio")
    private Long userId;

    @Schema(hidden = true)
    private Long restaurantId;

}
