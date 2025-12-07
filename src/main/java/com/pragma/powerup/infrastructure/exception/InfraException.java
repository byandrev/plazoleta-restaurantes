package com.pragma.powerup.infrastructure.exception;

import lombok.Getter;

@Getter
public class InfraException extends RuntimeException {
    private final int code;

    public InfraException(String message, int code) {
        super(message);
        this.code = code;
    }

}
