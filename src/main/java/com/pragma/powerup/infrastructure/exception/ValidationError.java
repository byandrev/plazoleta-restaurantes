package com.pragma.powerup.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationError {

    private String field;

    private String message;

    private Object rejectedValue;

}
