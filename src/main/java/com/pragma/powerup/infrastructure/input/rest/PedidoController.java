package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.PaginationRequestDto;
import com.pragma.powerup.application.dto.request.PedidoRequestDto;
import com.pragma.powerup.application.dto.request.PedidoUpdateDto;
import com.pragma.powerup.application.dto.response.PaginationResponseDto;
import com.pragma.powerup.application.dto.response.PedidoResponseDto;
import com.pragma.powerup.application.dto.response.PedidoTimeResponseDto;
import com.pragma.powerup.application.dto.response.TraceabilityResponseDto;
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
public class PedidoController {

    private final IPedidoHandler pedidoHandler;

    @Operation(summary = "Add new pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido created", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict to create pedido", content = @Content)
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

    @Operation(summary = "Get pedidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos returned")
    })
    @PreAuthorize("hasRole('EMPLEADO')")
    @GetMapping("/{restaurantId}")
    public ResponseEntity<CustomResponse<PaginationResponseDto<PedidoResponseDto>>> getPedidos(
            @Valid PaginationRequestDto paginationRequest,
            @RequestParam(required = false) PedidoEstado estado,
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

    @Operation(summary = "Update pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido updated", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict to update pedido", content = @Content)
    })
    @PatchMapping("/{pedidoId}")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<Void> updatePedido(
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

    @Operation(summary = "Cancel pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido canceled", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict to cancel pedido", content = @Content)
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

    @Operation(summary = "Get historial del pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial returned")
    })
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/trazabilidad/{pedidoId}")
    public ResponseEntity<CustomResponse<List<TraceabilityResponseDto>>> getHistorial(
            @PathVariable Long pedidoId
    ) {
        CustomResponse<List<TraceabilityResponseDto>> response = CustomResponse.<List<TraceabilityResponseDto>>builder()
                .status(HttpStatus.OK.value())
                .data(pedidoHandler.getHistory(pedidoId))
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get tiempo de los pedidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tiempo de los pedidos")
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

}
