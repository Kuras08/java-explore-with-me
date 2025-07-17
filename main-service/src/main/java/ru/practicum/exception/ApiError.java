package ru.practicum.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private HttpStatus status;
    private String reason;
    private String message;
    private LocalDateTime timestamp;
    private List<StackTraceElement> errors;
}

