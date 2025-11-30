package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.PlatoRequestDto;
import com.pragma.powerup.application.dto.response.PlatoResponseDto;
import com.pragma.powerup.application.handler.IPlatoHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/platos")
@RequiredArgsConstructor
public class PlatoRestController {

    private final IPlatoHandler platoHandler;

    @Operation(summary = "Add new plato")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plato created", content = @Content),
            @ApiResponse(responseCode = "409", description = "Plato already exists", content = @Content)
    })
    @PostMapping("/")
    public ResponseEntity<Void> savePlato(@Valid @RequestBody PlatoRequestDto platoRequestDto) {
        platoHandler.save(platoRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Get plato by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plato returned",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PlatoResponseDto.class)))),
            @ApiResponse(responseCode = "404", description = "No data found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PlatoResponseDto> getPlatoById(@PathVariable Long id) {
        return ResponseEntity.ok(platoHandler.getById(id));
    }

}
