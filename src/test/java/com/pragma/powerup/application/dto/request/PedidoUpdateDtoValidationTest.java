package com.pragma.powerup.application.dto.request;

import com.pragma.powerup.domain.model.PedidoEstado;
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
class PedidoUpdateDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private PedidoUpdateDto createValidPedidoDto() {
        return PedidoUpdateDto
                .builder()
                .id(1L)
                .estado(PedidoEstado.EN_PREPARACION)
                .build();
    }

    private void assertViolation(PedidoUpdateDto dto, String expectedMessage) {
        Set<ConstraintViolation<PedidoUpdateDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Debería haber violaciones de validación.");

        boolean found = violations.stream()
                .anyMatch(v -> v.getMessage().equals(expectedMessage));

        assertTrue(found, "No se encontró el mensaje de violación esperado: " + expectedMessage);
    }

    @Test
    @DisplayName("Pedido válido debe pasar la validación")
    void saveValidate_Success() {
        PedidoUpdateDto dto = createValidPedidoDto();
        Set<ConstraintViolation<PedidoUpdateDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "No deberían existir violaciones con datos válidos.");
    }

    @Test
    @DisplayName("El estado vacío debe fallar la validación")
    void saveValidateEstado_Empty_FailsValidation() {
        PedidoUpdateDto dto = createValidPedidoDto();
        dto.setEstado(null);
        assertViolation(dto, "El estado no puede estar vacio");
    }

    @Test
    @DisplayName("El tamaño del PIN menor que 6 debe fallar la validación")
    void saveValidatePin_MinSize_FailsValidation() {
        PedidoUpdateDto dto = createValidPedidoDto();
        dto.setPin("123");
        assertViolation(dto, "El tamaño del PIN debe ser 6 digitos");
    }

    @Test
    @DisplayName("El tamaño del PIN mayor que 6 debe fallar la validación")
    void saveValidatePin_MaxSize_FailsValidation() {
        PedidoUpdateDto dto = createValidPedidoDto();
        dto.setPin("1234567");
        assertViolation(dto, "El tamaño del PIN debe ser 6 digitos");
    }

}
