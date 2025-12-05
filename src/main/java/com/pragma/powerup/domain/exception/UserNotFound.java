package com.pragma.powerup.domain.exception;

import org.springframework.http.HttpStatus;

public class UserNotFound extends DomainException {
    public UserNotFound() {
        super("El usuario no existe", HttpStatus.NOT_FOUND.value());
    }
}
