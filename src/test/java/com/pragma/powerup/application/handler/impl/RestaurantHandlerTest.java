package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.mapper.IRestaurantRequestMapper;
import com.pragma.powerup.application.mapper.IRestaurantResponseMapper;
import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.model.RestaurantModel;
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantHandlerTest {

    @Mock
    private IRestaurantServicePort restaurantService;

    @Mock
    private IRestaurantRequestMapper restaurantRequestMapper;

    @Mock
    private IRestaurantResponseMapper restaurantResponseMapper;

    @InjectMocks
    private RestaurantHandler restaurantHandler;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void getAllRestaurants() {
        RestaurantModel restaurantModel1 = new RestaurantModel();
        restaurantModel1.setId(1L);
        restaurantModel1.setNombre("Restaurant 1");
        RestaurantModel restaurantModel2 = new RestaurantModel();
        restaurantModel2.setId(2L);
        restaurantModel2.setNombre("Restaurant 2");
        List<RestaurantModel> restaurantModels = Arrays.asList(restaurantModel1, restaurantModel2);

        RestaurantResponseDto restaurantResponseDto1 = new RestaurantResponseDto();
        restaurantResponseDto1.setId(1L);
        restaurantResponseDto1.setNombre("Restaurant 1");
        RestaurantResponseDto restaurantResponseDto2 = new RestaurantResponseDto();
        restaurantResponseDto2.setId(2L);
        restaurantResponseDto2.setNombre("Restaurant 2");
        List<RestaurantResponseDto> expectedResponse = Arrays.asList(restaurantResponseDto1, restaurantResponseDto2);

        when(restaurantService.getAll()).thenReturn(restaurantModels);
        when(restaurantResponseMapper.toResponseList(restaurantModels)).thenReturn(expectedResponse);

        List<RestaurantResponseDto> actualResponse = restaurantHandler.getAll();

        assertEquals(expectedResponse.size(), actualResponse.size());
        assertEquals(expectedResponse.get(0).getNombre(), actualResponse.get(0).getNombre());
        assertEquals(expectedResponse.get(1).getNombre(), actualResponse.get(1).getNombre());
        verify(restaurantService).getAll();
        verify(restaurantResponseMapper).toResponseList(restaurantModels);
    }


    @Test
    void saveRestaurantWithNumericNameFailsValidation() {
        RestaurantRequestDto restaurantRequestDto = new RestaurantRequestDto();
        restaurantRequestDto.setNombre("12345");
        restaurantRequestDto.setDireccion("Av 123");
        restaurantRequestDto.setTelefono("+573001234567");
        restaurantRequestDto.setUrlLogo("http://logo.com/logo.png");
        restaurantRequestDto.setNit("123456789");
        restaurantRequestDto.setIdPropietario(1L);

        Set<ConstraintViolation<RestaurantRequestDto>> violations = validator.validate(restaurantRequestDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<RestaurantRequestDto> violation = violations.iterator().next();
        assertEquals("El nombre no puede estar compuesto solo por números", violation.getMessage());
        assertEquals("nombre", violation.getPropertyPath().toString());
    }

    @Test
    void saveRestaurantWithBlankNitFailsValidation() {
        RestaurantRequestDto restaurantRequestDto = new RestaurantRequestDto();
        restaurantRequestDto.setNombre("Comercial");
        restaurantRequestDto.setDireccion("Av 123");
        restaurantRequestDto.setTelefono("+573001234567");
        restaurantRequestDto.setUrlLogo("http://logo.com/logo.png");
        // restaurantRequestDto.setNit("");
        restaurantRequestDto.setIdPropietario(1L);

        Set<ConstraintViolation<RestaurantRequestDto>> violations = validator.validate(restaurantRequestDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<RestaurantRequestDto> violation = violations.iterator().next();
        assertEquals("El NIT no puede estar vacio", violation.getMessage());
        assertEquals("nit", violation.getPropertyPath().toString());
    }

}
