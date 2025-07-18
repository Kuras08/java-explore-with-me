package ru.practicum.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.warn("404 {}", e.getMessage());
        return buildApiError(HttpStatus.NOT_FOUND, "Not found exception", e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        log.warn("409 {}", e.getMessage());
        return buildApiError(HttpStatus.CONFLICT, "Conflict", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("400 {}", message);
        return buildApiError(HttpStatus.BAD_REQUEST, "Argument not valid", message);
    }

    @ExceptionHandler(ValidationException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException e) {
        log.warn("400 {}", e.getMessage());
        return buildApiError(HttpStatus.BAD_REQUEST, "Parameter not valid", e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingParamException(final MissingServletRequestParameterException e) {
        log.warn("400 {}", e.getMessage());
        return buildApiError(HttpStatus.BAD_REQUEST, "Required request parameter not found", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleInternalServerException(final Exception e) {
        log.error("500 {} {}", e.getClass(), e.getMessage(), e);
        return buildApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.warn("409 {}", e.getMessage());
        return buildApiError(HttpStatus.CONFLICT, "Conflict", e.getMessage());
    }

    private ApiError buildApiError(HttpStatus status, String reason, String message) {
        return ApiError.builder()
                .status(status.name())
                .reason(reason)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

