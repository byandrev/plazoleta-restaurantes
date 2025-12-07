package com.pragma.powerup.infrastructure.exceptionhandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.infrastructure.exception.InfraException;
import com.pragma.powerup.infrastructure.exception.ValidationError;
import com.pragma.powerup.infrastructure.input.rest.response.CustomResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<CustomResponse<Void>> handleDomainException(DomainException ex) {
        CustomResponse<Void> response = CustomResponse.<Void>builder()
                .status(HttpStatus.CONFLICT.value())
                .error(ex.getMessage())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(response);
    }

    @ExceptionHandler(InfraException.class)
    public ResponseEntity<CustomResponse<Void>> handleInfraException(InfraException ex) {
        CustomResponse<Void> response = CustomResponse.<Void>builder()
                .status(ex.getCode())
                .error(ex.getMessage())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(ex.getCode()).body(response);
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
                .error(ExceptionResponse.BAD_REQUEST.getMessage())
                .message(ExceptionResponse.VALIDATION_ERROR.getMessage())
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CustomResponse<Void>> handleConstraintException(ConstraintViolationException ex) {
        List<ValidationError> errors = ex.getConstraintViolations().stream()
                .map(error -> new ValidationError(
                        error.getPropertyPath().toString(),
                        error.getMessage(),
                        error.getInvalidValue()
                ))
                .collect(Collectors.toList());

        CustomResponse<Void> response = CustomResponse.<Void>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ExceptionResponse.BAD_REQUEST.getMessage())
                .message(ExceptionResponse.VALIDATION_ERROR.getMessage())
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido";
        String message = String.format("Tipo de argumento inválido para '%s'. Se esperaba: %s.", ex.getName(), expectedType);

        log.warn(message);
        CustomResponse<Void> response = CustomResponse.<Void>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ExceptionResponse.BAD_REQUEST.getMessage())
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        List<ValidationError> errors = new ArrayList<>();

        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) ex.getCause();

            String field = ife.getPath().stream()
                    .map(p -> p.getFieldName())
                    .filter(s -> s != null)
                    .reduce((a, b) -> a + "." + b)
                    .orElse("Campo desconocido");

            String errorDetail = String.format("El campo '%s' no es valido. Valor recibido: '%s'", field, ife.getValue());

            errors.add(new ValidationError(
                    field,
                    errorDetail,
                    ife.getValue()
            ));
        }

        CustomResponse<Void> response = CustomResponse.<Void>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ExceptionResponse.BAD_REQUEST.getMessage())
                .message(ExceptionResponse.JSON_ERROR.getMessage())
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CustomResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String message = String.format("Parámetro requerido faltante: '%s'.", ex.getParameterName());

        log.warn(message);
        CustomResponse<Void> response = CustomResponse.<Void>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ExceptionResponse.BAD_REQUEST.getMessage())
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<CustomResponse<Void>> handleHttpMediaTypeNotSupportedException() {
        CustomResponse<Void> response = CustomResponse.<Void>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ExceptionResponse.BAD_REQUEST.getMessage())
                .message(ExceptionResponse.MEDIA_TYPE_IS_NOT_SUPPORTED.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomResponse<Void>> handleDataIntegrityViolationException() {
        CustomResponse<Void> response = CustomResponse.<Void>builder()
                .status(HttpStatus.CONFLICT.value())
                .error(ExceptionResponse.CONFLICT.getMessage())
                .message(ExceptionResponse.DUPLICATE_DATA.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthenticationException.class})
    public void handleSecurityExceptions(RuntimeException ex) {
        throw ex;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CustomResponse<Void>> handleGlobalException(RuntimeException ex) {
        CustomResponse<Void> response = CustomResponse.<Void>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(ExceptionResponse.SERVER_ERROR.getMessage())
                .message(ExceptionResponse.SERVER_ERROR.getMessage())
                .build();

        log.error(ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}