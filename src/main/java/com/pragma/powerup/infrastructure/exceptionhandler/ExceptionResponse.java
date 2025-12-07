package com.pragma.powerup.infrastructure.exceptionhandler;

public enum ExceptionResponse {
    NO_DATA_FOUND("No data found for the requested petition"),
    SERVER_ERROR("Internal Server Error"),
    DUPLICATE_DATA("La data esta duplicada"),
    BAD_REQUEST("Bad Request"),
    UNAUTHORIZED("Unauthorized"),
    CONFLICT("Conflict"),
    VALIDATION_ERROR("Error en el formato de los datos"),
    JSON_ERROR("Error de formato de JSON"),
    MEDIA_TYPE_IS_NOT_SUPPORTED("El MediaType no es soportado"),;

    private final String message;

    ExceptionResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}