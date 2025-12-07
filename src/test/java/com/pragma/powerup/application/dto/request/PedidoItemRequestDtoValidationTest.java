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
class PedidoItemRequestDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private PedidoItemRequestDto createValidPedidoItemDto() {
        return PedidoItemRequestDto
                .builder()
                .platoId(1L)
                .cantidad(1)
                .build();
    }

    private void assertViolation(PedidoItemRequestDto dto, String expectedMessage) {
        Set<ConstraintViolation<PedidoItemRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Debería haber violaciones de validación.");

        boolean found = violations.stream()
                .anyMatch(v -> v.getMessage().equals(expectedMessage));

        assertTrue(found, "No se encontró el mensaje de violación esperado: " + expectedMessage);
    }

    @Test
    @DisplayName("Pedido válido debe pasar la validación")
    void saveValidate_Success() {
        PedidoItemRequestDto dto = createValidPedidoItemDto();
        Set<ConstraintViolation<PedidoItemRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "No deberían existir violaciones con datos válidos.");
    }

    @Test
    @DisplayName("Cantidad vacía debe fallar la validación")
    void saveValidateCantidad_Empty_FailsValidation() {
        PedidoItemRequestDto dto = createValidPedidoItemDto();
        dto.setCantidad(null);
        assertViolation(dto, "La cantidad no puede estar vacia");
    }

    @Test
    @DisplayName("Cantidad negativa debe fallar la validación")
    void saveValidateCantidad_Negative_FailsValidation() {
        PedidoItemRequestDto dto = createValidPedidoItemDto();
        dto.setCantidad(-10);
        assertViolation(dto, "Se necesita minimo un plato para hacer un pedido");
    }

    @Test
    @DisplayName("El platoId vacío debe fallar la validación")
    void saveValidateIdCliente_Empty_FailsValidation() {
        PedidoItemRequestDto dto = createValidPedidoItemDto();
        dto.setPlatoId(null);
        assertViolation(dto, "Se nececita el id del plato para hacer el pedido");
    }


}
