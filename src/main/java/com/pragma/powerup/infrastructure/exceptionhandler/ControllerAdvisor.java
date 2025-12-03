package com.pragma.powerup.infrastructure.exceptionhandler;

import com.pragma.powerup.domain.exception.UnauthorizedUserException;
import com.pragma.powerup.infrastructure.exception.NoDataFoundException;
import com.pragma.powerup.infrastructure.exception.ValidationError;
import com.pragma.powerup.infrastructure.input.rest.response.CustomResponse;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<CustomResponse<Void>> handleNoDataFoundException(NoDataFoundException ignoredNoDataFoundException) {
        CustomResponse<Void> response = CustomResponse.<Void>builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ignoredNoDataFoundException.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()
                ))
                .collect(Collectors.toList());

        CustomResponse<Void> response = CustomResponse.<Void>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Error de validación")
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<CustomResponse<Void>> handleFeignException(FeignException.NotFound ignoredFeignException) {
        CustomResponse<Void> response = CustomResponse.<Void>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ExceptionResponse.NO_DATA_FOUND.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<CustomResponse<Void>> handleFeignException(FeignException ignoredFeignException) {
        CustomResponse<Void> response = CustomResponse.<Void>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ExceptionResponse.SERVER_ERROR.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<CustomResponse<Void>> handleUnauthorizedUserException(UnauthorizedUserException unauthorizedUserException) {
        CustomResponse<Void> response = CustomResponse.<Void>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Unauthorized")
                .message(unauthorizedUserException.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomResponse<Void>> handleHttpMessageNotReadableException() {
        CustomResponse<Void> response = CustomResponse.<Void>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Es necesario el body")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<CustomResponse<Void>> handleHttpMediaTypeNotSupportedException() {
        CustomResponse<Void> response = CustomResponse.<Void>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("MediaType no soportado")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}