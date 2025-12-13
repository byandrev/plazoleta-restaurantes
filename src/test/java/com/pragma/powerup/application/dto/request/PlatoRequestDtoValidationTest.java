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
class PlatoRequestDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private PlatoRequestDto createValidPlatoDto() {
        return PlatoRequestDto
                .builder()
                .nombre("Burger")
                .descripcion("Tests")
                .categoria("Categoria")
                .urlImagen("https://logo.png")
                .precio(100)
                .idRestaurante(1L)
                .build();
    }

    private void assertViolation(PlatoRequestDto dto, String expectedMessage) {
        Set<ConstraintViolation<PlatoRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Debería haber violaciones de validación.");

        boolean found = violations.stream()
                .anyMatch(v -> v.getMessage().equals(expectedMessage));

        assertTrue(found, "No se encontró el mensaje de violación esperado: " + expectedMessage);
    }

    @Test
    @DisplayName("Plato válido debe pasar la validación")
    void saveValidate_Success() {
        PlatoRequestDto dto = createValidPlatoDto();
        Set<ConstraintViolation<PlatoRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "No deberían existir violaciones con datos válidos.");
    }

    @Test
    @DisplayName("Nombre vacío debe fallar la validación")
    void saveValidateName_Empty_FailsValidation() {
        PlatoRequestDto dto = createValidPlatoDto();
        dto.setNombre("");
        assertViolation(dto, "El nombre no puede estar vacio");
    }

    @Test
    @DisplayName("Precio vacío debe fallar la validación")
    void saveValidatePrice_Empty_FailsValidation() {
        PlatoRequestDto dto = createValidPlatoDto();
        dto.setPrecio(null);
        assertViolation(dto, "El precio no puede estar vacio");
    }

    @Test
    @DisplayName("Precio negativo debe fallar la validación")
    void saveValidatePriceNegative_Empty_FailsValidation() {
        PlatoRequestDto dto = createValidPlatoDto();
        dto.setPrecio(-100);
        assertViolation(dto, "El precio no puede ser menor a 1");
    }

    @Test
    @DisplayName("Categoria vacía debe fallar la validación")
    void saveValidateCategoria_Empty_FailsValidation() {
        PlatoRequestDto dto = createValidPlatoDto();
        dto.setCategoria("");
        assertViolation(dto, "La categoria no puede estar vacia");
    }

    @Test
    @DisplayName("URL imagen vacía debe fallar la validación")
    void saveValidateUrlImage_InvalidFormat_FailsValidation() {
        PlatoRequestDto dto = createValidPlatoDto();
        dto.setUrlImagen("");
        assertViolation(dto, "La url_imagen no puede estar vacia");
    }

}
