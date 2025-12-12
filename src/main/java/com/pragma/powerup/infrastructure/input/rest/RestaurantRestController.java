package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.EmployeeRequestDto;
import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.PaginationResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.handler.IRestaurantHandler;
import com.pragma.powerup.infrastructure.input.rest.response.CustomResponse;
import com.pragma.powerup.infrastructure.out.security.models.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Validated
public class RestaurantRestController {

    private final IRestaurantHandler  restaurantHandler;

    @Operation(summary = "Get restaurants")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurants returned"),
            @ApiResponse(responseCode = "404", description = "No data found", content = @Content)
    })
    @GetMapping("/")
    public ResponseEntity<CustomResponse<PaginationResponseDto<RestaurantResponseDto>>> getRestaurants(
            @RequestParam(defaultValue = "0") @Min(value = 0) int page,
            @RequestParam(defaultValue = "10") @Min(value = 1) int size
    ) {
        CustomResponse<PaginationResponseDto<RestaurantResponseDto>> response = CustomResponse.<PaginationResponseDto<RestaurantResponseDto>>builder()
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

    @Operation(summary = "Assign employee to restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee assigned", content = @Content),
            @ApiResponse(responseCode = "404", description = "Restaurant not found", content = @Content)
    })
    @PreAuthorize("hasRole('PROPIETARIO')")
    @PostMapping("/{restaurantId}/employees")
    public ResponseEntity<Void> assignEmployee(
            @PathVariable Long restaurantId,
            @Valid @RequestBody EmployeeRequestDto employeeRequest,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        employeeRequest.setRestaurantId(restaurantId);
        restaurantHandler.assignEmployee(userDetail.getId(), employeeRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
