package ru.practicum.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.warn("404 {}", e.getMessage());
        return buildApiError(HttpStatus.NOT_FOUND, "Not found exception", e);
    }

    @ExceptionHandler(ConflictException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        log.warn("409 {}", e.getMessage());
        return buildApiError(HttpStatus.CONFLICT, "Conflict", e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("400 {}", e.getMessage());
        return buildApiError(HttpStatus.BAD_REQUEST, "Argument not valid", e);
    }

    @ExceptionHandler(ValidationException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException e) {
        log.warn("400 {}", e.getMessage());
        return buildApiError(HttpStatus.BAD_REQUEST, "Parameter not valid", e);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingParamException(final MissingServletRequestParameterException e) {
        log.warn("400 {}", e.getMessage());
        return buildApiError(HttpStatus.BAD_REQUEST, "Required request parameter not found", e);
    }

    @ExceptionHandler(Exception.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleInternalServerException(final Exception e) {
        log.error("500 {} {}", e.getClass(), e.getMessage());
        return buildApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
    }

    private ApiError buildApiError(HttpStatus status, String reason, Exception e) {
        return ApiError.builder()
                .status(status)
                .reason(reason)
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(Arrays.asList(e.getStackTrace()))
                .build();
    }
}

