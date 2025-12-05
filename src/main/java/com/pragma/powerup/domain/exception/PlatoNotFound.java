package com.pragma.powerup.domain.exception;

import org.springframework.http.HttpStatus;

public class PlatoNotFound extends DomainException {
    public PlatoNotFound() {
        super("El plato no existe", HttpStatus.NOT_FOUND.value());
    }
}
