package com.pragma.powerup.domain.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
    private final int code;

    public DomainException(String message, int code) {
        super(message);
        this.code = code;
    }
}
