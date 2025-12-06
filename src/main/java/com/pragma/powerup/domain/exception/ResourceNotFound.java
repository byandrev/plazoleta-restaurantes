package com.pragma.powerup.domain.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFound extends DomainException {
    public ResourceNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND.value());
    }
}
