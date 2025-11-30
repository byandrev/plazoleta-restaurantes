package com.pragma.powerup.infrastructure.exceptionhandler;

import com.pragma.powerup.domain.exception.UnauthorizedUserException;
import com.pragma.powerup.infrastructure.input.rest.response.CustomResponse;
import com.pragma.powerup.infrastructure.exception.NoDataFoundException;
import com.pragma.powerup.infrastructure.exception.ValidationError;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerAdvisor {

    private static final String MESSAGE = "message";

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<CustomResponse> handleNoDataFoundException(NoDataFoundException ignoredNoDataFoundException) {
        CustomResponse response = CustomResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ignoredNoDataFoundException.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse> handleValidationException(MethodArgumentNotValidException ex) {

        List<ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()
                ))
                .collect(Collectors.toList());

        CustomResponse response = CustomResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Error de validación")
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<CustomResponse> handleFeignException(FeignException.NotFound ignoredFeignException) {
        CustomResponse response = CustomResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ExceptionResponse.NO_DATA_FOUND.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<CustomResponse> handleFeignException(FeignException ignoredFeignException) {
        CustomResponse response = CustomResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ExceptionResponse.SERVER_ERROR.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<CustomResponse> handleUnauthorizedUserException(UnauthorizedUserException unauthorizedUserException) {
        CustomResponse response = CustomResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Unauthorized")
                .message(unauthorizedUserException.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}