package com.pragma.powerup.infrastructure.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFound extends InfraException {
    public ResourceNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND.value());
    }
}
