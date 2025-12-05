package com.pragma.powerup.domain.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedUserException extends DomainException {
    public UnauthorizedUserException(String message) {
        super("No estas autorizado para esta tarea", HttpStatus.FORBIDDEN.value());
    }
}
