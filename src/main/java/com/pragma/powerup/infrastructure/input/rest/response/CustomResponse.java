package com.pragma.powerup.infrastructure.input.rest.response;

import com.pragma.powerup.infrastructure.exception.ValidationError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomResponse<T> {

    private int status;

    private String error;

    private T data;

    private String message;

    private List<ValidationError> errors;

}
