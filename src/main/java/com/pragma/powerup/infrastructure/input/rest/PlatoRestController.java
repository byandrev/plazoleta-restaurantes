package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.PaginationRequestDto;
import com.pragma.powerup.application.dto.request.PlatoRequestDto;
import com.pragma.powerup.application.dto.request.PlatoUpdateDto;
import com.pragma.powerup.application.dto.response.PaginationResponseDto;
import com.pragma.powerup.application.dto.response.PlatoResponseDto;
import com.pragma.powerup.application.handler.IPlatoHandler;
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
@Validated
@RequestMapping("/api/v1/platos")
@RequiredArgsConstructor
public class PlatoRestController {

    private final IPlatoHandler platoHandler;

    @Operation(summary = "Get platos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Platos returned"),
            @ApiResponse(responseCode = "404", description = "No data found", content = @Content)
    })
    @GetMapping("/")
    public ResponseEntity<CustomResponse<PaginationResponseDto<PlatoResponseDto>>> getRestaurants(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
            @Valid PaginationRequestDto paginationRequest,
            @RequestParam @Min(value = 0) long restaurantId,
            @RequestParam(required = false) String categoria
    ) {
        CustomResponse<PaginationResponseDto<PlatoResponseDto>> response = CustomResponse.<PaginationResponseDto<PlatoResponseDto>>builder()
                .status(HttpStatus.OK.value())
                .data(platoHandler.getAll(categoria, restaurantId, paginationRequest))
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Add new plato")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plato created", content = @Content),
            @ApiResponse(responseCode = "409", description = "Plato already exists", content = @Content)
    })
    @PreAuthorize("hasRole('PROPIETARIO')")
    @PostMapping("/")
    public ResponseEntity<Void> savePlato(
            @Valid @RequestBody PlatoRequestDto platoRequestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetail userDetails
    ) {
        platoHandler.save(userDetails.getId(), platoRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Get plato by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Plato returned"
            ),
            @ApiResponse(responseCode = "404", description = "No data found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<PlatoResponseDto>> getPlatoById(@PathVariable Long id) {
        CustomResponse<PlatoResponseDto> response = CustomResponse.<PlatoResponseDto>builder()
                .status(HttpStatus.OK.value())
                .data(platoHandler.getById(id))
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update plato")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plato updated"),
            @ApiResponse(responseCode = "404", description = "Plato not found", content = @Content)
    })
    @PreAuthorize("hasRole('PROPIETARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse<PlatoResponseDto>> updatePlato(
            @Valid @PathVariable Long id,
            @RequestBody PlatoUpdateDto platoUpdateDto,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetail userDetails
    ) {
        CustomResponse<PlatoResponseDto> response = CustomResponse.<PlatoResponseDto>builder()
                .status(HttpStatus.OK.value())
                .data(platoHandler.update(userDetails.getId(), id, platoUpdateDto))
                .build();

        return ResponseEntity.ok(response);
    }

}
