package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.PaginationRequestDto;
import com.pragma.powerup.application.dto.request.PedidoRequestDto;
import com.pragma.powerup.application.dto.request.PedidoUpdateDto;
import com.pragma.powerup.application.dto.response.*;
import com.pragma.powerup.application.handler.IPedidoHandler;
import com.pragma.powerup.domain.model.PedidoEstado;
import com.pragma.powerup.domain.model.UserModel;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Endpoints para la gestión de pedidos, trazabilidad y métricas de tiempo")
public class PedidoRestController {

    private final IPedidoHandler pedidoHandler;

    @Operation(
            summary = "Crear un nuevo pedido",
            description = "Permite a un cliente registrar un pedido con múltiples platos de un mismo restaurante. **Solo accesible por CLIENTE.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos o platos de diferentes restaurantes", content = @Content),
            @ApiResponse(responseCode = "409", description = "El cliente ya tiene un pedido en curso", content = @Content)
    })
    @PostMapping("/")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> savePedido(
            @Valid @RequestBody PedidoRequestDto pedidoRequestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetail userDetails
    ) {
        UserModel client = UserModel
                .builder()
                .id(userDetails.getId())
                .correo(userDetails.getEmail())
                .build();

        pedidoHandler.save(client, pedidoRequestDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(
            summary = "Listar pedidos por restaurante y estado",
            description = "Permite a un empleado visualizar los pedidos asignados a su restaurante, filtrados opcionalmente por estado. **Solo accesible por EMPLEADO.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida correctamente"),
            @ApiResponse(responseCode = "403", description = "El empleado no pertenece al restaurante especificado", content = @Content)
    })
    @PreAuthorize("hasRole('EMPLEADO')")
    @GetMapping("/{restaurantId}")
    public ResponseEntity<CustomResponse<PaginationResponseDto<PedidoResponseDto>>> getPedidos(
            @Valid PaginationRequestDto paginationRequest,
            @Parameter(description = "Estado del pedido", example = "PENDIENTE")
            @RequestParam(required = false) PedidoEstado estado,
            @Parameter(description = "ID del restaurante", example = "1")
            @PathVariable Long restaurantId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetail userDetails
    ) {
        Long userId = userDetails.getId();

        CustomResponse<PaginationResponseDto<PedidoResponseDto>> response = CustomResponse.<PaginationResponseDto<PedidoResponseDto>>builder()
                .status(HttpStatus.OK.value())
                .data(pedidoHandler.getAll(userId, restaurantId, estado, paginationRequest))
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Actualizar estado del pedido",
            description = "Cambia el estado del pedido. **Solo accesible por EMPLEADO.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Transición de estado no permitida", content = @Content)
    })
    @PatchMapping("/{pedidoId}")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<Void> updatePedido(
            @Parameter(description = "ID del pedido a actualizar")
            @PathVariable Long pedidoId,
            @Valid @RequestBody PedidoUpdateDto pedidoUpdateDto,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetail userDetails
    ) {
        UserModel client = UserModel
                .builder()
                .id(userDetails.getId())
                .correo(userDetails.getEmail())
                .build();

        pedidoUpdateDto.setId(pedidoId);

        pedidoHandler.update(client, pedidoUpdateDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Cancelar un pedido",
            description = "Permite al cliente cancelar un pedido siempre y cuando no esté en preparación. **Solo accesible por CLIENTE.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido cancelado correctamente"),
            @ApiResponse(responseCode = "409", description = "El pedido no se puede cancelar en su estado actual", content = @Content)
    })
    @PatchMapping("/cancelar/{pedidoId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> cancelPedido(
            @PathVariable Long pedidoId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetail userDetails
    ) {
        UserModel client = UserModel
                .builder()
                .id(userDetails.getId())
                .correo(userDetails.getEmail())
                .build();

        pedidoHandler.cancel(client, pedidoId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Consultar historial de trazabilidad",
            description = "Obtiene todos los cambios de estado por los que ha pasado un pedido específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de trazabilidad obtenido exitosamente")
    })
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/trazabilidad/{pedidoId}")
    public ResponseEntity<CustomResponse<List<TraceabilityResponseDto>>> getHistorial(
            @Parameter(description = "ID del pedido")
            @PathVariable Long pedidoId
    ) {
        CustomResponse<List<TraceabilityResponseDto>> response = CustomResponse.<List<TraceabilityResponseDto>>builder()
                .status(HttpStatus.OK.value())
                .data(pedidoHandler.getHistory(pedidoId))
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Reporte de tiempos de pedidos",
            description = "Reporte que muestra cuánto tiempo tomó cada pedido en completarse. **Solo accesible por PROPIETARIO.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado correctamente"),
            @ApiResponse(responseCode = "409", description = "El usuario no es dueño del restaurante", content = @Content)
    })
    @PreAuthorize("hasRole('PROPIETARIO')")
    @GetMapping("/trazabilidad")
    public ResponseEntity<CustomResponse<PaginationResponseDto<PedidoTimeResponseDto>>> getTimePedidos(
            @Valid PaginationRequestDto paginationRequest,
            @RequestParam Long restauranteId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetail userDetails
    ) {
        CustomResponse<PaginationResponseDto<PedidoTimeResponseDto>> response = CustomResponse.<PaginationResponseDto<PedidoTimeResponseDto>>builder()
                .status(HttpStatus.OK.value())
                .data(pedidoHandler.getTimePedidos(
                        userDetails.getId(),
                        restauranteId,
                        paginationRequest
                ))
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Ranking de eficiencia de empleados",
            description = "Muestra el tiempo medio de entrega por empleado para un restaurante. **Solo accesible por PROPIETARIO.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ranking obtenido exitosamente")
    })
    @PreAuthorize("hasRole('PROPIETARIO')")
    @GetMapping("/trazabilidad/empleados")
    public ResponseEntity<CustomResponse<PaginationResponseDto<EmpleadoTiempoResponseDto>>> getTimeEmpleados(
            @Valid PaginationRequestDto paginationRequest,
            @RequestParam Long restauranteId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetail userDetails
    ) {
        CustomResponse<PaginationResponseDto<EmpleadoTiempoResponseDto>> response = CustomResponse.<PaginationResponseDto<EmpleadoTiempoResponseDto>>builder()
                .status(HttpStatus.OK.value())
                .data(pedidoHandler.getTimeEmpleados(
                        userDetails.getId(),
                        restauranteId,
                        paginationRequest
                ))
                .build();

        return ResponseEntity.ok(response);
    }

}
