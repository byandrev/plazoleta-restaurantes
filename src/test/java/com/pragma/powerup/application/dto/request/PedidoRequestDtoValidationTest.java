package com.pragma.powerup.application.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class PedidoRequestDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private PedidoRequestDto createValidPedidoDto() {
        return PedidoRequestDto
                .builder()
                .idRestaurante(1L)
                .items(Set.of(PedidoItemRequestDto.builder().platoId(10L).cantidad(2).build()))
                .build();
    }

    private void assertViolation(PedidoRequestDto dto, String expectedMessage) {
        Set<ConstraintViolation<PedidoRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Debería haber violaciones de validación.");

        boolean found = violations.stream()
                .anyMatch(v -> v.getMessage().equals(expectedMessage));

        assertTrue(found, "No se encontró el mensaje de violación esperado: " + expectedMessage);
    }

    @Test
    @DisplayName("Pedido válido debe pasar la validación")
    void saveValidate_Success() {
        PedidoRequestDto dto = createValidPedidoDto();
        Set<ConstraintViolation<PedidoRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "No deberían existir violaciones con datos válidos.");
    }

    @Test
    @DisplayName("idRestaurante vacío debe fallar la validación")
    void saveValidateIdRestaurante_Empty_FailsValidation() {
        PedidoRequestDto dto = createValidPedidoDto();
        dto.setIdRestaurante(null);
        assertViolation(dto, "El idRestaurante no puede estar vacio");
    }

    @Test
    @DisplayName("items vacío debe fallar la validación")
    void saveValidateItems_Empty_FailsValidation() {
        PedidoRequestDto dto = createValidPedidoDto();
        dto.setItems(null);
        assertViolation(dto, "Se necesita minimo un plato para hacer un pedido");
    }

}
