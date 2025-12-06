package com.pragma.powerup.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RestaurantResponseDto {

    private Long id;

    private String nombre;

    private String urlLogo;

}
