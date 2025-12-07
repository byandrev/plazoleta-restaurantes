package com.pragma.powerup.infrastructure.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedUserException extends InfraException {

    public UnauthorizedUserException(String message) {
        super("No estas autorizado para esta tarea", HttpStatus.FORBIDDEN.value());
    }

}
