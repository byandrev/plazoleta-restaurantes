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
class RestauranteRequestDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private RestaurantRequestDto createValidRestauranteDto() {
        return RestaurantRequestDto
                .builder()
                .nombre("Restaurante")
                .direccion("Av 123")
                .telefono("+573001234567")
                .urlLogo("https://logo.png")
                .nit("123456789")
                .build();
    }

    private void assertViolation(RestaurantRequestDto dto, String expectedMessage) {
        Set<ConstraintViolation<RestaurantRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Debería haber violaciones de validación.");

        boolean found = violations.stream()
                .anyMatch(v -> v.getMessage().equals(expectedMessage));

        assertTrue(found, "No se encontró el mensaje de violación esperado: " + expectedMessage);
    }

    @Test
    @DisplayName("Restaurante válido debe pasar la validación")
    void saveValidate_Success() {
        RestaurantRequestDto dto = createValidRestauranteDto();
        Set<ConstraintViolation<RestaurantRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "No deberían existir violaciones con datos válidos.");
    }

    @Test
    @DisplayName("NIT vacío debe fallar la validación")
    void saveValidateNit_Empty_FailsValidation() {
        RestaurantRequestDto dto = createValidRestauranteDto();
        dto.setNit("");
        assertViolation(dto, "El NIT no puede estar vacio");
    }

    @Test
    @DisplayName("Telefono vacío debe fallar la validación")
    void saveValidateTelefono_Empty_FailsValidation() {
        RestaurantRequestDto dto = createValidRestauranteDto();
        dto.setTelefono("");
        assertViolation(dto, "El telefono no puede estar vacio");
    }

    @Test
    @DisplayName("Telefono con formato invalido debe fallar la validación")
    void saveValidateTelefono_InvalidFormat_FailsValidation() {
        RestaurantRequestDto dto = createValidRestauranteDto();
        dto.setTelefono("-12345");
        assertViolation(dto, "Teléfono inválido. Máx 13 caracteres y puede iniciar con +");
    }

    @Test
    @DisplayName("Telefono con max length debe fallar la validación")
    void saveValidateTelefono_MaxLength_FailsValidation() {
        RestaurantRequestDto dto = createValidRestauranteDto();
        dto.setTelefono("12345678912345");
        assertViolation(dto, "Teléfono inválido. Máx 13 caracteres y puede iniciar con +");
    }

}
