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
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Platos", description = "Endpoints para la gestión del menú (creación, actualización y consulta de platos)")
public class PlatoRestController {

    private final IPlatoHandler platoHandler;

    @Operation(
            summary = "Listar platos por restaurante",
            description = "Obtiene una lista paginada de platos filtrados por restaurante y, opcionalmente, por categoría."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de platos obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros de consulta inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron platos para los criterios proporcionados", content = @Content)
    })
    @GetMapping("/")
    public ResponseEntity<CustomResponse<PaginationResponseDto<PlatoResponseDto>>> getRestaurants(
            @Valid PaginationRequestDto paginationRequest,
            @Parameter(description = "ID del restaurante propietario del menú", example = "1")
            @RequestParam @Min(value = 0) long restaurantId,
            @Parameter(description = "Nombre de la categoría para filtrar (opcional)", example = "Entradas")
            @RequestParam(required = false) String categoria
    ) {
        CustomResponse<PaginationResponseDto<PlatoResponseDto>> response = CustomResponse.<PaginationResponseDto<PlatoResponseDto>>builder()
                .status(HttpStatus.OK.value())
                .data(platoHandler.getAll(categoria, restaurantId, paginationRequest))
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Crear un nuevo plato",
            description = "Permite al propietario registrar un nuevo plato en su restaurante. **Solo accesible por PROPIETARIO.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plato creado exitosamente", content = @Content),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para crear platos", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflicto: El plato ya existe o datos duplicados", content = @Content)
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

    @Operation(
            summary = "Obtener detalle de un plato",
            description = "Retorna la información completa de un plato a partir de su ID único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Datos del plato obtenidos"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<PlatoResponseDto>> getPlatoById(
            @Parameter(description = "ID del plato a consultar", example = "10")
            @PathVariable Long id
    ) {
        CustomResponse<PlatoResponseDto> response = CustomResponse.<PlatoResponseDto>builder()
                .status(HttpStatus.OK.value())
                .data(platoHandler.getById(id))
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Actualizar información de un plato",
            description = "Permite modificar el precio o la descripción de un plato existente. **Solo accesible por el PROPIETARIO dueño del plato.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plato actualizado correctamente"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para modificar este plato", content = @Content),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado", content = @Content)
    })
    @PreAuthorize("hasRole('PROPIETARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse<PlatoResponseDto>> updatePlato(
            @Parameter(description = "ID del plato a modificar")
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
