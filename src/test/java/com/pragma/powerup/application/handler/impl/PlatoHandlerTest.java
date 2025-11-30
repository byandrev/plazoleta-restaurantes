package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.PlatoRequestDto;
import com.pragma.powerup.application.dto.request.PlatoUpdateDto;
import com.pragma.powerup.application.dto.response.PlatoResponseDto;
import com.pragma.powerup.application.mapper.IPlatoRequestMapper;
import com.pragma.powerup.application.mapper.IPlatoResponseMapper;
import com.pragma.powerup.domain.api.IPlatoServicePort;
import com.pragma.powerup.domain.model.PlatoModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatoHandlerTest {

    @Mock
    private IPlatoServicePort platoService;

    @Mock
    private IPlatoRequestMapper  platoRequestMapper;

    @Mock
    private IPlatoResponseMapper platoResponseMapper;

    @InjectMocks
    private  PlatoHandler platoHandler;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void save_IsDefaultActivo() {
        PlatoModel platoModel = new PlatoModel();
        platoModel.setId(1L);
        platoModel.setNombre("Plato");

        when(platoService.save(platoModel)).thenReturn(platoModel);

        PlatoModel currentPlato = platoService.save(platoModel);

        assertEquals(platoModel.getNombre(), currentPlato.getNombre());
    }

    @Test
    void getById() {
        PlatoModel platoModel = new PlatoModel();
        platoModel.setId(1L);
        platoModel.setNombre("Plato");

        PlatoResponseDto expectedPlato = new PlatoResponseDto();
        expectedPlato.setId(1L);
        expectedPlato.setNombre("Plato");

        when(platoService.getById(1L)).thenReturn(platoModel);
        when(platoResponseMapper.toResponse(platoModel)).thenReturn(expectedPlato);

        PlatoResponseDto currentPlato = platoHandler.getById(1L);

        assertEquals(platoModel.getNombre(), currentPlato.getNombre());

        verify(platoService).getById(1L);
    }

    @Test
    void savePlatoSuccessfully() {
        PlatoRequestDto platoRequestDto = new PlatoRequestDto();
        platoRequestDto.setNombre("Test");
        platoRequestDto.setDescripcion("Test");
        platoRequestDto.setCategoria("test");
        platoRequestDto.setPrecio(1000);
        platoRequestDto.setUrlImagen("https://test.png");
        platoRequestDto.setIdRestaurante(1L);

        Set<ConstraintViolation<PlatoRequestDto>> violations = validator.validate(platoRequestDto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void savePlatoBlankNombreFailsValidation() {
        PlatoRequestDto platoRequestDto = new PlatoRequestDto();
        platoRequestDto.setNombre("");
        platoRequestDto.setDescripcion("Test");
        platoRequestDto.setCategoria("test");
        platoRequestDto.setPrecio(1000);
        platoRequestDto.setUrlImagen("https://test.png");
        platoRequestDto.setIdRestaurante(1L);

        Set<ConstraintViolation<PlatoRequestDto>> violations = validator.validate(platoRequestDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<PlatoRequestDto> violation = violations.iterator().next();
        assertEquals("El nombre no puede estar vacio", violation.getMessage());
    }

    @Test
    void savePlatoPriceZeroFailsValidation() {
        PlatoRequestDto platoRequestDto = new PlatoRequestDto();
        platoRequestDto.setNombre("Test");
        platoRequestDto.setDescripcion("Test");
        platoRequestDto.setCategoria("test");
        platoRequestDto.setPrecio(0);
        platoRequestDto.setUrlImagen("https://test.png");
        platoRequestDto.setIdRestaurante(1L);

        Set<ConstraintViolation<PlatoRequestDto>> violations = validator.validate(platoRequestDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<PlatoRequestDto> violation = violations.iterator().next();
        assertEquals("El precio no puede ser menor a 1", violation.getMessage());
    }

    @Test
    void savePlatoPriceNegativeFailsValidation() {
        PlatoRequestDto platoRequestDto = new PlatoRequestDto();
        platoRequestDto.setNombre("Test");
        platoRequestDto.setDescripcion("Test");
        platoRequestDto.setCategoria("test");
        platoRequestDto.setPrecio(-1000);
        platoRequestDto.setUrlImagen("https://test.png");
        platoRequestDto.setIdRestaurante(1L);

        Set<ConstraintViolation<PlatoRequestDto>> violations = validator.validate(platoRequestDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<PlatoRequestDto> violation = violations.iterator().next();
        assertEquals("El precio no puede ser menor a 1", violation.getMessage());
    }

    @Test
    void savePlatoBlankCategoriaFailsValidation() {
        PlatoRequestDto platoRequestDto = new PlatoRequestDto();
        platoRequestDto.setNombre("Test");
        platoRequestDto.setDescripcion("Test");
        platoRequestDto.setCategoria("");
        platoRequestDto.setPrecio(1000);
        platoRequestDto.setUrlImagen("https://test.png");
        platoRequestDto.setIdRestaurante(1L);

        Set<ConstraintViolation<PlatoRequestDto>> violations = validator.validate(platoRequestDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<PlatoRequestDto> violation = violations.iterator().next();
        assertEquals("La categoria no puede estar vacia", violation.getMessage());
    }

    @Test
    void updatePlatoBlankDescripcionFailsValidation() {
        PlatoUpdateDto platoUpdateDto = new PlatoUpdateDto();
        platoUpdateDto.setDescripcion("");
        platoUpdateDto.setPrecio(1000);

        Set<ConstraintViolation<PlatoUpdateDto>> violations = validator.validate(platoUpdateDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<PlatoUpdateDto> violation = violations.iterator().next();
        assertEquals("La descripcion no puede estar vacia", violation.getMessage());
    }

    @Test
    void updatePlatoNegativePriceFailsValidation() {
        PlatoUpdateDto platoUpdateDto = new PlatoUpdateDto();
        platoUpdateDto.setDescripcion("Test");
        platoUpdateDto.setPrecio(-1000);

        Set<ConstraintViolation<PlatoUpdateDto>> violations = validator.validate(platoUpdateDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<PlatoUpdateDto> violation = violations.iterator().next();
        assertEquals("El precio no puede ser menor a 1", violation.getMessage());
    }
}