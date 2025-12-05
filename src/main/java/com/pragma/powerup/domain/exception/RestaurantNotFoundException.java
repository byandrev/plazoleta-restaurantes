package com.pragma.powerup.domain.exception;

import org.springframework.http.HttpStatus;

public class RestaurantNotFoundException extends DomainException {
    public RestaurantNotFoundException() {
        super("El restaurante no existe", HttpStatus.NOT_FOUND.value());
    }
}
