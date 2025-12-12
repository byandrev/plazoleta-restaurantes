package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.PedidoRequestDto;
import com.pragma.powerup.application.dto.response.PaginationResponseDto;
import com.pragma.powerup.application.dto.response.PedidoResponseDto;
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
    public ResponseEntity<CustomResponse<PaginationResponseDto<PedidoResponseDto>>> getRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) PedidoEstado estado,
            @PathVariable Long restaurantId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetail userDetails
    ) {
        Long userId = userDetails.getId();

        CustomResponse<PaginationResponseDto<PedidoResponseDto>> response = CustomResponse.<PaginationResponseDto<PedidoResponseDto>>builder()
                .status(HttpStatus.OK.value())
                .data(pedidoHandler.getAll(userId, restaurantId, estado, page, size))
                .build();

        return ResponseEntity.ok(response);
    }

}
