package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.handler.IRestaurantHandler;
import com.pragma.powerup.infrastructure.input.rest.response.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantRestController {

    private final IRestaurantHandler  restaurantHandler;

    @Operation(summary = "Get restaurants")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurants returned"),
            @ApiResponse(responseCode = "404", description = "No data found", content = @Content)
    })
    @GetMapping("/")
    public ResponseEntity<CustomResponse<Page<RestaurantResponseDto>>> getRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        CustomResponse<Page<RestaurantResponseDto>> response = CustomResponse.<Page<RestaurantResponseDto>>builder()
                .status(HttpStatus.OK.value())
                .data(restaurantHandler.getAll(page, size))
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get by ID restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurants returned"),
            @ApiResponse(responseCode = "404", description = "No data found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<RestaurantResponseDto>> getRestaurant(@PathVariable Long id) {
        CustomResponse<RestaurantResponseDto> response = CustomResponse.<RestaurantResponseDto>builder()
                .status(HttpStatus.OK.value())
                .data(restaurantHandler.getById(id))
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Add a new restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant created", content = @Content),
            @ApiResponse(responseCode = "409", description = "Restaurant already exists", content = @Content)
    })
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/")
    public ResponseEntity<Void> saveRestaurant(@Valid @RequestBody RestaurantRequestDto restaurantRequestDto) {
        restaurantHandler.save(restaurantRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
