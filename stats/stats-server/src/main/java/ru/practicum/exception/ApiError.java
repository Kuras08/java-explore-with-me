package ru.practicum.exception;

public class ApiError {
    String error;

    public ApiError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}