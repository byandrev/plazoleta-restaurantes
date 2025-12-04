package com.pragma.powerup.infrastructure.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.powerup.infrastructure.input.rest.response.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        CustomResponse<?> customResponse = CustomResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("No estás autenticado o tu token es inválido")
                .error("Unauthorized")
                .build();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(customResponse));
    }
}