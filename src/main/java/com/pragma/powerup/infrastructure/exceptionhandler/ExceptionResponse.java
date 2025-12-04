package com.pragma.powerup.infrastructure.exceptionhandler;

public enum ExceptionResponse {
    NO_DATA_FOUND("No data found for the requested petition"),
    SERVER_ERROR("Internal Server Error"),
    DUPLICATE_DATA("Duplicate data"),
    BAD_REQUEST("Bad Request"),
    UNAUTHORIZED("Unauthorized"),
    CONFLICT("Conflict");

    private final String message;

    ExceptionResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}