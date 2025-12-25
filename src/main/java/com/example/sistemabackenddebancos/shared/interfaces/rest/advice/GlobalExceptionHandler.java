package com.example.sistemabackenddebancos.shared.interfaces.rest.advice;

import com.example.sistemabackenddebancos.shared.domain.exceptions.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ApiError handleDomain(DomainException ex, HttpServletRequest req) {
        return new ApiError(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "DomainError",
                ex.getMessage(),
                req.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ApiError handleGeneric(Exception ex, HttpServletRequest req) {
        return new ApiError(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "InternalServerError",
                ex.getMessage(),
                req.getRequestURI()
        );
    }
}