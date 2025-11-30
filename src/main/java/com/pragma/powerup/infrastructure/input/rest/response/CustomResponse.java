package com.pragma.powerup.infrastructure.input.rest.response;

import com.pragma.powerup.infrastructure.exception.ValidationError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.mapping.Any;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomResponse {

    private int status;

    private String error;

    private Object data;

    private String message;

    private String path;

    private List<ValidationError> errors;

}
