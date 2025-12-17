package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.EmployeeRequestDto;
import com.pragma.powerup.application.dto.request.PaginationRequestDto;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Validated
@Tag(name = "Restaurantes", description = "Endpoints para la gestión de restaurantes, listados y asignación de personal")
public class RestaurantRestController {

    private final IRestaurantHandler  restaurantHandler;

    @Operation(
            summary = "Listar restaurantes con paginación",
            description = "Retorna una lista paginada de todos los restaurantes registrados, ordenados alfabéticamente por nombre."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron restaurantes", content = @Content)
    })
    @GetMapping("/")
    public ResponseEntity<CustomResponse<PaginationResponseDto<RestaurantResponseDto>>> getRestaurants(
            @Parameter(description = "Configuración de página y tamaño", required = true)
            @Valid PaginationRequestDto paginationRequest
    ) {
        CustomResponse<PaginationResponseDto<RestaurantResponseDto>> response = CustomResponse.<PaginationResponseDto<RestaurantResponseDto>>builder()
                .status(HttpStatus.OK.value())
                .data(restaurantHandler.getAll(paginationRequest))
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obtener restaurante por ID",
            description = "Busca y retorna la información detallada de un restaurante específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado"),
            @ApiResponse(responseCode = "404", description = "El ID del restaurante no existe", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<RestaurantResponseDto>> getRestaurant(
            @Parameter(description = "ID único del restaurante", example = "1")
            @PathVariable Long id
    ) {
        CustomResponse<RestaurantResponseDto> response = CustomResponse.<RestaurantResponseDto>builder()
                .status(HttpStatus.OK.value())
                .data(restaurantHandler.getById(id))
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Crear un nuevo restaurante",
            description = "Registra un nuevo restaurante en el sistema. **Solo accesible por el rol ADMINISTRADOR.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurante creado con éxito", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "No tiene permisos de administrador", content = @Content),
            @ApiResponse(responseCode = "409", description = "Ya existe un restaurante con el mismo NIT", content = @Content)
    })
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/")
    public ResponseEntity<Void> saveRestaurant(@Valid @RequestBody RestaurantRequestDto restaurantRequestDto) {
        restaurantHandler.save(restaurantRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(
            summary = "Asignar empleado a un restaurante",
            description = "Vincula a un usuario con el rol EMPLEADO a un restaurante específico. **Solo accesible por el PROPIETARIO del restaurante.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empleado vinculado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Restaurante o Empleado no encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "El usuario no es el propietario de este restaurante", content = @Content)
    })
    @PreAuthorize("hasRole('PROPIETARIO')")
    @PostMapping("/{restaurantId}/employees")
    public ResponseEntity<Void> assignEmployee(
            @Parameter(description = "ID del restaurante al que se asignará el empleado", example = "1")
            @PathVariable Long restaurantId,
            @Valid @RequestBody EmployeeRequestDto employeeRequest,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        employeeRequest.setRestaurantId(restaurantId);
        restaurantHandler.assignEmployee(userDetail.getId(), employeeRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
